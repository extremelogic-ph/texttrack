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

import static ph.extremelogic.libcaption.caption.caption_header.libcaption_stauts_t.LIBCAPTION_ERROR;
import static ph.extremelogic.libcaption.caption.caption_header.libcaption_stauts_t.LIBCAPTION_READY;

public class caption_header {

    public static final int SCREEN_ROWS = 15;
    public static final int SCREEN_COLS = 32;

    public static libcaption_stauts_t libcaption_status_update(libcaption_stauts_t old_stat, libcaption_stauts_t new_stat) {
        return (LIBCAPTION_ERROR == old_stat || LIBCAPTION_ERROR == new_stat) ? LIBCAPTION_ERROR : (LIBCAPTION_READY == old_stat) ? LIBCAPTION_READY : new_stat;
    }
    public enum libcaption_stauts_t {
        LIBCAPTION_ERROR,
        LIBCAPTION_OK,
        LIBCAPTION_READY;
    }

    // Class to represent a caption frame cell
    public static class caption_frame_cell_t {
        public static final int CAPTION_FRAME_TEXT_BYTES = 4 * ((SCREEN_COLS + 2) * SCREEN_ROWS) + 1;
        public static final int CAPTION_FRAME_DUMP_BUF_SIZE = 8192;
        private boolean underline; // uln
        private int style; // sty
        private String data; // data[5], for UTF-8 characters

        public caption_frame_cell_t() {
            data = new String(); // Allocate array of size 5
        }

        // Getters and setters
        public boolean isUnderline() {
            return underline;
        }

        public void setUnderline(boolean underline) {
            this.underline = underline;
        }

        public int getStyle() {
            return style;
        }

        public void setStyle(int style) {
            this.style = style;
        }

        public String getData() {
            return data;
        }

        public void setData(String data) {
            this.data = data;
        }

    }

    public static class caption_frame_state_t {
        private int underline; // uln
        private int style; // sty
        private int rollUpCount; // rup
        private int row;
        private int col;
        private int ccData; // uint16_t cc_data;

        // Getters and setters
        public int getUnderline() {
            return underline;
        }

        public void setUnderline(int underline) {
            this.underline = underline;
        }

        public int getStyle() {
            return style;
        }

        public void setStyle(int style) {
            this.style = style;
        }

        public int getRollUpCount() {
            return rollUpCount;
        }

        public void setRollUpCount(int rollUpCount) {
            this.rollUpCount = rollUpCount;
        }

        public int getRow() {
            return row;
        }

        public void setRow(int row) {
            this.row = row;
        }

        public int getCol() {
            return col;
        }

        public void setCol(int col) {
            this.col = col;
        }

        public int getCcData() {
            return ccData;
        }

        public void setCcData(int ccData) {
            this.ccData = ccData;
        }
    }
}
