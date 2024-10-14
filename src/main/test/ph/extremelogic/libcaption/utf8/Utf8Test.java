package ph.extremelogic.libcaption.utf8;

import org.junit.jupiter.api.Test;
import ph.extremelogic.libcaption.Utf8;

import static org.junit.jupiter.api.Assertions.*;
import static ph.extremelogic.libcaption.Utf8.utf8CharLength;

class Utf8Test {

    @Test
    void testUtf8CharLength() {
        // Test ASCII character (1 byte)
        assertEquals(1, utf8CharLength(new byte[]{0x41})); // 'A'

        // Test 2-byte UTF-8 character
        assertEquals(2, utf8CharLength(new byte[]{(byte)0xC3, (byte)0xA9})); // 'é'

        // Test 3-byte UTF-8 character
        assertEquals(3, utf8CharLength(new byte[]{(byte)0xE2, (byte)0x82, (byte)0xAC})); // '€'

        // Test 4-byte UTF-8 character
        assertEquals(4, utf8CharLength(new byte[]{(byte)0xF0, (byte)0x9F, (byte)0x98, (byte)0x80})); // '😀'

        // Test null input
        assertEquals(0, utf8CharLength(null));

        // Test empty array
        assertEquals(0, utf8CharLength(new byte[]{}));

        // Test array with null byte
        assertEquals(0, utf8CharLength(new byte[]{0x00}));

        // Test with a four-byte UTF-8 character ('😁')
        assertEquals(4, utf8CharLength(new byte[]{(byte)0xF0, (byte)0x9F, (byte)0x98, (byte)0x81}));

        // Test with an invalid start byte (0xFF)
        assertEquals(0, utf8CharLength(new byte[]{(byte)0xFF}));

        // Test with negative byte values (Java bytes are signed)
        assertEquals(2, utf8CharLength(new byte[]{(byte)0xC2, (byte)0xA2})); // '¢'

        // Test with a start byte indicating more bytes than provided
        assertEquals(2, utf8CharLength(new byte[]{(byte)0xC2}));

        // Test with the maximum valid Unicode code point (U+10FFFF)
        assertEquals(4, utf8CharLength(new byte[]{(byte)0xF4, (byte)0x8F, (byte)0xBF, (byte)0xBF}));

        // Test with an overlong encoding (invalid in UTF-8)
        // assertEquals(0, utf8CharLength(new byte[] {(byte)0xC0, (byte)0xAF}));

        // Test with a code point beyond the Unicode range (invalid)
        //assertEquals(0, utf8CharLength(new byte[] {(byte)0xF4, (byte)0x90, (byte)0x80, (byte)0x80}));
    }

    @Test
    void testUtf8CharWhitespace() {
        // Test space character
        assertTrue(Utf8.utf8CharWhitespace(new byte[]{0x20}));

        // Test tab character
        assertTrue(Utf8.utf8CharWhitespace(new byte[]{0x09}));

        // Test newline character
        assertTrue(Utf8.utf8CharWhitespace(new byte[]{0x0A}));

        // Test carriage return character
        assertTrue(Utf8.utf8CharWhitespace(new byte[]{0x0D}));

        // Test non-breaking space (U+00A0)
        assertTrue(Utf8.utf8CharWhitespace(new byte[]{(byte)0xC2, (byte)0xA0}));

        // Test non-whitespace character
        assertFalse(Utf8.utf8CharWhitespace(new byte[]{0x41})); // 'A'

        // Test null input
        assertFalse(Utf8.utf8CharWhitespace(null));

        // Test empty array
        assertFalse(Utf8.utf8CharWhitespace(new byte[]{}));
    }

    @Test
    void testUtf8CharCopy() {
        byte[] src = new byte[]{(byte)0xE2, (byte)0x82, (byte)0xAC}; // '€'
        byte[] dst = new byte[3];

        // Test successful copy
        assertEquals(3, Utf8.utf8CharCopy(dst, src));
        assertArrayEquals(src, dst);

        // Test copy with null destination
        assertEquals(3, Utf8.utf8CharCopy(null, src));

        // Test copy with null source
        assertEquals(0, Utf8.utf8CharCopy(dst, null));

        // Test copy with destination too small
        byte[] smallDst = new byte[2];
        assertEquals(3, Utf8.utf8CharCopy(smallDst, src));
        assertArrayEquals(new byte[2], smallDst); // Should not have been modified
    }
}
