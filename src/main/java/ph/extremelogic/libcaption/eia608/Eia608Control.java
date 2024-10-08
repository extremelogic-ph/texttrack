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
package ph.extremelogic.libcaption.eia608;

import lombok.Getter;

/**
 * The {@code Eia608Control} enum represents various control commands used in EIA-608 closed captions.
 * Each command has an associated integer value that corresponds to specific caption control actions.
 */
@Getter
public enum Eia608Control {

    TAB_OFFSET_0(0x1720),
    TAB_OFFSET_1(0x1721),
    TAB_OFFSET_2(0x1722),
    TAB_OFFSET_3(0x1723),

    CONTROL_RESUME_CAPTION_LOADING(0x1420),
    CONTROL_BACKSPACE(0x1421),
    CONTROL_ALARM_OFF(0x1422),
    CONTROL_ALARM_ON(0x1423),
    CONTROL_DELETE_TO_END_OF_ROW(0x1424),
    CONTROL_ROLL_UP_2(0x1425),
    CONTROL_ROLL_UP_3(0x1426),
    CONTROL_ROLL_UP_4(0x1427),
    CONTROL_RESUME_DIRECT_CAPTIONING(0x1429),
    CONTROL_TEXT_RESTART(0x142A),
    CONTROL_TEXT_RESUME_TEXT_DISPLAY(0x142B),
    CONTROL_ERASE_DISPLAY_MEMORY(0x142C),
    CONTROL_CARRIAGE_RETURN(0x142D),
    CONTROL_ERASE_NON_DISPLAYED_MEMORY(0x142E),
    CONTROL_END_OF_CAPTION(0x142F);

    /** The integer value associated with the control command. */
    private final int value;

    /**
     * Constructs an {@code Eia608Control} enum with the specified value.
     *
     * @param value the integer value representing the control command
     */
    Eia608Control(int value) {
        this.value = value;
    }

    /**
     * Returns the {@code Eia608Control} enum corresponding to the given integer value.
     *
     * @param value the integer value of the control command
     * @return the corresponding {@code Eia608Control} enum
     * @throws IllegalArgumentException if the value does not match any known control command
     */
    public static Eia608Control fromInt(int value) {
        for (Eia608Control control : Eia608Control.values()) {
            if (control.getValue() == value) {
                return control;
            }
        }
        throw new IllegalArgumentException("Unknown value: " + value);
    }
}
