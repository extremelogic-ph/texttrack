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

import java.util.HashMap;
import java.util.Map;

public class eia608_c {

    // Row and reverse row mappings
    public static final int[] EIA608_ROW_MAP = {10, -1, 0, 1, 2, 3, 11, 12, 13, 14, 4, 5, 6, 7, 8, 9};
    public static final int[] EIA608_REVERSE_ROW_MAP = {2, 3, 4, 5, 10, 11, 12, 13, 14, 15, 0, 6, 7, 8, 9, 1};

    // Style map for visual representation
    public static final String[] EIA608_STYLE_MAP = {
            "white", "green", "blue", "cyan", "red", "yellow", "magenta", "italics"
    };
    // Mapping for characters, this would be populated according to your needs
    public static final Map<Integer, String> EIA608_CHAR_MAP = new HashMap<>() {{
        put(0, "\u0020"); // SPACE
        put(1, "\u0021"); // EXCLAMATION_MARK
        put(2, "\""); // QUOTATION_MARK
        put(3, "\u0023"); // NUMBER_SIGN
        put(4, "\u0024"); // DOLLAR_SIGN
        put(5, "\u0025"); // PERCENT_SIGN
        put(6, "\u0026"); // AMPERSAND
        put(7, "\u2019"); // RIGHT_SINGLE_QUOTATION_MARK
        put(8, "\u0028"); // LEFT_PARENTHESIS
        put(9, "\u0029"); // RIGHT_PARENTHESIS
        put(10, "\u00E1"); // LATIN_SMALL_LETTER_A_WITH_ACUTE
        put(11, "\u002B"); // PLUS_SIGN
        put(12, "\u002C"); // COMMA
        put(13, "\u002D"); // HYPHEN_MINUS
        put(14, "\u002E"); // FULL_STOP
        put(15, "\u002F"); // SOLIDUS
        put(16, "\u0030"); // DIGIT_ZERO
        put(17, "\u0031"); // DIGIT_ONE
        put(18, "\u0032"); // DIGIT_TWO
        put(19, "\u0033"); // DIGIT_THREE
        put(20, "\u0034"); // DIGIT_FOUR
        put(21, "\u0035"); // DIGIT_FIVE
        put(22, "\u0036"); // DIGIT_SIX
        put(23, "\u0037"); // DIGIT_SEVEN
        put(24, "\u0038"); // DIGIT_EIGHT
        put(25, "\u0039"); // DIGIT_NINE
        put(26, "\u003A"); // COLON
        put(27, "\u003B"); // SEMICOLON
        put(28, "\u003C"); // LESS_THAN_SIGN
        put(29, "\u003D"); // EQUALS_SIGN
        put(30, "\u003E"); // GREATER_THAN_SIGN
        put(31, "\u003F"); // QUESTION_MARK
        put(32, "\u0040"); // COMMERCIAL_AT
        put(33, "\u0041"); // LATIN_CAPITAL_LETTER_A
        put(34, "\u0042"); // LATIN_CAPITAL_LETTER_B
        put(35, "\u0043"); // LATIN_CAPITAL_LETTER_C
        put(36, "\u0044"); // LATIN_CAPITAL_LETTER_D
        put(37, "\u0045"); // LATIN_CAPITAL_LETTER_E
        put(38, "\u0046"); // LATIN_CAPITAL_LETTER_F
        put(39, "\u0047"); // LATIN_CAPITAL_LETTER_G
        put(40, "\u0048"); // LATIN_CAPITAL_LETTER_H
        put(41, "\u0049"); // LATIN_CAPITAL_LETTER_I
        put(42, "\u004A"); // LATIN_CAPITAL_LETTER_J
        put(43, "\u004B"); // LATIN_CAPITAL_LETTER_K
        put(44, "\u004C"); // LATIN_CAPITAL_LETTER_L
        put(45, "\u004D"); // LATIN_CAPITAL_LETTER_M
        put(46, "\u004E"); // LATIN_CAPITAL_LETTER_N
        put(47, "\u004F"); // LATIN_CAPITAL_LETTER_O
        put(48, "\u0050"); // LATIN_CAPITAL_LETTER_P
        put(49, "\u0051"); // LATIN_CAPITAL_LETTER_Q
        put(50, "\u0052"); // LATIN_CAPITAL_LETTER_R
        put(51, "\u0053"); // LATIN_CAPITAL_LETTER_S
        put(52, "\u0054"); // LATIN_CAPITAL_LETTER_T
        put(53, "\u0055"); // LATIN_CAPITAL_LETTER_U
        put(54, "\u0056"); // LATIN_CAPITAL_LETTER_V
        put(55, "\u0057"); // LATIN_CAPITAL_LETTER_W
        put(56, "\u0058"); // LATIN_CAPITAL_LETTER_X
        put(57, "\u0059"); // LATIN_CAPITAL_LETTER_Y
        put(58, "\u005A"); // LATIN_CAPITAL_LETTER_Z
        put(59, "\u005B"); // LEFT_SQUARE_BRACKET
        put(60, "\u00E9"); // LATIN_SMALL_LETTER_E_WITH_ACUTE
        put(61, "\u005D"); // RIGHT_SQUARE_BRACKET
        put(62, "\u00ED"); // LATIN_SMALL_LETTER_I_WITH_ACUTE
        put(63, "\u00F3"); // LATIN_SMALL_LETTER_O_WITH_ACUTE
        put(64, "\u00FA"); // LATIN_SMALL_LETTER_U_WITH_ACUTE
        put(65, "\u0061"); // LATIN_SMALL_LETTER_A
        put(66, "\u0062"); // LATIN_SMALL_LETTER_B
        put(67, "\u0063"); // LATIN_SMALL_LETTER_C
        put(68, "\u0064"); // LATIN_SMALL_LETTER_D
        put(69, "\u0065"); // LATIN_SMALL_LETTER_E
        put(70, "\u0066"); // LATIN_SMALL_LETTER_F
        put(71, "\u0067"); // LATIN_SMALL_LETTER_G
        put(72, "\u0068"); // LATIN_SMALL_LETTER_H
        put(73, "\u0069"); // LATIN_SMALL_LETTER_I
        put(74, "\u006A"); // LATIN_SMALL_LETTER_J
        put(75, "\u006B"); // LATIN_SMALL_LETTER_K
        put(76, "\u006C"); // LATIN_SMALL_LETTER_L
        put(77, "\u006D"); // LATIN_SMALL_LETTER_M
        put(78, "\u006E"); // LATIN_SMALL_LETTER_N
        put(79, "\u006F"); // LATIN_SMALL_LETTER_O
        put(80, "\u0070"); // LATIN_SMALL_LETTER_P
        put(81, "\u0071"); // LATIN_SMALL_LETTER_Q
        put(82, "\u0072"); // LATIN_SMALL_LETTER_R
        put(83, "\u0073"); // LATIN_SMALL_LETTER_S
        put(84, "\u0074"); // LATIN_SMALL_LETTER_T
        put(85, "\u0075"); // LATIN_SMALL_LETTER_U
        put(86, "\u0076"); // LATIN_SMALL_LETTER_V
        put(87, "\u0077"); // LATIN_SMALL_LETTER_W
        put(88, "\u0078"); // LATIN_SMALL_LETTER_X
        put(89, "\u0079"); // LATIN_SMALL_LETTER_Y
        put(90, "\u007A"); // LATIN_SMALL_LETTER_Z
        put(91, "\u00E7"); // LATIN_SMALL_LETTER_C_WITH_CEDILLA
        put(92, "\u00F7"); // DIVISION_SIGN
        put(93, "\u00D1"); // LATIN_CAPITAL_LETTER_N_WITH_TILDE
        put(94, "\u00F1"); // LATIN_SMALL_LETTER_N_WITH_TILDE
        put(95, "\u2588"); // FULL_BLOCK
        put(96, "\u00AE"); // REGISTERED_SIGN
        put(97, "\u00B0"); // DEGREE_SIGN
        put(98, "\u00BD"); // VULGAR_FRACTION_ONE_HALF
        put(99, "\u00BF"); // INVERTED_QUESTION_MARK
        put(100, "\u2122"); // TRADE_MARK_SIGN
        put(101, "\u00A2"); // CENT_SIGN
        put(102, "\u00A3"); // POUND_SIGN
        put(103, "\u266A"); // EIGHTH_NOTE
        put(104, "\u00E0"); // LATIN_SMALL_LETTER_A_WITH_GRAVE
        put(105, "\u00A0"); // NO_BREAK_SPACE
        put(106, "\u00E8"); // LATIN_SMALL_LETTER_E_WITH_GRAVE
        put(107, "\u00E2"); // LATIN_SMALL_LETTER_A_WITH_CIRCUMFLEX
        put(108, "\u00AA"); // LATIN_SMALL_LETTER_E_WITH_CIRCUMFLEX
        put(109, "\u00AE"); // LATIN_SMALL_LETTER_I_WITH_CIRCUMFLEX
        put(110, "\u00BA"); // LATIN_SMALL_LETTER_O_WITH_CIRCUMFLEX
        put(111, "\u00BB"); // LATIN_SMALL_LETTER_U_WITH_CIRCUMFLEX
        put(112, "\u00C1"); // LATIN_CAPITAL_LETTER_A_WITH_ACUTE
        put(113, "\u00C9"); // LATIN_CAPITAL_LETTER_E_WITH_ACUTE
        put(114, "\u00D3"); // LATIN_CAPITAL_LETTER_O_WITH_ACUTE
        put(115, "\u00DA"); // LATIN_CAPITAL_LETTER_U_WITH_ACUTE
        put(116, "\u00DC"); // LATIN_CAPITAL_LETTER_U_WITH_DIAERESIS
        put(117, "\u00FC"); // LATIN_SMALL_LETTER_U_WITH_DIAERESIS
        put(118, "\u2018"); // LEFT_SINGLE_QUOTATION_MARK
        put(119, "\u00A1"); // INVERTED_EXCLAMATION_MARK
        put(120, "\u002A"); // ASTERISK
        put(121, "\u0027"); // APOSTROPHE
        put(122, "\u2014"); // EM_DASH
        put(123, "\u00A9"); // COPYRIGHT_SIGN
        put(124, "\u2120"); // SERVICE_MARK
        put(125, "\u2022"); // BULLET
        put(126, "\u201C"); // LEFT_DOUBLE_QUOTATION_MARK
        put(127, "\u201D"); // RIGHT_DOUBLE_QUOTATION_MARK
        put(128, "\u00C0"); // LATIN_CAPITAL_LETTER_A_WITH_GRAVE
        put(129, "\u00C2"); // LATIN_CAPITAL_LETTER_A_WITH_CIRCUMFLEX
        put(130, "\u00C7"); // LATIN_CAPITAL_LETTER_C_WITH_CEDILLA
        put(131, "\u00C8"); // LATIN_CAPITAL_LETTER_E_WITH_GRAVE
        put(132, "\u00CA"); // LATIN_CAPITAL_LETTER_E_WITH_CIRCUMFLEX
        put(133, "\u00CB"); // LATIN_CAPITAL_LETTER_E_WITH_DIAERESIS
        put(134, "\u00AB"); // LATIN_SMALL_LETTER_E_WITH_DIAERESIS
        put(135, "\u00CE"); // LATIN_CAPITAL_LETTER_I_WITH_CIRCUMFLEX
        put(136, "\u00CF"); // LATIN_CAPITAL_LETTER_I_WITH_DIAERESIS
        put(137, "\u00AF"); // LATIN_SMALL_LETTER_I_WITH_DIAERESIS
        put(138, "\u00D4"); // LATIN_CAPITAL_LETTER_O_WITH_CIRCUMFLEX
        put(139, "\u00D9"); // LATIN_CAPITAL_LETTER_U_WITH_GRAVE
        put(140, "\u00B9"); // LATIN_SMALL_LETTER_U_WITH_GRAVE
        put(141, "\u00DB"); // LATIN_CAPITAL_LETTER_U_WITH_CIRCUMFLEX
        put(142, "\u00AB"); // LEFT_POINTING_DOUBLE_ANGLE_QUOTATION_MARK
        put(143, "\u00BB"); // RIGHT_POINTING_DOUBLE_ANGLE_QUOTATION_MARK
        put(144, "\u00C3"); // LATIN_CAPITAL_LETTER_A_WITH_TILDE
        put(145, "\u00A3"); // LATIN_SMALL_LETTER_A_WITH_TILDE
        put(146, "\u00CD"); // LATIN_CAPITAL_LETTER_I_WITH_ACUTE
        put(147, "\u00CC"); // LATIN_CAPITAL_LETTER_I_WITH_GRAVE
        put(148, "\u00AC"); // LATIN_SMALL_LETTER_I_WITH_GRAVE
        put(149, "\u00D2"); // LATIN_CAPITAL_LETTER_O_WITH_GRAVE
        put(150, "\u00B2"); // LATIN_SMALL_LETTER_O_WITH_GRAVE
        put(151, "\u00D5"); // LATIN_CAPITAL_LETTER_O_WITH_TILDE
        put(152, "\u00B5"); // LATIN_SMALL_LETTER_O_WITH_TILDE
        put(153, "\u007B"); // LEFT_CURLY_BRACKET
        put(154, "\u007D"); // RIGHT_CURLY_BRACKET
        put(155, "\\u005C"); // REVERSE_SOLIDUS
        put(156, "\u005E"); // CIRCUMFLEX_ACCENT
        put(157, "\u005F"); // LOW_LINE
        put(158, "\u007C"); // VERTICAL_LINE
        put(159, "\u007E"); // TILDE
        put(160, "\u00C4"); // LATIN_CAPITAL_LETTER_A_WITH_DIAERESIS
        put(161, "\u00A4"); // LATIN_SMALL_LETTER_A_WITH_DIAERESIS
        put(162, "\u00D6"); // LATIN_CAPITAL_LETTER_O_WITH_DIAERESIS
        put(163, "\u00B6"); // LATIN_SMALL_LETTER_O_WITH_DIAERESIS
        put(164, "\u00DF"); // LATIN_SMALL_LETTER_SHARP_S
        put(165, "\u00A5"); // YEN_SIGN
        put(166, "\u00A4"); // CURRENCY_SIGN
        put(167, "\u00A6"); // BROKEN_BAR
        put(168, "\u00C5"); // LATIN_CAPITAL_LETTER_A_WITH_RING_ABOVE
        put(169, "\u00A5"); // LATIN_SMALL_LETTER_A_WITH_RING_ABOVE
        put(170, "\u00D8"); // LATIN_CAPITAL_LETTER_O_WITH_STROKE
        put(171, "\u00B8"); // LATIN_SMALL_LETTER_O_WITH_STROKE
        put(172, "\u250C"); // BOX_DRAWINGS_LIGHT_DOWN_AND_RIGHT
        put(173, "\u2510"); // BOX_DRAWINGS_LIGHT_DOWN_AND_LEFT
        put(174, "\u2514"); // BOX_DRAWINGS_LIGHT_UP_AND_RIGHT
        put(175, "\u2518"); // BOX_DRAWINGS_LIGHT_UP_AND_LEFT
    }};

    // Inline method for row pramble
    private static int eia608_row_preamble(int row, int chan, int x, boolean underline) {
        row = EIA608_REVERSE_ROW_MAP[row & 0x0F];
        return eia608_parity(0x1040 | (chan != 0 ? 0x0800 : 0x0000) | ((row << 7) & 0x0700) | ((row << 5) & 0x0020)) |
                ((x << 1) & 0x001E) | (underline ? 0x0001 : 0x0000);
    }

    // Row and column pramble
    public static int eia608_row_column_preamble(int row, int col, int chan, boolean underline) {
        return eia608_row_preamble(row, chan, 0x10 | (col / 4), underline);
    }

    // Row and style pramble
    public static int eia608_row_style_preamble(int row, int chan, eia608_header.eia608_style_t style, boolean underline) {
        return eia608_row_preamble(row, chan, style.getValue(), underline);
    }

    // Midrow change
    public static int eia608_midrow_change(int chan, eia608_header.eia608_style_t style, boolean underline) {
        return eia608_parity(0x1120 | ((chan << 11) & 0x0800) | ((style.getValue() << 1) & 0x000E) | (underline ? 0x0001 : 0));
    }

    // Parse preamble
    public static boolean eia608_parse_preamble(int ccData, int[] row, int[] col, eia608_header.eia608_style_t[] style, int[] chan, int[] underline) {
        row[0] = EIA608_ROW_MAP[((0x0700 & ccData) >> 7) | ((0x0020 & ccData) >> 5)];
        chan[0] = (0x0800 & ccData) != 0 ? 1 : 0;
        underline[0] = (0x0001 & ccData) != 0 ? 1 : 0;

        if ((0x0010 & ccData) != 0) {
            style[0] = eia608_header.eia608_style_t.WHITE;
            col[0] = 4 * ((0x000E & ccData) >> 1);
        } else {
            style[0] = eia608_header.eia608_style_t.values()[(0x000E & ccData) >> 1];
            col[0] = 0;
        }

        return true;
    }

    // Parse midrow change
    public static boolean eia608_parse_midrow_change(int ccData, int[] chan, eia608_header.eia608_style_t[] style, boolean[] underline) {
        chan[0] = (0x0800 & ccData) != 0 ? 1 : 0;

        if ((0x1120 & ccData) == (0x7770 & ccData)) {
            style[0] = eia608_header.eia608_style_t.values()[(0x000E & ccData) >> 1];
            underline[0] = (0x0001 & ccData) != 0;
        }

        return true;
    }

    // Parse control command
    public static eia608_header.eia608_control_t eia608_parse_control(int ccData, int[] cc) {
     //   Debug.print("eia608_parse_control: " + ccData);
        if ((0x0200 & ccData) != 0) {
            cc[0] = (ccData & 0x0800) != 0 ? 1 : 0;
            return eia608_header.eia608_control_t.fromInt(0x177F & ccData);
        } else {
            // Wrap the bitwise OR operation in parentheses to ensure proper precedence
            cc[0] = ((ccData & 0x0800) != 0 ? 1 : 0) | ((ccData & 0x0100) != 0 ? 2 : 0);
            return eia608_header.eia608_control_t.fromInt(0x167F & ccData);
        }
    }

    // Control command
    public static int eia608_control_command(eia608_header.eia608_control_t cmd, int cc) {
        int c = (cc & 0x01) != 0 ? 0x0800 : 0x0000;
        int f = (cc & 0x02) != 0 ? 0x0100 : 0x0000;

        if (cmd == eia608_header.eia608_control_t.TAB_OFFSET_0) {
            return eia608_parity(cmd.getValue() | c);
        } else {
            return eia608_parity(cmd.getValue() | c | f);
        }
    }

    // Text handling functions
    public static int eia608_to_index(int ccData, int[] chan, int[] c1, int[] c2) {
        c1[0] = -1;
        c2[0] = -1;
        chan[0] = 0;
        ccData &= 0x7F7F; // strip off parity bits

        if (eia608_header.eia608_is_basicna(ccData)) {
            c1[0] = (ccData >> 8) - 0x20;
            ccData &= 0x00FF;

            if (0x0020 <= ccData && ccData < 0x0080) {
                c2[0] = ccData - 0x20;
                return 2;
            }
            return 1;
        }

        // Check and strip second channel toggle
        chan[0] = (ccData & 0x0800) != 0 ? 1 : 0;
        ccData &= 0xF7FF;

        if (eia608_header.eia608_is_specialna(ccData)) {
            c1[0] = ccData - 0x1130 + 0x60;
            return 1;
        }

        if (0x1220 <= ccData && ccData < 0x1240) {
            c1[0] = ccData - 0x1220 + 0x70;
            return 1;
        }

        if (0x1320 <= ccData && ccData < 0x1340) {
            c1[0] = ccData - 0x1320 + 0x90;
            return 1;
        }

        return 0;
    }

    // Mapping from index to UTF-8 char
    public static String utf8_from_index(int idx) {
        return (0 <= idx && idx < EIA608_CHAR_MAP.size()) ? EIA608_CHAR_MAP.get(idx) : "";
    }

    // Convert to UTF-8
    public static int eia608_to_utf8(int ccData, int[] chan, String[] str1, String[] str2) {
        int[] c1 = new int[1], c2 = new int[1];
        int size = eia608_to_index(ccData, chan, c1, c2);
        str1[0] = utf8_from_index(c1[0]);
        str2[0] = utf8_from_index(c2[0]);
        return size;
    }

    // Parity function (placeholder, assuming you have it in eia608_header)
    private static int eia608_parity(int ccData) {
        return eia608_header.eia608_parity(ccData);
    }
}
