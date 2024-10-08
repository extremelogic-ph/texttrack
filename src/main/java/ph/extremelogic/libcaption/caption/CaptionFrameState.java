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

import lombok.Data;

/**
 * Represents the state of a caption frame, including formatting and positional
 * information such as underline, style, roll-up count, row, and column.
 */
@Data
public class CaptionFrameState {
    /**
     * Indicates whether the text is underlined (1 for underline, 0 for no underline).
     */
    private int underline;

    /**
     * Represents the style of the text (e.g., font, size, or color).
     */
    private int style;

    /**
     * Specifies the number of rows to roll up in roll-up mode captions.
     */
    private int rollUpCount;

    /**
     * Represents the current row position of the caption within the frame.
     */
    private int row;

    /**
     * Represents the current column position of the caption within the frame.
     */
    private int col;

    /**
     * Holds the raw closed caption data (CC Data) for processing.
     */
    private int ccData;
}
