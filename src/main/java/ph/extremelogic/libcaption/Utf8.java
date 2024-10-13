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
package ph.extremelogic.libcaption;

/**
 * The {@code Utf8} class provides utility methods for working with UTF-8 encoded characters.
 * It includes methods for determining the length of a UTF-8 character, checking for whitespace characters,
 * and copying UTF-8 characters between byte arrays.
 *
 * This class is not instantiable and only provides static utility methods.
 */
public class Utf8 {
    /**
     * The default maximum file size for UTF-8 processing, set to 50 MB.
     */
    public static final int UFTF_DEFAULT_MAX_FILE_SIZE = 50 * 1024 * 1024;

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private Utf8() {
        // Prevent instantiation
    }

    /**
     * Determines the length of a UTF-8 character based on the first byte of the provided byte array.
     *
     * @param c the byte array containing the UTF-8 character
     * @return the length of the character in bytes, or 0 if the input is null or invalid
     */
    public static int utf8CharLength(byte[] c) {
        if (c == null || c.length == 0 || c[0] == 0) {
            return 0;
        }

        int[] utf8CharLength = {
                1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
                0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 2, 2, 3, 3, 4, 0
        };

        return utf8CharLength[(c[0] >> 3) & 0x1F];
    }

    /**
     * Determines if the first character in the provided byte array is a whitespace character.
     * Whitespace characters include space, tab, new line, and other control characters.
     *
     * @param c the byte array containing the UTF-8 character
     * @return {@code true} if the first character is a whitespace character, {@code false} otherwise
     */

    public static boolean utf8CharWhitespace(byte[] c) {
        if (c == null || c.length == 0) {
            return false;
        }

        // 0x7F is DEL
        if ((c[0] & 0xFF) <= ' ' || c[0] == 0x7F) {
            return true;
        }

        // EIA608_CHAR_NO_BREAK_SPACE
        return c.length >= 2 && (c[0] & 0xFF) == 0xC2 && (c[1] & 0xFF) == 0xA0;
    }

    /**
     * Copies a UTF-8 character from the source byte array to the destination byte array.
     *
     * @param dst the destination byte array where the character will be copied
     * @param src the source byte array containing the UTF-8 character to be copied
     * @return the number of bytes copied, or 0 if the source array is invalid or the destination array is too small
     */
    public static int utf8CharCopy(byte[] dst, byte[] src) {
        int bytes = utf8CharLength(src);

        if (bytes > 0 && dst != null && dst.length >= bytes) {
            System.arraycopy(src, 0, dst, 0, bytes);
        }

        return bytes;
    }
}
