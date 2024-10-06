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
package ph.extremelogic.libcaption.utf8;

/**
 * utf.c
 */
public class utf8_c {

    // returns the length of the char in bytes
    public static int utf8_char_length(byte[] c) {
        if (c == null || c.length == 0 || c[0] == 0) {
            return 0;
        }

        int[] UTF8_CHAR_LENGTH = {
                1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
                0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 2, 2, 3, 3, 4, 0
        };

        return UTF8_CHAR_LENGTH[(c[0] >> 3) & 0x1F];
    }

    // returns true if the first character is whitespace
    public static boolean utf8_char_whitespace(byte[] c) {
        if (c == null || c.length == 0) {
            return false;
        }

        // 0x7F is DEL
        if ((c[0] & 0xFF) <= ' ' || c[0] == 0x7F) {
            return true;
        }

        // EIA608_CHAR_NO_BREAK_SPACE
        if (c.length >= 2 && (c[0] & 0xFF) == 0xC2 && (c[1] & 0xFF) == 0xA0) {
            return true;
        }

        return false;
    }

    // copies a UTF-8 character
    public static int utf8_char_copy(byte[] dst, byte[] src) {
        int bytes = utf8_char_length(src);

        if (bytes > 0 && dst != null && dst.length >= bytes) {
            System.arraycopy(src, 0, dst, 0, bytes);
        }

        return bytes;
    }
}
