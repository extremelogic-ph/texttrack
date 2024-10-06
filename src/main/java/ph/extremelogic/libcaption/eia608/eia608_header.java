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

import ph.extremelogic.texttrack.utils.Debug;

public class eia608_header {
    // Parity calculation macros converted to Java methods

    // Static parity table in Java
    public static final int[] eia608_parity_table = concatenate(EIA608_B1(0), EIA608_B1(64));

    public static int EIA608_BX(int B, int X) {
        return (B << X) & 0x80;
    }

    public static int EIA608_BP(int B) {
        return (B & 0x7F) | (0x80 ^ EIA608_BX(B, 1) ^ EIA608_BX(B, 2) ^ EIA608_BX(B, 3) ^
                EIA608_BX(B, 4) ^ EIA608_BX(B, 5) ^ EIA608_BX(B, 6) ^ EIA608_BX(B, 7));
    }

    public static int[] EIA608_B2(int B) {
        int[] result = new int[8];
        for (int i = 0; i < 8; i++) {
            result[i] = EIA608_BP(B + i);
        }
        return result;
    }

    public static int[] EIA608_B1(int B) {
        int[] result = new int[64];
        for (int i = 0; i < 8; i++) {
            System.arraycopy(EIA608_B2(B + (i * 8)), 0, result, i * 8, 8);
        }
        return result;
    }

    // Helper method to concatenate arrays in Java
    public static int[] concatenate(int[]... arrays) {
        int totalLength = 0;
        for (int[] array : arrays) {
            totalLength += array.length;
        }
        int[] result = new int[totalLength];
        int offset = 0;
        for (int[] array : arrays) {
            System.arraycopy(array, 0, result, offset, array.length);
            offset += array.length;
        }
        return result;
    }

    // Function to return parity byte
    public static int eia608_parity_byte(int cc_data) {
        return eia608_parity_table[0x7F & cc_data];
    }

    // Function to return parity word
    public static int eia608_parity_word(int cc_data) {
        return (eia608_parity_byte(cc_data >> 8) << 8) | eia608_parity_byte(cc_data);
    }

    // Parity calculation for the given data
    public static int eia608_parity(int cc_data) {
        return eia608_parity_word(cc_data);
    }

    // Verifies if the parity word matches the original data
    public static boolean eia608_parity_verify(int cc_data) {
        return eia608_parity_word(cc_data) == cc_data;
    }

    // Strips parity bits from the data
    public static int eia608_parity_strip(int cc_data) {
        return cc_data & 0x7F7F;
    }

    // Tests the second channel bit
    public static boolean eia608_test_second_channel_bit(int cc_data) {
        return (cc_data & 0x0800) != 0;
    }

    // Checks if data is basicna type
    public static boolean eia608_is_basicna(int cc_data) {
        Debug.print("eia608_is_basicna " + ((0x6000 & cc_data) != 0 ? 1 : 0));
        return (0x6000 & cc_data) != 0;
    }

    // Checks if data is a preamble
    public static boolean eia608_is_preamble(int cc_data) {
        Debug.print("cc_data " + cc_data);
        Debug.print("0x7040 & cc_data " + (0x7040 & cc_data));
        Debug.print("eia608_is_preamble " + ((0x1040 == (0x7040 & cc_data)) ? 1 : 0));
        return (0x1040 == (0x7040 & cc_data));
    }

    // Function to check for midrowchange
    public static boolean eia608_is_midrowchange(int cc_data) {
        return (0x1120 == (0x7770 & cc_data));
    }

    // Function to check for specialna
    public static boolean eia608_is_specialna(int cc_data) {
        Debug.print("eia608_is_specialna " + ((0x1130 == (0x7770 & cc_data)) ? 1 : 0));
        return (0x1130 == (0x7770 & cc_data));
    }

    // Function to check for XDS data
    public static boolean eia608_is_xds(int cc_data) {
        return (0x0000 == (0x7070 & cc_data) && (0x0000 != (0x0F0F & cc_data)));
    }

    // Function to check for West European data
    public static boolean eia608_is_westeu(int cc_data) {
        Debug.print("eia608_is_westeu");
        return (0x1220 == (0x7660 & cc_data));
    }

    // Function to check for control data
    public static boolean eia608_is_control(int cc_data) {
        return (0x1420 == (0x7670 & cc_data) || 0x1720 == (0x7770 & cc_data));
    }

    // Function to check for Norpak data
    public static boolean eia608_is_norpak(int cc_data) {
        return (0x1724 == (0x777C & cc_data) || 0x1728 == (0x777C & cc_data));
    }

    // Function to check for padding
    public static boolean eia608_is_padding(int cc_data) {
        return (0x8080 == cc_data);
    }

    // Method to return the appropriate tab offset based on the value
    public static eia608_control_t eia608_tab(int offset) {
        return switch (offset) {
            case 0 -> eia608_control_t.TAB_OFFSET_0;
            case 1 -> eia608_control_t.TAB_OFFSET_1;
            case 2 -> eia608_control_t.TAB_OFFSET_2;
            case 3 -> eia608_control_t.TAB_OFFSET_3;
            default -> throw new IllegalArgumentException("Invalid tab offset: " + offset);
        };
    }

    // Enum for styles
    public enum eia608_style_t {
        WHITE(0),
        GREEN(1),
        BLUE(2),
        CYAN(3),
        RED(4),
        YELLOW(5),
        MAGENTA(6),
        ITALICS(7);

        private final int value;

        eia608_style_t(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    // Enumeration for control commands
    public enum eia608_control_t {

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

        private final int value;

        eia608_control_t(int value) {
            this.value = value;
        }

        public static eia608_control_t fromInt(int value) {
            for (eia608_control_t control : eia608_control_t.values()) {
                if (control.getValue() == value) {
                    return control;
                }
            }
            throw new IllegalArgumentException("Unknown value: " + value);
        }

        public int getValue() {
            return value;
        }
    }

}
