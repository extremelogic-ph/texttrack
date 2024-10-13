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

import ph.extremelogic.texttrack.utils.ArrayUtil;
import ph.extremelogic.texttrack.utils.Debug;

/**
 * The {@code Eia608Decoder} class provides utility methods for processing
 * EIA-608 closed captioning data. It includes methods for computing parity,
 * verifying data integrity, and identifying control and special character sets
 * used in EIA-608 encoding.
 *
 * This class is not instantiable and contains only static methods.
 */
public class Eia608Decoder {

    /**
     * A precomputed table for EIA-608 parity checking.
     */
    protected static final int[] EIA_608_PARITY_TABLE = ArrayUtil.concatenate(computeEIA608B1(0), computeEIA608B1(64));

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private Eia608Decoder() {
        // Prevent instantiation
    }

    /**
     * Computes the BX value based on a shift of the byte B by X bits.
     *
     * @param b the byte to shift
     * @param x the number of positions to shift
     * @return the computed BX value
     */
    public static int computeEIA608BX(int b, int x) {
        return (b << x) & 0x80;
    }

    /**
     * Computes the BP value by applying parity bits to byte B.
     *
     * @param b the byte for which the parity bits are computed
     * @return the byte with applied parity bits
     */
    public static int computeEIA608BP(int b) {
        return (b & 0x7F) | (0x80 ^ computeEIA608BX(b, 1) ^ computeEIA608BX(b, 2) ^ computeEIA608BX(b, 3) ^
                computeEIA608BX(b, 4) ^ computeEIA608BX(b, 5) ^ computeEIA608BX(b, 6) ^ computeEIA608BX(b, 7));
    }

    /**
     * Generates an array of BP values for the byte B and its 8 successors.
     *
     * @param b the initial byte
     * @return an array of 8 BP values
     */
    public static int[] computeEIA608B2(int b) {
        int[] result = new int[8];
        for (int i = 0; i < 8; i++) {
            result[i] = computeEIA608BP(b + i);
        }
        return result;
    }

    /**
     * Generates an array of 64 BP values starting from the byte B.
     *
     * @param b the initial byte
     * @return an array of 64 BP values
     */
    public static int[] computeEIA608B1(int b) {
        int[] result = new int[64];
        for (int i = 0; i < 8; i++) {
            System.arraycopy(computeEIA608B2(b + (i * 8)), 0, result, i * 8, 8);
        }
        return result;
    }

    /**
     * Returns the parity byte for a given ccData value.
     *
     * @param ccData the closed caption data
     * @return the parity byte
     */
    public static int eia608ParityByte(int ccData) {
        return EIA_608_PARITY_TABLE[0x7F & ccData];
    }

    /**
     * Computes the parity word for a given ccData value.
     *
     * @param ccData the closed caption data
     * @return the parity word
     */
    public static int eia608ParityWord(int ccData) {
        return (eia608ParityByte(ccData >> 8) << 8) | eia608ParityByte(ccData);
    }

    /**
     * Calculates the parity for the given ccData value.
     *
     * @param ccData the closed caption data
     * @return the calculated parity
     */
    public static int eia608Parity(int ccData) {
        return eia608ParityWord(ccData);
    }

    /**
     * Verifies whether the parity word matches the original ccData.
     *
     * @param ccData the closed caption data to verify
     * @return {@code true} if the parity is correct, {@code false} otherwise
     */

    public static boolean eia608ParityVerify(int ccData) {
        return eia608ParityWord(ccData) == ccData;
    }

    /**
     * Strips the parity bits from the ccData.
     *
     * @param ccData the closed caption data
     * @return the ccData without parity bits
     */

    public static int eia608ParityStrip(int ccData) {
        return ccData & 0x7F7F;
    }

    /**
     * Tests if the second channel bit is set in the ccData.
     *
     * @param ccData the closed caption data
     * @return {@code true} if the second channel bit is set, {@code false} otherwise
     */
    public static boolean eia608TestSecondChannelBit(int ccData) {
        return (ccData & 0x0800) != 0;
    }

    /**
     * Checks whether the ccData represents basicna type data.
     *
     * @param ccData the closed caption data
     * @return {@code true} if the data is of basicna type, {@code false} otherwise
     */
    public static boolean eia608IsBasicna(int ccData) {
        Debug.print("eia608_is_basicna " + ((0x6000 & ccData) != 0 ? 1 : 0));
        return (0x6000 & ccData) != 0;
    }

    /**
     * Checks whether the ccData is a preamble.
     *
     * @param ccData the closed caption data
     * @return {@code true} if the data is a preamble, {@code false} otherwise
     */
    public static boolean eia608IsPreamble(int ccData) {
        Debug.print("cc_data " + ccData);
        Debug.print("0x7040 & cc_data " + (0x7040 & ccData));
        Debug.print("eia608_is_preamble " + ((0x1040 == (0x7040 & ccData)) ? 1 : 0));
        return (0x1040 == (0x7040 & ccData));
    }

    /**
     * Checks if the ccData contains a midrow change.
     *
     * @param ccData the closed caption data
     * @return {@code true} if the data is a midrow change, {@code false} otherwise
     */
    public static boolean eia608IsMidrowChange(int ccData) {
        return (0x1120 == (0x7770 & ccData));
    }

    /**
     * Checks if the ccData is of specialna type.
     *
     * @param ccData the closed caption data
     * @return {@code true} if the data is of specialna type, {@code false} otherwise
     */
    public static boolean eia608IsSpecialna(int ccData) {
        Debug.print("eia608_is_specialna " + ((0x1130 == (0x7770 & ccData)) ? 1 : 0));
        return (0x1130 == (0x7770 & ccData));
    }

    /**
     * Checks if the ccData is XDS (Extended Data Services) data.
     *
     * @param ccData the closed caption data
     * @return {@code true} if the data is XDS, {@code false} otherwise
     */
    public static boolean eia608IsXDS(int ccData) {
        return (0x0000 == (0x7070 & ccData) && (0x0000 != (0x0F0F & ccData)));
    }

    /**
     * Checks if the ccData is West European data.
     *
     * @param ccData the closed caption data
     * @return {@code true} if the data is West European, {@code false} otherwise
     */
    public static boolean eia608IsWestEU(int ccData) {
        Debug.print("eia608_is_westeu");
        return (0x1220 == (0x7660 & ccData));
    }

    /**
     * Checks if the ccData is control data.
     *
     * @param ccData the closed caption data
     * @return {@code true} if the data is control data, {@code false} otherwise
     */
    public static boolean eia608IsControl(int ccData) {
        return (0x1420 == (0x7670 & ccData) || 0x1720 == (0x7770 & ccData));
    }

    /**
     * Checks if the ccData is Norpak data.
     *
     * @param ccData the closed caption data
     * @return {@code true} if the data is Norpak, {@code false} otherwise
     */
    public static boolean eia608IsNorpak(int ccData) {
        return (0x1724 == (0x777C & ccData) || 0x1728 == (0x777C & ccData));
    }

    /**
     * Checks if the ccData is padding.
     *
     * @param ccData the closed caption data
     * @return {@code true} if the data is padding, {@code false} otherwise
     */
    public static boolean eia608IsPadding(int ccData) {
        return (0x8080 == ccData);
    }

    /**
     * Returns the appropriate tab offset control based on the given offset value.
     *
     * @param offset the tab offset value (0 to 3)
     * @return the corresponding {@link Eia608Control} value
     * @throws IllegalArgumentException if the offset is invalid
     */

    public static Eia608Control eia608Tab(int offset) {
        return switch (offset) {
            case 0 -> Eia608Control.TAB_OFFSET_0;
            case 1 -> Eia608Control.TAB_OFFSET_1;
            case 2 -> Eia608Control.TAB_OFFSET_2;
            case 3 -> Eia608Control.TAB_OFFSET_3;
            default -> throw new IllegalArgumentException("Invalid tab offset: " + offset);
        };
    }
}
