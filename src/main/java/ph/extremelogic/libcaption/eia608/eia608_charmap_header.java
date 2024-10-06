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
 * eia608_charmap.c
 */
public class eia608_charmap_header {
    public static final int EIA608_CHAR_COUNT = 176;

    // Helper char
    public static final String EIA608_CHAR_NULL = "";
    // Basic North American character set
    public static final String EIA608_CHAR_SPACE = "\u0020";
    public static final String EIA608_CHAR_EXCLAMATION_MARK = "\u0021";
    public static final String EIA608_CHAR_QUOTATION_MARK = "\"";
    public static final String EIA608_CHAR_NUMBER_SIGN = "\u0023";
    public static final String EIA608_CHAR_DOLLAR_SIGN = "\u0024";
    public static final String EIA608_CHAR_PERCENT_SIGN = "\u0025";
    public static final String EIA608_CHAR_AMPERSAND = "\u0026";
    public static final String EIA608_CHAR_LEFT_SINGLE_QUOTATION_MARK = "\u2018";
    public static final String EIA608_CHAR_LEFT_PARENTHESIS = "\u0028";
    public static final String EIA608_CHAR_RIGHT_PARENTHESIS = "\u0029";
    public static final String EIA608_CHAR_LATIN_SMALL_LETTER_A_WITH_ACUTE = "\u00E1";
    public static final String EIA608_CHAR_PLUS_SIGN = "\u002B";
    public static final String EIA608_CHAR_COMMA = "\u002C";
    public static final String EIA608_CHAR_HYPHEN_MINUS = "\u002D";
    public static final String EIA608_CHAR_FULL_STOP = "\u002E";
    public static final String EIA608_CHAR_SOLIDUS = "\u002F";

    // Basic North American character set
    public static final String EIA608_CHAR_DIGIT_ZERO = "\u0030";
    public static final String EIA608_CHAR_DIGIT_ONE = "\u0031";
    public static final String EIA608_CHAR_DIGIT_TWO = "\u0032";
    public static final String EIA608_CHAR_DIGIT_THREE = "\u0033";
    public static final String EIA608_CHAR_DIGIT_FOUR = "\u0034";
    public static final String EIA608_CHAR_DIGIT_FIVE = "\u0035";
    public static final String EIA608_CHAR_DIGIT_SIX = "\u0036";
    public static final String EIA608_CHAR_DIGIT_SEVEN = "\u0037";
    public static final String EIA608_CHAR_DIGIT_EIGHT = "\u0038";
    public static final String EIA608_CHAR_DIGIT_NINE = "\u0039";
    public static final String EIA608_CHAR_COLON = "\u003A";
    public static final String EIA608_CHAR_SEMICOLON = "\u003B";
    public static final String EIA608_CHAR_LESS_THAN_SIGN = "\u003C";
    public static final String EIA608_CHAR_EQUALS_SIGN = "\u003D";
    public static final String EIA608_CHAR_GREATER_THAN_SIGN = "\u003E";
    public static final String EIA608_CHAR_QUESTION_MARK = "\u003F";

    // Basic North American character set
    public static final String EIA608_CHAR_COMMERCIAL_AT = "\u0040";
    public static final String EIA608_CHAR_LATIN_CAPITAL_LETTER_A = "\u0041";
    public static final String EIA608_CHAR_LATIN_CAPITAL_LETTER_B = "\u0042";
    public static final String EIA608_CHAR_LATIN_CAPITAL_LETTER_C = "\u0043";
    public static final String EIA608_CHAR_LATIN_CAPITAL_LETTER_D = "\u0044";
    public static final String EIA608_CHAR_LATIN_CAPITAL_LETTER_E = "\u0045";
    public static final String EIA608_CHAR_LATIN_CAPITAL_LETTER_F = "\u0046";
    public static final String EIA608_CHAR_LATIN_CAPITAL_LETTER_G = "\u0047";
    public static final String EIA608_CHAR_LATIN_CAPITAL_LETTER_H = "\u0048";
    public static final String EIA608_CHAR_LATIN_CAPITAL_LETTER_I = "\u0049";
    public static final String EIA608_CHAR_LATIN_CAPITAL_LETTER_J = "\u004A";
    public static final String EIA608_CHAR_LATIN_CAPITAL_LETTER_K = "\u004B";
    public static final String EIA608_CHAR_LATIN_CAPITAL_LETTER_L = "\u004C";
    public static final String EIA608_CHAR_LATIN_CAPITAL_LETTER_M = "\u004D";
    public static final String EIA608_CHAR_LATIN_CAPITAL_LETTER_N = "\u004E";
    public static final String EIA608_CHAR_LATIN_CAPITAL_LETTER_O = "\u004F";

    // Basic North American character set
    public static final String EIA608_CHAR_LATIN_CAPITAL_LETTER_P = "\u0050";
    public static final String EIA608_CHAR_LATIN_CAPITAL_LETTER_Q = "\u0051";
    public static final String EIA608_CHAR_LATIN_CAPITAL_LETTER_R = "\u0052";
    public static final String EIA608_CHAR_LATIN_CAPITAL_LETTER_S = "\u0053";
    public static final String EIA608_CHAR_LATIN_CAPITAL_LETTER_T = "\u0054";
    public static final String EIA608_CHAR_LATIN_CAPITAL_LETTER_U = "\u0055";
    public static final String EIA608_CHAR_LATIN_CAPITAL_LETTER_V = "\u0056";
    public static final String EIA608_CHAR_LATIN_CAPITAL_LETTER_W = "\u0057";
    public static final String EIA608_CHAR_LATIN_CAPITAL_LETTER_X = "\u0058";
    public static final String EIA608_CHAR_LATIN_CAPITAL_LETTER_Y = "\u0059";
    public static final String EIA608_CHAR_LATIN_CAPITAL_LETTER_Z = "\u005A";
    public static final String EIA608_CHAR_LEFT_SQUARE_BRACKET = "\u005B";
    public static final String EIA608_CHAR_LATIN_SMALL_LETTER_E_WITH_ACUTE = "\u00E9";
    public static final String EIA608_CHAR_RIGHT_SQUARE_BRACKET = "\u005D";
    public static final String EIA608_CHAR_LATIN_SMALL_LETTER_I_WITH_ACUTE = "\u00ED";
    public static final String EIA608_CHAR_LATIN_SMALL_LETTER_O_WITH_ACUTE = "\u00F3";

    // Basic North American character set
    public static final String EIA608_CHAR_LATIN_SMALL_LETTER_U_WITH_ACUTE = "\u00FA";
    public static final String EIA608_CHAR_LATIN_SMALL_LETTER_A = "\u0061";
    public static final String EIA608_CHAR_LATIN_SMALL_LETTER_B = "\u0062";
    public static final String EIA608_CHAR_LATIN_SMALL_LETTER_C = "\u0063";
    public static final String EIA608_CHAR_LATIN_SMALL_LETTER_D = "\u0064";
    public static final String EIA608_CHAR_LATIN_SMALL_LETTER_E = "\u0065";
    public static final String EIA608_CHAR_LATIN_SMALL_LETTER_F = "\u0066";
    public static final String EIA608_CHAR_LATIN_SMALL_LETTER_G = "\u0067";
    public static final String EIA608_CHAR_LATIN_SMALL_LETTER_H = "\u0068";
    public static final String EIA608_CHAR_LATIN_SMALL_LETTER_I = "\u0069";
    public static final String EIA608_CHAR_LATIN_SMALL_LETTER_J = "\u006A";
    public static final String EIA608_CHAR_LATIN_SMALL_LETTER_K = "\u006B";
    public static final String EIA608_CHAR_LATIN_SMALL_LETTER_L = "\u006C";
    public static final String EIA608_CHAR_LATIN_SMALL_LETTER_M = "\u006D";
    public static final String EIA608_CHAR_LATIN_SMALL_LETTER_N = "\u006E";
    public static final String EIA608_CHAR_LATIN_SMALL_LETTER_O = "\u006F";

    // Basic North American character set
    public static final String EIA608_CHAR_LATIN_SMALL_LETTER_P = "\u0070";
    public static final String EIA608_CHAR_LATIN_SMALL_LETTER_Q = "\u0071";
    public static final String EIA608_CHAR_LATIN_SMALL_LETTER_R = "\u0072";
    public static final String EIA608_CHAR_LATIN_SMALL_LETTER_S = "\u0073";
    public static final String EIA608_CHAR_LATIN_SMALL_LETTER_T = "\u0074";
    public static final String EIA608_CHAR_LATIN_SMALL_LETTER_U = "\u0075";
    public static final String EIA608_CHAR_LATIN_SMALL_LETTER_V = "\u0076";
    public static final String EIA608_CHAR_LATIN_SMALL_LETTER_W = "\u0077";
    public static final String EIA608_CHAR_LATIN_SMALL_LETTER_X = "\u0078";
    public static final String EIA608_CHAR_LATIN_SMALL_LETTER_Y = "\u0079";
    public static final String EIA608_CHAR_LATIN_SMALL_LETTER_Z = "\u007A";
    public static final String EIA608_CHAR_LATIN_SMALL_LETTER_C_WITH_CEDILLA = "\u00E7";
    public static final String EIA608_CHAR_DIVISION_SIGN = "\u00F7";
    public static final String EIA608_CHAR_LATIN_CAPITAL_LETTER_N_WITH_TILDE = "\u00D1";
    public static final String EIA608_CHAR_LATIN_SMALL_LETTER_N_WITH_TILDE = "\u00F1";
    public static final String EIA608_CHAR_FULL_BLOCK = "\u2588";

    // Special North American character set[edit]
    public static final String EIA608_CHAR_REGISTERED_SIGN = "\u00AE";
    public static final String EIA608_CHAR_DEGREE_SIGN = "\u00B0";
    public static final String EIA608_CHAR_VULGAR_FRACTION_ONE_HALF = "\u00BD";
    public static final String EIA608_CHAR_INVERTED_QUESTION_MARK = "\u00BF";
    public static final String EIA608_CHAR_TRADE_MARK_SIGN = "\u2122";
    public static final String EIA608_CHAR_CENT_SIGN = "\u00A2";
    public static final String EIA608_CHAR_POUND_SIGN = "\u00A3";
    public static final String EIA608_CHAR_EIGHTH_NOTE = "\u266A";
    public static final String EIA608_CHAR_LATIN_SMALL_LETTER_A_WITH_GRAVE = "\u00E0";
    public static final String EIA608_CHAR_NO_BREAK_SPACE = "\u00A0";
    public static final String EIA608_CHAR_LATIN_SMALL_LETTER_E_WITH_GRAVE = "\u00E8";
    public static final String EIA608_CHAR_LATIN_SMALL_LETTER_A_WITH_CIRCUMFLEX = "\u00E2";
    public static final String EIA608_CHAR_LATIN_SMALL_LETTER_E_WITH_CIRCUMFLEX = "\u00EA";
    public static final String EIA608_CHAR_LATIN_SMALL_LETTER_I_WITH_CIRCUMFLEX = "\u00EE";
    public static final String EIA608_CHAR_LATIN_SMALL_LETTER_O_WITH_CIRCUMFLEX = "\u00F4";
    public static final String EIA608_CHAR_LATIN_SMALL_LETTER_U_WITH_CIRCUMFLEX = "\u00FB";

    // Extended Western European character set : Extended Spanish/Miscellaneous
    public static final String EIA608_CHAR_LATIN_CAPITAL_LETTER_A_WITH_ACUTE = "\u00C1";
    public static final String EIA608_CHAR_LATIN_CAPITAL_LETTER_E_WITH_ACUTE = "\u00C9";
    public static final String EIA608_CHAR_LATIN_CAPITAL_LETTER_O_WITH_ACUTE = "\u00D3";
    public static final String EIA608_CHAR_LATIN_CAPITAL_LETTER_U_WITH_ACUTE = "\u00DA";
    public static final String EIA608_CHAR_LATIN_CAPITAL_LETTER_U_WITH_DIAERESIS = "\u00DC";
    public static final String EIA608_CHAR_LATIN_SMALL_LETTER_U_WITH_DIAERESIS = "\u00FC";
    public static final String EIA608_CHAR_RIGHT_SINGLE_QUOTATION_MARK = "\u2019";
    public static final String EIA608_CHAR_INVERTED_EXCLAMATION_MARK = "\u00A1";
    public static final String EIA608_CHAR_ASTERISK = "\u002A";
    public static final String EIA608_CHAR_APOSTROPHE = "\u0027";
    public static final String EIA608_CHAR_EM_DASH = "\u2014";
    public static final String EIA608_CHAR_COPYRIGHT_SIGN = "\u00A9";
    public static final String EIA608_CHAR_SERVICE_MARK = "\u2120";
    public static final String EIA608_CHAR_BULLET = "\u2022";
    public static final String EIA608_CHAR_LEFT_DOUBLE_QUOTATION_MARK = "\u201C";
    public static final String EIA608_CHAR_RIGHT_DOUBLE_QUOTATION_MARK = "\u201D";

    // Extended Western European character set : Extended French
    public static final String EIA608_CHAR_LATIN_CAPITAL_LETTER_A_WITH_GRAVE = "\u00C0";
    public static final String EIA608_CHAR_LATIN_CAPITAL_LETTER_A_WITH_CIRCUMFLEX = "\u00C2";
    public static final String EIA608_CHAR_LATIN_CAPITAL_LETTER_C_WITH_CEDILLA = "\u00C7";
    public static final String EIA608_CHAR_LATIN_CAPITAL_LETTER_E_WITH_GRAVE = "\u00C8";
    public static final String EIA608_CHAR_LATIN_CAPITAL_LETTER_E_WITH_CIRCUMFLEX = "\u00CA";
    public static final String EIA608_CHAR_LATIN_CAPITAL_LETTER_E_WITH_DIAERESIS = "\u00CB";
    public static final String EIA608_CHAR_LATIN_SMALL_LETTER_E_WITH_DIAERESIS = "\u00EB";
    public static final String EIA608_CHAR_LATIN_CAPITAL_LETTER_I_WITH_CIRCUMFLEX = "\u00CE";
    public static final String EIA608_CHAR_LATIN_CAPITAL_LETTER_I_WITH_DIAERESIS = "\u00CF";
    public static final String EIA608_CHAR_LATIN_SMALL_LETTER_I_WITH_DIAERESIS = "\u00EF";
    public static final String EIA608_CHAR_LATIN_CAPITAL_LETTER_O_WITH_CIRCUMFLEX = "\u00D4";
    public static final String EIA608_CHAR_LATIN_CAPITAL_LETTER_U_WITH_GRAVE = "\u00D9";
    public static final String EIA608_CHAR_LATIN_SMALL_LETTER_U_WITH_GRAVE = "\u00F9";
    public static final String EIA608_CHAR_LATIN_CAPITAL_LETTER_U_WITH_CIRCUMFLEX = "\u00DB";
    public static final String EIA608_CHAR_LEFT_POINTING_DOUBLE_ANGLE_QUOTATION_MARK = "\u00AB";
    public static final String EIA608_CHAR_RIGHT_POINTING_DOUBLE_ANGLE_QUOTATION_MARK = "\u00BB";

    // Extended Western European character set : Portuguese
    public static final String EIA608_CHAR_LATIN_CAPITAL_LETTER_A_WITH_TILDE = "\u00C3";
    public static final String EIA608_CHAR_LATIN_SMALL_LETTER_A_WITH_TILDE = "\u00E3";
    public static final String EIA608_CHAR_LATIN_CAPITAL_LETTER_I_WITH_ACUTE = "\u00CD";
    public static final String EIA608_CHAR_LATIN_CAPITAL_LETTER_I_WITH_GRAVE = "\u00CC";
    public static final String EIA608_CHAR_LATIN_SMALL_LETTER_I_WITH_GRAVE = "\u00EC";
    public static final String EIA608_CHAR_LATIN_CAPITAL_LETTER_O_WITH_GRAVE = "\u00D2";
    public static final String EIA608_CHAR_LATIN_SMALL_LETTER_O_WITH_GRAVE = "\u00F2";
    public static final String EIA608_CHAR_LATIN_CAPITAL_LETTER_O_WITH_TILDE = "\u00D5";
    public static final String EIA608_CHAR_LATIN_SMALL_LETTER_O_WITH_TILDE = "\u00F5";
    public static final String EIA608_CHAR_LEFT_CURLY_BRACKET = "\u007B";
    public static final String EIA608_CHAR_RIGHT_CURLY_BRACKET = "\u007D";
    public static final String EIA608_CHAR_REVERSE_SOLIDUS = "\\\\";
    public static final String EIA608_CHAR_CIRCUMFLEX_ACCENT = "\u005E";
    public static final String EIA608_CHAR_LOW_LINE = "\u005F";
    public static final String EIA608_CHAR_VERTICAL_LINE = "\u007C";
    public static final String EIA608_CHAR_TILDE = "\u007E";

    // Extended Western European character set : German/Danish
    public static final String EIA608_CHAR_LATIN_CAPITAL_LETTER_A_WITH_DIAERESIS = "\u00C4";
    public static final String EIA608_CHAR_LATIN_SMALL_LETTER_A_WITH_DIAERESIS = "\u00E4";
    public static final String EIA608_CHAR_LATIN_CAPITAL_LETTER_O_WITH_DIAERESIS = "\u00D6";
    public static final String EIA608_CHAR_LATIN_SMALL_LETTER_O_WITH_DIAERESIS = "\u00F6";
    public static final String EIA608_CHAR_LATIN_SMALL_LETTER_SHARP_S = "\u00DF";
    public static final String EIA608_CHAR_YEN_SIGN = "\u00A5";
    public static final String EIA608_CHAR_CURRENCY_SIGN = "\u00A4";
    public static final String EIA608_CHAR_BROKEN_BAR = "\u00A6";
    public static final String EIA608_CHAR_LATIN_CAPITAL_LETTER_A_WITH_RING_ABOVE = "\u00C5";
    public static final String EIA608_CHAR_LATIN_SMALL_LETTER_A_WITH_RING_ABOVE = "\u00E5";
    public static final String EIA608_CHAR_LATIN_CAPITAL_LETTER_O_WITH_STROKE = "\u00D8";
    public static final String EIA608_CHAR_LATIN_SMALL_LETTER_O_WITH_STROKE = "\u00F8";
    public static final String EIA608_CHAR_BOX_DRAWINGS_LIGHT_DOWN_AND_RIGHT = "\u250C"; // top left
    public static final String EIA608_CHAR_BOX_DRAWINGS_LIGHT_DOWN_AND_LEFT = "\u2510"; // top right
    public static final String EIA608_CHAR_BOX_DRAWINGS_LIGHT_UP_AND_RIGHT = "\u2514"; // lower left
    public static final String EIA608_CHAR_BOX_DRAWINGS_LIGHT_UP_AND_LEFT = "\u2518"; // bottom right
}