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
package ph.extremelogic.libcaption.constant;

import lombok.Getter;

/**
 * The {@code CcType} enum represents the different types of closed caption (CC) data
 * used in CEA-708 captions. Each type is associated with an integer value.
 */
@Getter
public enum CcType {
    /** Represents NTSC closed captions in field 1 with a value of 0. */
    NTSC_CC_FIELD_1(0),

    /** Represents NTSC closed captions in field 2 with a value of 1. */
    NTSC_CC_FIELD_2(1),

    /** Represents DTVCC (Digital TV Closed Caption) packet data with a value of 2. */
    DTVCC_PACKET_DATA(2),

    /** Represents the start of a DTVCC packet with a value of 3. */
    DTVCC_PACKET_START(3);

    /** The integer value associated with the closed caption type. */
    private final int value;

    /**
     * Constructs a {@code CcType} enum with the specified value.
     *
     * @param value the integer value representing the closed caption type
     */
    CcType(int value) {
        this.value = value;
    }
}
