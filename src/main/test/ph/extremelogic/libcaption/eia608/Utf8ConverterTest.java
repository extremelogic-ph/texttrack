package ph.extremelogic.libcaption.eia608;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class Utf8ConverterTest {

    @Test
    public void testAsciiCharacters() {
        // Test basic ASCII characters
        assertEquals(0x1229, eia608_from_utf8.eia608_from_utf8("'"), "Failed for apostrophe");
        assertEquals(0x1228, eia608_from_utf8.eia608_from_utf8("*"), "Failed for asterisk");
        assertEquals(0x132B, eia608_from_utf8.eia608_from_utf8("\\"), "Failed for backslash");
        assertEquals(0x132C, eia608_from_utf8.eia608_from_utf8("^"), "Failed for caret");
        assertEquals(0x132D, eia608_from_utf8.eia608_from_utf8("_"), "Failed for underscore");
        assertEquals(0x1226, eia608_from_utf8.eia608_from_utf8("`"), "Failed for grave accent");
        assertEquals(0x1329, eia608_from_utf8.eia608_from_utf8("{"), "Failed for left curly bracket");
        assertEquals(0x132E, eia608_from_utf8.eia608_from_utf8("|"), "Failed for vertical line");
        assertEquals(0x132A, eia608_from_utf8.eia608_from_utf8("}"), "Failed for right curly bracket");
        assertEquals(0x132F, eia608_from_utf8.eia608_from_utf8("~"), "Failed for tilde");
    }

    @Test
    public void testExtendedLatinCharacters() {
        // Test UTF-8 characters in the extended Latin-1 range (C2xx)
        assertEquals(0x1139, eia608_from_utf8.eia608_from_utf8("\u00A0"), "Failed for NO_BREAK_SPACE");
        assertEquals(0x1227, eia608_from_utf8.eia608_from_utf8("\u00A1"), "Failed for INVERTED_EXCLAMATION_MARK");
        assertEquals(0x1135, eia608_from_utf8.eia608_from_utf8("\u00A2"), "Failed for CENT_SIGN");
        assertEquals(0x1136, eia608_from_utf8.eia608_from_utf8("\u00A3"), "Failed for POUND_SIGN");
        assertEquals(0x1336, eia608_from_utf8.eia608_from_utf8("\u00A4"), "Failed for CURRENCY_SIGN");
        assertEquals(0x1335, eia608_from_utf8.eia608_from_utf8("\u00A5"), "Failed for YEN_SIGN");
        assertEquals(0x1337, eia608_from_utf8.eia608_from_utf8("\u00A6"), "Failed for BROKEN_BAR");
        assertEquals(0x122B, eia608_from_utf8.eia608_from_utf8("\u00A9"), "Failed for COPYRIGHT_SIGN");
        assertEquals(0x123E, eia608_from_utf8.eia608_from_utf8("\u00AB"), "Failed for LEFT_POINTING_DOUBLE_ANGLE_QUOTATION_MARK");
        assertEquals(0x1130, eia608_from_utf8.eia608_from_utf8("\u00AE"), "Failed for REGISTERED_SIGN");
        assertEquals(0x1131, eia608_from_utf8.eia608_from_utf8("\u00B0"), "Failed for DEGREE_SIGN");
        assertEquals(0x123F, eia608_from_utf8.eia608_from_utf8("\u00BB"), "Failed for RIGHT_POINTING_DOUBLE_ANGLE_QUOTATION_MARK");
        assertEquals(0x1132, eia608_from_utf8.eia608_from_utf8("\u00BD"), "Failed for VULGAR_FRACTION_ONE_HALF");
        assertEquals(0x1133, eia608_from_utf8.eia608_from_utf8("\u00BF"), "Failed for INVERTED_QUESTION_MARK");
    }

    @Test
    public void testLatinCapitalLettersWithDiacritics() {
        // Test UTF-8 characters for Latin capital letters with diacritics (C3xx)
        assertEquals(0x1230, eia608_from_utf8.eia608_from_utf8("\u00C0"), "Failed for LATIN_CAPITAL_LETTER_A_WITH_GRAVE");
        assertEquals(0x1220, eia608_from_utf8.eia608_from_utf8("\u00C1"), "Failed for LATIN_CAPITAL_LETTER_A_WITH_ACUTE");
        assertEquals(0x1231, eia608_from_utf8.eia608_from_utf8("\u00C2"), "Failed for LATIN_CAPITAL_LETTER_A_WITH_CIRCUMFLEX");
        assertEquals(0x1320, eia608_from_utf8.eia608_from_utf8("\u00C3"), "Failed for LATIN_CAPITAL_LETTER_A_WITH_TILDE");
        assertEquals(0x1330, eia608_from_utf8.eia608_from_utf8("\u00C4"), "Failed for LATIN_CAPITAL_LETTER_A_WITH_DIAERESIS");
        assertEquals(0x1338, eia608_from_utf8.eia608_from_utf8("\u00C5"), "Failed for LATIN_CAPITAL_LETTER_A_WITH_RING_ABOVE");
        assertEquals(0x1232, eia608_from_utf8.eia608_from_utf8("\u00C7"), "Failed for LATIN_CAPITAL_LETTER_C_WITH_CEDILLA");
        assertEquals(0x1233, eia608_from_utf8.eia608_from_utf8("\u00C8"), "Failed for LATIN_CAPITAL_LETTER_E_WITH_GRAVE");
        assertEquals(0x1221, eia608_from_utf8.eia608_from_utf8("\u00C9"), "Failed for LATIN_CAPITAL_LETTER_E_WITH_ACUTE");
        assertEquals(0x1234, eia608_from_utf8.eia608_from_utf8("\u00CA"), "Failed for LATIN_CAPITAL_LETTER_E_WITH_CIRCUMFLEX");
        assertEquals(0x1235, eia608_from_utf8.eia608_from_utf8("\u00CB"), "Failed for LATIN_CAPITAL_LETTER_E_WITH_DIAERESIS");
    }

    @Test
    public void testSpecialCharacters() {
        // Test special cases
        assertEquals(0x0000, eia608_from_utf8.eia608_from_utf8(null), "Failed for null string");
        assertEquals(0x0000, eia608_from_utf8.eia608_from_utf8(""), "Failed for empty string");
        assertEquals(0x0000, eia608_from_utf8.eia608_from_utf8("\u007F"), "Failed for DEL character");
    }

    @Test
    public void testNonMappedCharacters() {
        // Test characters that are not explicitly mapped and should return 0x0000
        assertEquals(0x0000, eia608_from_utf8.eia608_from_utf8("\u20AC"), "Failed for Euro sign (should not be mapped)");
        assertEquals(0x0000, eia608_from_utf8.eia608_from_utf8("\u2030"), "Failed for Per mille sign (should not be mapped)");
    }
}

