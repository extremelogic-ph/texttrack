/*
 * The MIT License
 *
 * Copyright 2016-2017 Twitch Interactive, Inc. or its affiliates. All Rights Reserved.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package ph.extremelogic.libcaption.caption;

import ph.extremelogic.texttrack.utils.Debug;

import static ph.extremelogic.libcaption.caption.caption_header.SCREEN_COLS;
import static ph.extremelogic.libcaption.caption.caption_header.SCREEN_ROWS;
import static ph.extremelogic.libcaption.eia608.eia608_c.*;
import static ph.extremelogic.libcaption.eia608.eia608_charmap_header.EIA608_CHAR_NULL;
import static ph.extremelogic.libcaption.eia608.eia608_header.*;
import static ph.extremelogic.libcaption.eia608.eia608_header.eia608_control_t.TAB_OFFSET_0;

public class caption_c {

    // Class representing the caption frame buffer
    public static class caption_frame_buffer_t {
        public caption_header.caption_frame_cell_t[][] cell;

        public caption_frame_buffer_t() {
            cell = new caption_header.caption_frame_cell_t[SCREEN_ROWS][SCREEN_COLS];
            // Initialize cells
            for (int i = 0; i < SCREEN_ROWS; i++) {
                for (int j = 0; j < SCREEN_COLS; j++) {
                    cell[i][j] = new caption_header.caption_frame_cell_t();
                }
            }
        }

        // Method to clear the buffer
        public void clear() {
            for (int i = 0; i < SCREEN_ROWS; i++) {
                for (int j = 0; j < SCREEN_COLS; j++) {
                    cell[i][j] = new caption_header.caption_frame_cell_t();
                }
            }
        }
    }

    // Main class to handle caption operations
    public static class caption_frame_t {
        private static final int[] _caption_frame_rollup = {0, 2, 3, 4};

        public caption_header.caption_frame_state_t state = new caption_header.caption_frame_state_t();
        public caption_frame_buffer_t front = new caption_frame_buffer_t();
        public caption_frame_buffer_t back = new caption_frame_buffer_t();
        public caption_frame_buffer_t write = null;
        public double timestamp = -1;
        public caption_header.libcaption_stauts_t status = caption_header.libcaption_stauts_t.LIBCAPTION_OK;

        // Determines the roll-up count
        public int caption_frame_rollup() {
            return _caption_frame_rollup[this.state.getRollUpCount()];
        }

        // Checks if the frame is in paint-on mode
        public boolean caption_frame_painton() {
            return this.write == this.front;
        }

        // Clears the caption frame buffer
        public void caption_frame_buffer_clear(caption_frame_buffer_t buffer) {
            buffer.clear();
        }

        // Initializes the caption frame
        public void caption_frame_init() {
            caption_frame_buffer_clear(this.back);
            caption_frame_buffer_clear(this.front);
            // TODO check if this is a bug, write is originally initialized to null, so we should too but exception
            this.write = null;
            this.timestamp = -1;
            this.state.setRow(SCREEN_ROWS - 1);
            this.state.setCol(0);
            this.state.setUnderline(0);
            this.state.setStyle(0);
            this.state.setRollUpCount(0);
            this.state.setCcData(0);
        }

        // Helper method to access a cell in the buffer
        public caption_header.caption_frame_cell_t frame_buffer_cell(caption_frame_buffer_t buffer, int row, int col) {
            if (buffer == null || row < 0 || row >= SCREEN_ROWS || col < 0 || col >= SCREEN_COLS) {
                return null;
            }
            return buffer.cell[row][col];
        }

        // Writes a character into the caption frame
        public int caption_frame_write_char(int row, int col, int style, boolean underline, String c) {
            Debug.print("caption_frame_write_char");
            Debug.print(" - row: " + row);
            Debug.print(" - col: " + col);
            //Debug.print(" - style: " + style);
            //Debug.print(" - underline: " + underline);
            Debug.print(" - c: " + c);
            //System.out.print("" + c);
            if (this.write == null || c == null || c.isEmpty()) {
                return 0;
            }

            caption_header.caption_frame_cell_t cell = frame_buffer_cell(this.write, row, col);
            if (cell != null) {
                cell.setUnderline(underline);
                cell.setStyle(style);
                cell.setData(c);
                return 1;
            }

            return 0;
        }

        public caption_header.libcaption_stauts_t caption_frame_backspace() {
            Debug.print("caption_frame_backspace()");
            // Do not reverse wrap
            this.state.setCol(Math.max(this.state.getCol() - 1, 0));
            caption_frame_write_char(this.state.getRow(), this.state.getCol(), eia608_style_t.WHITE.getValue(), false, EIA608_CHAR_NULL);
            return caption_header.libcaption_stauts_t.LIBCAPTION_READY;
        }

        // Simulates a carriage return in the caption frame
        public caption_header.libcaption_stauts_t caption_frame_carriage_return() {
            Debug.print("caption_frame_carriage_return");
            if (this.state.getRow() < 0 || this.state.getRow() >= SCREEN_ROWS) {
                Debug.print("caption_frame_carriage_return A");
                return caption_header.libcaption_stauts_t.LIBCAPTION_ERROR;
            }

            int r = this.state.getRow() - (this.state.getRollUpCount() - 1);
            Debug.print("row: " + r);
            Debug.print("rollup: " + this.state.getRollUpCount());
            Debug.print("rollup: " + this.caption_frame_rollup());
            if (0 >= r || this.caption_frame_rollup() == 0) {
                Debug.print("caption_frame_carriage_return B");
                return caption_header.libcaption_stauts_t.LIBCAPTION_OK;
            }

            for (; r < SCREEN_ROWS; ++r) {
                caption_header.caption_frame_cell_t[] dst = this.write.cell[r - 1];
                caption_header.caption_frame_cell_t[] src = this.write.cell[r];
                System.arraycopy(src, 0, dst, 0, SCREEN_COLS);
                Debug.print("caption_frame_carriage_return C");
            }

            this.state.setCol(0);
            // Clear the last row
            for (int col = 0; col < SCREEN_COLS; col++) {
                this.write.cell[SCREEN_ROWS - 1][col] = new caption_header.caption_frame_cell_t();
                //Debug.print("caption_frame_carriage_return D");
            }
            return caption_header.libcaption_stauts_t.LIBCAPTION_OK;
        }

        // Processes the end of the caption frame
        public caption_header.libcaption_stauts_t captionFrameEnd() {
            // Copy back buffer to front buffer
            for (int i = 0; i < SCREEN_ROWS; i++) {
                System.arraycopy(this.back.cell[i], 0, this.front.cell[i], 0, SCREEN_COLS);
            }
            caption_frame_buffer_clear(this.back);
            return caption_header.libcaption_stauts_t.LIBCAPTION_READY;
        }

        // Writes a character to the frame
        public caption_header.libcaption_stauts_t eia608_write_char(String c) {
            if (this.write == null || c == null || c.isEmpty()) {
                return caption_header.libcaption_stauts_t.LIBCAPTION_OK;
            }

            // Write the character and increment the column
            if (caption_frame_write_char(this.state.getRow(), this.state.getCol(), this.state.getStyle(), this.state.getUnderline() != 0, c) == 1) {
                this.state.setCol(this.state.getCol() + 1);
            }

            return caption_header.libcaption_stauts_t.LIBCAPTION_OK;
        }

        public caption_header.libcaption_stauts_t caption_frame_delete_to_end_of_row() {
            if (this.write != null) {
                for (int c = this.state.getCol(); c < SCREEN_COLS; c++) {
                    caption_frame_write_char(this.state.getRow(), c, eia608_style_t.WHITE.getValue(), false, EIA608_CHAR_NULL);
                }
            }
            return caption_header.libcaption_stauts_t.LIBCAPTION_READY;
        }

        public caption_header.libcaption_stauts_t caption_frame_decode_control(int cc_data) {
            Debug.print("caption_frame_decode_control(" + cc_data + ")");
            int[] cc = new int[1];
            eia608_control_t cmd = eia608_parse_control(cc_data, cc);
            Debug.print("eia608_parse_control(" + cc_data + ", " + cc[0] + ")");

            //System.out.printf("caption_frame_decode_control() cmd=0x%04X ", cmd);

            switch (cmd) {
                // PAINT ON
                case CONTROL_RESUME_DIRECT_CAPTIONING:
                    Debug.print("eia608_control_resume_direct_captioning");
                    this.state.setRollUpCount(0);
                    this.write = this.front;
                    return caption_header.libcaption_stauts_t.LIBCAPTION_OK;
                case CONTROL_ERASE_DISPLAY_MEMORY:
                    Debug.print("eia608_control_erase_display_memory");
                    caption_frame_buffer_clear(this.front);
                    return caption_header.libcaption_stauts_t.LIBCAPTION_READY;

                // ROLL-UP
                case CONTROL_ROLL_UP_2:
                    Debug.print("eia608_control_roll_up_2");
                    this.state.setRollUpCount(1);
                    this.write = this.front;
                    return caption_header.libcaption_stauts_t.LIBCAPTION_OK;

                case CONTROL_ROLL_UP_3:
                    Debug.print("eia608_control_roll_up_3");
                    this.state.setRollUpCount(2);
                    this.write = this.front;
                    return caption_header.libcaption_stauts_t.LIBCAPTION_OK;

                case CONTROL_ROLL_UP_4:
                    Debug.print("eia608_control_roll_up_4");
                    this.state.setRollUpCount(3);
                    this.write = this.front;
                    return caption_header.libcaption_stauts_t.LIBCAPTION_OK;

                case CONTROL_CARRIAGE_RETURN:
                    Debug.print("eia608_control_carriage_return");
                    return caption_frame_carriage_return();

                // Corrections
                case CONTROL_BACKSPACE:
                    Debug.print("eia608_control_backspace");
                    return caption_frame_backspace();

                case CONTROL_DELETE_TO_END_OF_ROW:
                    Debug.print("eia608_control_delete_to_end_of_row");
                    return caption_frame_delete_to_end_of_row();

                // POP ON
                case CONTROL_RESUME_CAPTION_LOADING:
                    Debug.print("eia608_control_resume_caption_loading");
                    this.state.setRollUpCount(0);
                    this.write = this.back;
                    return caption_header.libcaption_stauts_t.LIBCAPTION_OK;

                case CONTROL_ERASE_NON_DISPLAYED_MEMORY:
                    Debug.print("eia608_control_erase_non_displayed_memory");
                    caption_frame_buffer_clear(this.back);
                    return caption_header.libcaption_stauts_t.LIBCAPTION_OK;

                case CONTROL_END_OF_CAPTION:
                    Debug.print("eia608_control_end_of_caption");
                    return captionFrameEnd();

                // Cursor positioning
                case TAB_OFFSET_0:
                case TAB_OFFSET_1:
                case TAB_OFFSET_2:
                case TAB_OFFSET_3:
                    Debug.print("eia608_tab_offset");
                    this.state.setCol(this.state.getCol() + (cmd.getValue() - TAB_OFFSET_0.getValue()));
                    return caption_header.libcaption_stauts_t.LIBCAPTION_OK;

                // Unhandled
                default:
                    Debug.print("Unhandled");
                    return caption_header.libcaption_stauts_t.LIBCAPTION_OK;
            }
        }

        public caption_header.libcaption_stauts_t caption_frame_decode_text(int cc_data) {
            Debug.print("caption_frame_decode_text: " + cc_data);
            int[] chan = new int[1];
            String[] char1 = new String[1];
            String[] char2 = new String[1];
            int chars = eia608_to_utf8(cc_data, chan, char1, char2);
            Debug.print(" - chars: " + chars);

            if (eia608_is_westeu(cc_data)) {
                Debug.print(" - eia608_is_westeu: true");
                // Extended characters replace the previous character for backward compatibility
                caption_frame_backspace();
            }

            if (0 < chars) {
                Debug.print(" - 0 < chars");
                eia608_write_char(char1[0]);
            }

            if (1 < chars) {
                Debug.print(" - 1 < chars");
                eia608_write_char(char2[0]);
            }

            return caption_header.libcaption_stauts_t.LIBCAPTION_OK;
        }

        public caption_header.libcaption_stauts_t caption_frame_decode_preamble(int cc_data) {
            //          Debug.print("caption_frame_decode_preamble() cc_data=0x%04X ", cc_data);
            eia608_style_t[] sty = new eia608_style_t[1];
            int[] row = new int[1];
            int[] col = new int[1];
            int[] chn = new int[1];
            int[] uln = new int[1];

            if (eia608_parse_preamble(cc_data, row, col, sty, chn, uln)) {
                // System.out.printf("row:%d col:%d underline:%d channel:%d style:%s\n", row[0], col[0], uln[0], chn[0], style[sty[0]]);
                this.state.setRow(row[0]);
                this.state.setCol(col[0]);
                this.state.setStyle(sty[0].getValue());
                this.state.setUnderline(uln[0]);
            }

            return caption_header.libcaption_stauts_t.LIBCAPTION_OK;
        }

        public caption_header.libcaption_stauts_t caption_frame_decode_midrowchange(int cc_data) {
            System.out.printf("caption_frame_decode_midrowchange() cc_data=0x%04X\n", cc_data);
            eia608_style_t[] sty = new eia608_style_t[1];
            int[] chn = new int[1];
            boolean[] uln = new boolean[1];

            if (eia608_parse_midrow_change(cc_data, chn, sty, uln)) {
                this.state.setStyle(sty[0].getValue());
                this.state.setUnderline(uln[0] ? 1 : 0);
            }

            return caption_header.libcaption_stauts_t.LIBCAPTION_OK;
        }

        // Processes the caption frame decoding
        public caption_header.libcaption_stauts_t caption_frame_decode(int cc_data, double timestamp) {
            Debug.print("caption_frame_decode: " + (cc_data) + ", " + String.format("%.6f", timestamp)); // should be 32896
            if (!eia608_parity_verify(cc_data)) {
                this.status = caption_header.libcaption_stauts_t.LIBCAPTION_ERROR;
                Debug.print("caption_frame_decode A");
                return this.status;
            }

            if (eia608_is_padding(cc_data)) {
                this.status = caption_header.libcaption_stauts_t.LIBCAPTION_OK;
                Debug.print("caption_frame_decode B");
                return this.status;
            }

            if ((this.timestamp < 0) || (this.timestamp == timestamp) || (this.status == caption_header.libcaption_stauts_t.LIBCAPTION_READY)) {
                this.timestamp = timestamp;
                this.status = caption_header.libcaption_stauts_t.LIBCAPTION_OK;
                Debug.print("caption_frame_decode C");
            }

            // Skip duplicate control commands
            if ((eia608_is_specialna(cc_data) || eia608_is_control(cc_data)) && (cc_data == this.state.getCcData())) {
                this.status = caption_header.libcaption_stauts_t.LIBCAPTION_OK;
                Debug.print("caption_frame_decode D");
                return this.status;
            }

            this.state.setCcData(cc_data);

            if (eia608_is_control(cc_data)) {
                this.status = caption_frame_decode_control(cc_data);
                Debug.print("caption_frame_decode E");
            } else if (eia608_is_basicna(cc_data) || eia608_is_specialna(cc_data) || eia608_is_westeu(cc_data)) {
                // Don't decode text if we don't know what mode we are in
                if (this.write == null) {
                    this.status = caption_header.libcaption_stauts_t.LIBCAPTION_OK;
                    Debug.print("caption_frame_decode F");
                    return this.status;
                }

                this.status = caption_frame_decode_text(cc_data);

                // If we are in paint-on mode, display immediately
                if (this.status == caption_header.libcaption_stauts_t.LIBCAPTION_OK && this.caption_frame_painton()) {
                    Debug.print("caption_frame_decode G");
                    this.status = caption_header.libcaption_stauts_t.LIBCAPTION_READY;
                }
            } else if (eia608_is_preamble(cc_data)) {
                this.status = caption_frame_decode_preamble(cc_data);
                Debug.print("caption_frame_decode H");
            } else if (eia608_is_midrowchange(cc_data)) {
                this.status = caption_frame_decode_midrowchange(cc_data);
                Debug.print("caption_frame_decode I");
            }
            Debug.print("caption_frame_decode J");
            return this.status;
        }

        public String caption_frame_to_text() {
            StringBuilder sb = new StringBuilder();
            for (int r = 0; r < SCREEN_ROWS; r++) {
                boolean lineHasContent = false;
                StringBuilder line = new StringBuilder();
                for (int c = 0; c < SCREEN_COLS; c++) {
                    caption_header.caption_frame_cell_t cell = this.front.cell[r][c];
                    if (cell != null && !cell.getData().isEmpty()) {
                        lineHasContent = true;
                        String charStr = cell.getData();
                        line.append(charStr);
                    } else {
                        line.append(' ');
                    }
                }
                if (lineHasContent) {
                    sb.append(line.toString().trim()).append("\n");
                }
            }
            return sb.toString();
        }
    }
}
