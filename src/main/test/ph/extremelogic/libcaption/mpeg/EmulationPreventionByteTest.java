package ph.extremelogic.libcaption.mpeg;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EmulationPreventionByteTest {

    @Test
    public void testFindEmulationPreventionByte() {
        byte[] data = {
                (byte) 0xB5, (byte)0x00, 0x2F, 0x03, 0x3F, (byte) 0xD4, (byte) 0xFF, (byte) 0xFC, (byte) 0x80, (byte) 0x80,
                (byte) 0xFD, (byte) 0x80, (byte) 0x80, (byte) 0xFA, 0x00, 0x00, (byte) 0xFA, 0x00, 0x00,
                (byte) 0xFA, 0x00, 0x00, (byte) 0xFA, 0x00, 0x00, (byte) 0xFA, 0x00, 0x00, (byte) 0xFA,
                0x00, 0x00, (byte) 0xFA, 0x00, 0x00, (byte) 0xFA, 0x00, 0x00, (byte) 0xFA, 0x00, 0x00,
                (byte) 0xFA, 0x00, 0x00, (byte) 0xFA, 0x00, 0x00, (byte) 0xFA, 0x00, 0x00, (byte) 0xFA,
                0x00, 0x00, (byte) 0xFA, 0x00, 0x00, (byte) 0xFA, 0x00, 0x00, (byte) 0xFA, 0x00, 0x00,
                (byte) 0xFF
        };
        int expectedSize = 68;

        int result = mpeg_c.find_emulation_prevention_byte(data, expectedSize);
        assertEquals(expectedSize, result);  // Expected offset based on the input data

        byte [] data2 = {
                (byte) 0x32
        };
        expectedSize = 1;

        result = mpeg_c.find_emulation_prevention_byte(data2, expectedSize);
        assertEquals(expectedSize, result);  // Expected offset based on the input data
    }
}

