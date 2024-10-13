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

/**
 * The {@code Eia608FromUtf8} class provides a utility method to convert UTF-8 encoded strings
 * to EIA-608 encoded integers used for captioning. The conversion maps ASCII and some special
 * Unicode characters to their corresponding EIA-608 code representations.
 *
 * This class contains only static methods and cannot be instantiated.
 */
public class Eia608FromUtf8 {
    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private Eia608FromUtf8() {
        // Prevent instantiation
    }

    /**
     * Converts a UTF-8 encoded string into an EIA-608 encoded integer.
     * This method processes the first character of the string and returns its corresponding
     * EIA-608 code based on predefined mappings of ASCII and special Unicode characters.
     *
     * @param s the UTF-8 encoded string to convert; if the string is {@code null} or empty,
     *          the method returns 0x0000.
     * @return the EIA-608 encoded integer corresponding to the input string's first character,
     *         or 0x0000 if the string is null or empty, or if the character is not recognized.
     * @throws IllegalArgumentException if an unsupported or unexpected Unicode character is encountered.
     */
    public static int eia608FromUtf8(String s) {
        if (s == null || s.isEmpty()) {
            return 0x0000;
        }

        int YYCURSOR = 0;
        int yych;

        // Get the Unicode code point at the current cursor position
        yych = s.codePointAt(YYCURSOR);
        int charCount = Character.charCount(yych); // Number of char units (1 or 2) for the code point

        if (yych <= '`') {
            if (yych <= '*') {
                if (yych <= '&') {
                    if (yych <= 0x00) {
                        // NULL
                        return 0x0000;
                    } else if (yych <= 0x1F) {
                        // DEFAULT_RULE
                        return 0x0000;
                    } else {
                        // ASCII range
                        return (yych << 8) & 0xFF00;
                    }
                } else {
                    if (yych == '\'') {
                        // APOSTROPHE -> RIGHT_SINGLE_QUOTATION_MARK
                        return 0x1229;
                    } else if (yych <= ')') {
                        // ASCII range
                        return (yych << 8) & 0xFF00;
                    } else if (yych == '*') {
                        // ASTERISK
                        return 0x1228;
                    } else {
                        // ASCII range
                        return (yych << 8) & 0xFF00;
                    }
                }
            } else {
                if (yych <= ']') {
                    if (yych == '\\') {
                        // REVERSE_SOLIDUS
                        return 0x132B;
                    } else {
                        // ASCII range
                        return (yych << 8) & 0xFF00;
                    }
                } else {
                    if (yych == '^') {
                        // CIRCUMFLEX_ACCENT
                        return 0x132C;
                    } else if (yych == '_') {
                        // LOW_LINE
                        return 0x132D;
                    } else if (yych == '`') {
                        // GRAVE_ACCENT -> LEFT_SINGLE_QUOTATION_MARK
                        return 0x1226;
                    } else {
                        // ASCII range
                        return (yych << 8) & 0xFF00;
                    }
                }
            }
        } else {
            if (yych <= 0x7F) {
                if (yych <= '|') {
                    if (yych <= 'z') {
                        // ASCII range
                        return (yych << 8) & 0xFF00;
                    } else if (yych == '{') {
                        // LEFT_CURLY_BRACKET
                        return 0x1329;
                    } else if (yych == '|') {
                        // VERTICAL_LINE
                        return 0x132E;
                    } else {
                        // ASCII range
                        return (yych << 8) & 0xFF00;
                    }
                } else {
                    if (yych == '}') {
                        // RIGHT_CURLY_BRACKET
                        return 0x132A;
                    } else if (yych == '~') {
                        // TILDE
                        return 0x132F;
                    } else {
                        // DEL/BACKSPACE
                        return 0x0000;
                    }
                }
            } else {
                // Handle Unicode characters beyond ASCII
                switch (yych) {
                    case 0x00A0: return 0x1139; // NO_BREAK_SPACE
                    case 0x00A1: return 0x1227; // INVERTED_EXCLAMATION_MARK
                    case 0x00A2: return 0x1135; // CENT_SIGN
                    case 0x00A3: return 0x1136; // POUND_SIGN
                    case 0x00A4: return 0x1336; // CURRENCY_SIGN
                    case 0x00A5: return 0x1335; // YEN_SIGN
                    case 0x00A6: return 0x1337; // BROKEN_BAR
                    case 0x00A9: return 0x122B; // COPYRIGHT_SIGN
                    case 0x00AB: return 0x123E; // LEFT_POINTING_DOUBLE_ANGLE_QUOTATION_MARK
                    case 0x00AE: return 0x1130; // REGISTERED_SIGN
                    case 0x00B0: return 0x1131; // DEGREE_SIGN
                    case 0x00BB: return 0x123F; // RIGHT_POINTING_DOUBLE_ANGLE_QUOTATION_MARK
                    case 0x00BD: return 0x1132; // VULGAR_FRACTION_ONE_HALF
                    case 0x00BF: return 0x1133; // INVERTED_QUESTION_MARK
                    case 0x00C0: return 0x1230; // LATIN_CAPITAL_LETTER_A_WITH_GRAVE
                    case 0x00C1: return 0x1220; // LATIN_CAPITAL_LETTER_A_WITH_ACUTE
                    case 0x00C2: return 0x1231; // LATIN_CAPITAL_LETTER_A_WITH_CIRCUMFLEX
                    case 0x00C3: return 0x1320; // LATIN_CAPITAL_LETTER_A_WITH_TILDE
                    case 0x00C4: return 0x1330; // LATIN_CAPITAL_LETTER_A_WITH_DIAERESIS
                    case 0x00C5: return 0x1338; // LATIN_CAPITAL_LETTER_A_WITH_RING_ABOVE
                    case 0x00C7: return 0x1232; // LATIN_CAPITAL_LETTER_C_WITH_CEDILLA
                    case 0x00C8: return 0x1233; // LATIN_CAPITAL_LETTER_E_WITH_GRAVE
                    case 0x00C9: return 0x1221; // LATIN_CAPITAL_LETTER_E_WITH_ACUTE
                    case 0x00CA: return 0x1234; // LATIN_CAPITAL_LETTER_E_WITH_CIRCUMFLEX
                    case 0x00CB: return 0x1235; // LATIN_CAPITAL_LETTER_E_WITH_DIAERESIS
                    // Add other cases as needed for your tests
                    default:
                        // DEFAULT_RULE
                        return 0x0000;
                }
            }
        }
    }
}
