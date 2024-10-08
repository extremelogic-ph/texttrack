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

/**
 * The {@code CaptionFrameBuffer} class represents a buffer that holds caption cells for
 * a closed caption display. It is used to manage a 2D array of {@link CaptionFrameCell}
 * objects representing the content of a caption frame on the screen.
 */
public class CaptionFrameBuffer {

    /**
     * A 2D array of {@link CaptionFrameCell} objects representing the individual cells
     * of the caption frame. The array size is determined by the number of rows and columns
     * defined by {@link CaptionFrame#SCREEN_ROWS} and {@link CaptionFrame#SCREEN_COLS}.
     */
    private CaptionFrameCell[][] cell;


    /**
     * Constructs a new {@code CaptionFrameBuffer} object. The buffer is initialized
     * with a 2D array of {@link CaptionFrameCell} objects. Each cell in the array
     * is initialized with a new {@code CaptionFrameCell} instance.
     */
    public CaptionFrameBuffer() {
        cell = new CaptionFrameCell[CaptionFrame.SCREEN_ROWS][CaptionFrame.SCREEN_COLS];
        // Initialize cells
        for (int i = 0; i < CaptionFrame.SCREEN_ROWS; i++) {
            for (int j = 0; j < CaptionFrame.SCREEN_COLS; j++) {
                cell[i][j] = new CaptionFrameCell();
            }
        }
    }

    /**
     * Returns the 2D array of {@link CaptionFrameCell} objects representing the caption frame buffer.
     * This array contains the individual cells used to display captions on the screen.
     *
     * @return a 2D array of {@link CaptionFrameCell} objects.
     */
    public CaptionFrameCell[][] getCell() {
        return cell;
    }

    /**
     * Clears the caption frame buffer by reinitializing each cell with a new
     * {@link CaptionFrameCell} object. This effectively resets the content of the
     * caption frame to an empty state.
     */
    public void clear() {
        for (int i = 0; i < CaptionFrame.SCREEN_ROWS; i++) {
            for (int j = 0; j < CaptionFrame.SCREEN_COLS; j++) {
                cell[i][j] = new CaptionFrameCell();
            }
        }
    }
}
