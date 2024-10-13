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

import ph.extremelogic.libcaption.eia608.Eia608Control;
import ph.extremelogic.libcaption.eia608.Eia608Style;
import ph.extremelogic.libcaption.constant.LibCaptionStatus;
import ph.extremelogic.texttrack.utils.Debug;

import static ph.extremelogic.libcaption.constant.Eia608CharConstants.EIA608_CHAR_NULL;
import static ph.extremelogic.libcaption.eia608.Eia608Control.TAB_OFFSET_0;
import static ph.extremelogic.libcaption.constant.LibCaptionStatus.ERROR;
import static ph.extremelogic.libcaption.constant.LibCaptionStatus.READY;
import static ph.extremelogic.libcaption.eia608.Eia608.*;
import static ph.extremelogic.libcaption.eia608.Eia608Decoder.*;

/**
 * The {@code CaptionFrame} class is responsible for handling closed captions,
 * specifically those using the EIA-608 format. It manages caption data such as
 * text display, buffering, formatting, and decoding EIA-608 control codes.
 */
public class CaptionFrame {

    /**
     * Number of rows in the caption frame (15 for standard EIA-608 captions).
     */
    public static final int SCREEN_ROWS = 15;

    /**
     * Number of columns in the caption frame (32 for standard EIA-608 captions).
     */
    public static final int SCREEN_COLS = 32;

    /**
     * The number of bytes used to store the text in the caption frame.
     * <p>
     * This value is calculated based on the number of rows ({@code SCREEN_ROWS}) and columns
     * ({@code SCREEN_COLS}) in the caption frame. Each cell in the grid is considered with an
     * additional 2 bytes per row, and multiplied by 4 for internal representation.
     * An extra byte is added for the null terminator or other purposes.
     */
    public static final int CAPTION_FRAME_TEXT_BYTES = 4 * ((SCREEN_COLS + 2) * SCREEN_ROWS) + 1;

    /**
     * The size of the buffer used to dump caption frame data.
     * <p>
     * The buffer is set to 8192 bytes to handle large amounts of data being written
     * or dumped from the caption frame during operations such as saving or logging
     * caption content.
     */
    public static final int CAPTION_FRAME_DUMP_BUF_SIZE = 8192;

    // Roll-up values for the caption frame, representing how many lines can be rolled up

    private static final int[] CAPTION_FRAME_ROLLUP = {0, 2, 3, 4};

    // Represents the current state of the caption frame
    private final CaptionFrameState state = new CaptionFrameState();

    // Front and back buffers for managing caption frames
    private final CaptionFrameBuffer front = new CaptionFrameBuffer();
    private final CaptionFrameBuffer back = new CaptionFrameBuffer();

    // The current buffer being written to
    private CaptionFrameBuffer write = null;
    // Timestamp to manage synchronization of caption data
    private double timestamp = -1;
    // Status of the caption frame, indicating errors or readiness
    private LibCaptionStatus status = LibCaptionStatus.OK;

    /**
     * Constructs a new {@code CaptionFrame} object and initializes the buffers and state.
     */
    public CaptionFrame() {
        init();
    }

    /**
     * Updates the status of the caption frame by comparing the old and new statuses.
     *
     * @param oldStat the old status
     * @param newStat the new status
     * @return {@code ERROR} if either status is {@code ERROR}, otherwise returns {@code READY} or the new status
     */
    public static LibCaptionStatus statusUpdate(LibCaptionStatus oldStat, LibCaptionStatus newStat) {
        if (oldStat == ERROR || newStat == ERROR) {
            return ERROR;
        }
        return (oldStat == READY) ? READY : newStat;
    }

    /**
     * Returns the number of roll-up lines based on the current roll-up count.
     *
     * @return the number of lines to roll up
     */
    public int rollUp() {
        return CAPTION_FRAME_ROLLUP[this.state.getRollUpCount()];
    }

    /**
     * Checks if the caption frame is in paint-on mode.
     *
     * @return {@code true} if the frame is in paint-on mode, {@code false} otherwise
     */
    public boolean paintOn() {
        return this.write == this.front;
    }

    /**
     * Clears the specified caption frame buffer.
     *
     * @param buffer the buffer to clear
     */
    public void bufferClear(CaptionFrameBuffer buffer) {
        buffer.clear();
    }

    /**
     * Initializes the caption frame by clearing buffers and resetting the state.
     */
    private void init() {
        bufferClear(this.back);
        bufferClear(this.front);
        this.write = null;
        this.timestamp = -1;
        this.state.setRow(SCREEN_ROWS - 1);
        this.state.setCol(0);
        this.state.setUnderline(0);
        this.state.setStyle(0);
        this.state.setRollUpCount(0);
        this.state.setCcData(0);
    }

    /**
     * Returns the {@code CaptionFrameCell} at the specified row and column in the given buffer.
     *
     * @param buffer the buffer to access
     * @param row    the row index
     * @param col    the column index
     * @return the {@code CaptionFrameCell} at the specified position, or {@code null} if invalid
     */
    public CaptionFrameCell frameBufferCell(CaptionFrameBuffer buffer, int row, int col) {
        if (buffer == null || row < 0 || row >= SCREEN_ROWS || col < 0 || col >= SCREEN_COLS) {
            return null;
        }
        return buffer.getCell()[row][col];
    }

    /**
     * Writes a character into the caption frame at the specified row and column.
     *
     * @param row       the row index
     * @param col       the column index
     * @param style     the style of the caption (e.g., color)
     * @param underline whether the character should be underlined
     * @param c         the character to write
     * @return 1 if the character was successfully written, 0 otherwise
     */
    public int writeChar(int row, int col, int style, boolean underline, String c) {
        Debug.print("caption_frame_write_char");
        Debug.print(" - row: " + row);
        Debug.print(" - col: " + col);
        Debug.print(" - c: " + c);
        if (this.write == null || c == null || c.isEmpty()) {
            return 0;
        }

        CaptionFrameCell cell = frameBufferCell(this.write, row, col);
        if (cell != null) {
            cell.setUnderline(underline);
            cell.setStyle(style);
            cell.setData(c);
            return 1;
        }

        return 0;
    }

    /**
     * Handles backspacing in the caption frame, moving the cursor backward by one column.
     *
     * @return the status after the operation
     */
    public LibCaptionStatus backspace() {
        Debug.print("caption_frame_backspace()");
        // Do not reverse wrap
        this.state.setCol(Math.max(this.state.getCol() - 1, 0));
        writeChar(this.state.getRow(), this.state.getCol(), Eia608Style.WHITE.getValue(), false, EIA608_CHAR_NULL);
        return LibCaptionStatus.READY;
    }

    /**
     * Simulates a carriage return in the caption frame, shifting rows up and clearing the last row.
     *
     * @return the status after the carriage return
     */
    public LibCaptionStatus carriageReturn() {
        Debug.print("caption_frame_carriage_return");
        if (this.state.getRow() < 0 || this.state.getRow() >= SCREEN_ROWS) {
            Debug.print("caption_frame_carriage_return A");
            return LibCaptionStatus.ERROR;
        }

        int r = this.state.getRow() - (this.state.getRollUpCount() - 1);
        Debug.print("row: " + r);
        Debug.print("rollup: " + this.state.getRollUpCount());
        Debug.print("rollup: " + this.rollUp());
        if (0 >= r || this.rollUp() == 0) {
            Debug.print("caption_frame_carriage_return B");
            return LibCaptionStatus.OK;
        }

        for (; r < SCREEN_ROWS; ++r) {
            CaptionFrameCell[] dst = this.write.getCell()[r - 1];
            CaptionFrameCell[] src = this.write.getCell()[r];
            System.arraycopy(src, 0, dst, 0, SCREEN_COLS);
            Debug.print("caption_frame_carriage_return C");
        }

        this.state.setCol(0);
        // Clear the last row
        for (int col = 0; col < SCREEN_COLS; col++) {
            this.write.getCell()[SCREEN_ROWS - 1][col] = new CaptionFrameCell();
        }
        return LibCaptionStatus.OK;
    }

    /**
     * Copies the back buffer to the front buffer and clears the back buffer.
     *
     * @return the status after the operation
     */
    public LibCaptionStatus end() {
        // Copy back buffer to front buffer
        for (int i = 0; i < SCREEN_ROWS; i++) {
            System.arraycopy(this.back.getCell()[i], 0, this.front.getCell()[i], 0, SCREEN_COLS);
        }
        bufferClear(this.back);
        return LibCaptionStatus.READY;
    }

    /**
     * Writes a character into the caption frame at the current row and column,
     * then increments the column position.
     *
     * @param c the character to write into the caption frame
     * @return {@code LibcaptionStatus.OK} after writing the character
     */
    public LibCaptionStatus eia608WriteChar(String c) {
        if (this.write == null || c == null || c.isEmpty()) {
            return LibCaptionStatus.OK;
        }

        // Write the character and increment the column
        if (writeChar(this.state.getRow(), this.state.getCol(), this.state.getStyle(), this.state.getUnderline() != 0, c) == 1) {
            this.state.setCol(this.state.getCol() + 1);
        }

        return LibCaptionStatus.OK;
    }

    /**
     * Deletes all characters from the current column to the end of the row by
     * replacing them with null characters.
     *
     * @return {@code LibcaptionStatus.READY} after clearing the row
     */
    public LibCaptionStatus deleteToEndOfRow() {
        if (this.write != null) {
            for (int c = this.state.getCol(); c < SCREEN_COLS; c++) {
                writeChar(this.state.getRow(), c, Eia608Style.WHITE.getValue(), false, EIA608_CHAR_NULL);
            }
        }
        return LibCaptionStatus.READY;
    }

    /**
     * Decodes and processes EIA-608 control codes from the given data, executing
     * operations such as backspacing, carriage returns, roll-up captions, and more.
     *
     * @param ccData the control data to decode
     * @return the updated {@code LibcaptionStatus} based on the control code
     */
    public LibCaptionStatus decodeControl(int ccData) {
        Debug.print("caption_frame_decode_control(" + ccData + ")");
        int[] cc = new int[1];
        Eia608Control cmd = eia608ParseControl(ccData, cc);
        Debug.print("eia608_parse_control(" + ccData + ", " + cc[0] + ")");

        switch (cmd) {
            // PAINT ON
            case CONTROL_RESUME_DIRECT_CAPTIONING:
                Debug.print("eia608_control_resume_direct_captioning");
                this.state.setRollUpCount(0);
                this.write = this.front;
                return LibCaptionStatus.OK;
            case CONTROL_ERASE_DISPLAY_MEMORY:
                Debug.print("eia608_control_erase_display_memory");
                bufferClear(this.front);
                return LibCaptionStatus.READY;

            // ROLL-UP
            case CONTROL_ROLL_UP_2:
                Debug.print("eia608_control_roll_up_2");
                this.state.setRollUpCount(1);
                this.write = this.front;
                return LibCaptionStatus.OK;

            case CONTROL_ROLL_UP_3:
                Debug.print("eia608_control_roll_up_3");
                this.state.setRollUpCount(2);
                this.write = this.front;
                return LibCaptionStatus.OK;

            case CONTROL_ROLL_UP_4:
                Debug.print("eia608_control_roll_up_4");
                this.state.setRollUpCount(3);
                this.write = this.front;
                return LibCaptionStatus.OK;

            case CONTROL_CARRIAGE_RETURN:
                Debug.print("eia608_control_carriage_return");
                return carriageReturn();

            // Corrections
            case CONTROL_BACKSPACE:
                Debug.print("eia608_control_backspace");
                return backspace();

            case CONTROL_DELETE_TO_END_OF_ROW:
                Debug.print("eia608_control_delete_to_end_of_row");
                return deleteToEndOfRow();

            // POP ON
            case CONTROL_RESUME_CAPTION_LOADING:
                Debug.print("eia608_control_resume_caption_loading");
                this.state.setRollUpCount(0);
                this.write = this.back;
                return LibCaptionStatus.OK;

            case CONTROL_ERASE_NON_DISPLAYED_MEMORY:
                Debug.print("eia608_control_erase_non_displayed_memory");
                bufferClear(this.back);
                return LibCaptionStatus.OK;

            case CONTROL_END_OF_CAPTION:
                Debug.print("eia608_control_end_of_caption");
                return end();

            // Cursor positioning
            case TAB_OFFSET_0, TAB_OFFSET_1, TAB_OFFSET_2, TAB_OFFSET_3:
                Debug.print("eia608_tab_offset");
                this.state.setCol(this.state.getCol() + (cmd.getValue() - TAB_OFFSET_0.getValue()));
                return LibCaptionStatus.OK;

            // Unhandled
            default:
                Debug.print("Unhandled");
                return LibCaptionStatus.OK;
        }
    }

    /**
     * Decodes text data in EIA-608 format and writes characters into the caption frame.
     * Handles extended character sets like Western European characters.
     *
     * @param ccData the text data to decode
     * @return the updated {@code LibcaptionStatus}
     */
    public LibCaptionStatus decodeText(int ccData) {
        Debug.print("caption_frame_decode_text: " + ccData);
        int[] chan = new int[1];
        String[] char1 = new String[1];
        String[] char2 = new String[1];
        int chars = eia608ToUtf8(ccData, chan, char1, char2);
        Debug.print(" - chars: " + chars);

        if (eia608IsWestEU(ccData)) {
            Debug.print(" - eia608_is_westeu: true");
            // Extended characters replace the previous character for backward compatibility
            backspace();
        }

        if (0 < chars) {
            Debug.print(" - 0 < chars");
            eia608WriteChar(char1[0]);
        }

        if (1 < chars) {
            Debug.print(" - 1 < chars");
            eia608WriteChar(char2[0]);
        }

        return LibCaptionStatus.OK;
    }

    /**
     * Decodes EIA-608 preamble data which contains formatting and positioning
     * information such as row, column, style, and underline attributes.
     *
     * @param ccData the preamble data to decode
     * @return the updated {@code LibcaptionStatus}
     */
    public LibCaptionStatus decodePreamble(int ccData) {
        Eia608Style[] sty = new Eia608Style[1];
        int[] row = new int[1];
        int[] col = new int[1];
        int[] chn = new int[1];
        int[] uln = new int[1];

        if (eia608ParsePreamble(ccData, row, col, sty, chn, uln)) {
            this.state.setRow(row[0]);
            this.state.setCol(col[0]);
            this.state.setStyle(sty[0].getValue());
            this.state.setUnderline(uln[0]);
        }

        return LibCaptionStatus.OK;
    }

    /**
     * Decodes EIA-608 midrow style changes which alter the style or underline attributes
     * in the middle of a caption row.
     *
     * @param ccData the midrow change data to decode
     * @return the updated {@code LibcaptionStatus}
     */
    public LibCaptionStatus decodeMidrowChange(int ccData) {
        System.out.printf("caption_frame_decode_midrowchange() cc_data=0x%04X%n", ccData);
        Eia608Style[] sty = new Eia608Style[1];
        int[] chn = new int[1];
        boolean[] uln = new boolean[1];

        if (eia608ParseMidrowChange(ccData, chn, sty, uln)) {
            this.state.setStyle(sty[0].getValue());
            this.state.setUnderline(uln[0] ? 1 : 0);
        }

        return LibCaptionStatus.OK;
    }

    /**
     * Processes the caption frame, decoding EIA-608 data and updating the frame
     * content based on the decoded result. This method handles both text and control
     * commands.
     *
     * @param ccData    the closed caption data to decode
     * @param timestamp the timestamp of the caption frame
     * @return the updated {@code LibcaptionStatus}
     */
    public LibCaptionStatus decode(int ccData, double timestamp) {
        Debug.print("caption_frame_decode: " + (ccData) + ", " + String.format("%.6f", timestamp));
        if (!eia608ParityVerify(ccData)) {
            Debug.print("caption_frame_decode A");
            this.status = LibCaptionStatus.ERROR;
            return this.status;
        }

        if (eia608IsPadding(ccData)) {
            Debug.print("caption_frame_decode B");
            this.status = LibCaptionStatus.OK;
            return this.status;
        }

        if ((this.timestamp < 0) || (this.timestamp == timestamp) || (this.status == LibCaptionStatus.READY)) {
            this.timestamp = timestamp;
            this.status = LibCaptionStatus.OK;
            Debug.print("caption_frame_decode C");
        }

        // Skip duplicate control commands
        if ((eia608IsSpecialna(ccData) || eia608IsControl(ccData)) && (ccData == this.state.getCcData())) {
            this.status = LibCaptionStatus.OK;
            Debug.print("caption_frame_decode D");
            return this.status;
        }

        this.state.setCcData(ccData);

        if (eia608IsControl(ccData)) {
            this.status = decodeControl(ccData);
            Debug.print("caption_frame_decode E");
        } else if (eia608IsBasicna(ccData) || eia608IsSpecialna(ccData) || eia608IsWestEU(ccData)) {
            // Don't decode text if we don't know what mode we are in
            if (this.write == null) {
                this.status = LibCaptionStatus.OK;
                Debug.print("caption_frame_decode F");
                return this.status;
            }

            this.status = decodeText(ccData);

            // If we are in paint-on mode, display immediately
            if (this.status == LibCaptionStatus.OK && this.paintOn()) {
                Debug.print("caption_frame_decode G");
                this.status = LibCaptionStatus.READY;
            }
        } else if (eia608IsPreamble(ccData)) {
            this.status = decodePreamble(ccData);
            Debug.print("caption_frame_decode H");
        } else if (eia608IsMidrowChange(ccData)) {
            this.status = decodeMidrowChange(ccData);
            Debug.print("caption_frame_decode I");
        }
        Debug.print("caption_frame_decode J");
        return this.status;
    }

    /**
     * Converts the caption frame buffer to text. This method retrieves the text content
     * stored in the caption frame cells, organizing it into lines and trimming empty lines.
     *
     * @return a {@code String} containing the text of the caption frame
     */
    public String toText() {
        StringBuilder sb = new StringBuilder();
        for (int r = 0; r < SCREEN_ROWS; r++) {
            StringBuilder line = new StringBuilder();
            for (int c = 0; c < SCREEN_COLS; c++) {
                CaptionFrameCell cell = this.front.getCell()[r][c];
                String data = (cell != null && !cell.getData().isEmpty()) ? cell.getData() : " ";
                line.append(data);
            }
            String trimmedLine = line.toString().trim();
            if (!trimmedLine.isEmpty()) {
                sb.append(trimmedLine).append("\n");
            }
        }
        return sb.toString();
    }
}
