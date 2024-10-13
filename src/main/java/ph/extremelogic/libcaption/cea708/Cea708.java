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
package ph.extremelogic.libcaption.cea708;

import ph.extremelogic.libcaption.caption.CaptionFrame;
import ph.extremelogic.libcaption.constant.CcType;
import ph.extremelogic.libcaption.constant.ItuT35CountryCode;
import ph.extremelogic.libcaption.constant.ItuTt35ProviderCode;
import ph.extremelogic.libcaption.constant.LibCaptionStatus;
import ph.extremelogic.libcaption.model.CcData;
import ph.extremelogic.libcaption.model.UserData;
import ph.extremelogic.texttrack.TextTrackException;
import ph.extremelogic.texttrack.utils.Debug;

import java.util.Arrays;

import static ph.extremelogic.texttrack.utils.Debug.printDataArray;

/**
 * The {@code Cea708} class provides utilities for working with CEA-708 closed captioning data.
 * This includes initialization, parsing, and encoding of closed caption data, as well as converting
 * the parsed data to caption frames.
 */
public class Cea708 {
    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private Cea708() {
        // Prevent instantiation
    }

    /**
     * Initializes a {@code Cea708Data} object with default values based on CEA-708 standards.
     *
     * @param cea708Data the {@code Cea708Data} object to initialize
     * @param timestamp the timestamp associated with the data
     * @return {@code 1} to indicate successful initialization
     */
    public static int init(Cea708Data cea708Data, double timestamp) {
        cea708Data.setCountry(ItuT35CountryCode.COUNTRY_UNITED_STATES);
        cea708Data.setProvider(ItuTt35ProviderCode.T_35_PROVIDER_ATSC);
        cea708Data.setUserIdentifier(0x47413934); // GA94
        cea708Data.setUserDataTypeCode((byte) 3);
        cea708Data.setDirectvUserDataLength((byte) 0);
        cea708Data.getUserData().setProcessEmDataFlag(false);
        cea708Data.getUserData().setProcessCcDataFlag(true);
        cea708Data.getUserData().setAdditionalDataFlag(false);
        cea708Data.getUserData().setEmData(0xFF);
        cea708Data.getUserData().setCcCount(0);
        cea708Data.setTimestamp(timestamp);
        return 1;
    }

    /**
     * Parses the user data type structure from the byte array, equivalent to
     * {@code cea708_parse_user_data_type_strcture} in C.
     *
     * @param data the byte array containing the CEA-708 data
     * @param size the size of the data in bytes
     * @param userData the {@code UserData} object to populate
     * @return the populated {@code UserData} object
     */
    private static UserData parseUserDataTypeStructure(byte[] data, int size, UserData userData) {
        userData.setProcessEmDataFlag((data[0] & 0x80) != 0);
        userData.setProcessCcDataFlag((data[0] & 0x40) != 0);
        userData.setAdditionalDataFlag((data[0] & 0x20) != 0);
        userData.setCcCount(data[0] & 0x1F);
        userData.setEmData(data[1] & 0xFF);

        int offset = 2;
        for (int i = 0; offset + 3 <= size && i < userData.getCcCount(); i++) {
            userData.getCcData()[i].setMarkerBits((data[offset] >> 3) & 0xFF);
            userData.getCcData()[i].setCcValid((((data[offset] >> 2) & 0xFF) & 0x01) != 0);
            userData.getCcData()[i].setCcType(CcType.values()[((data[offset] & 0xFF) & 0x03)]);
            userData.getCcData()[i].setCcPayload(((data[offset + 1] & 0xFF) << 8) | (data[offset + 2] & 0xFF));
            offset += 3;
        }
        return userData;
    }

    /**
     * Parses H.264 video stream data for CEA-708 captioning, equivalent to {@code cea708_parse_h264} in C.
     *
     * @param data the byte array containing the H.264 video stream data
     * @param size the size of the data in bytes
     * @param cea708Data the {@code Cea708Data} object to populate
     * @return the status of the parsing operation, represented as a {@code LibCaptionStatus}
     */
    public static LibCaptionStatus parseH264(byte[] data, int size, Cea708Data cea708Data) {
        Debug.print("cea708_parse_h264 [START] <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
        printDataArray(data, data.length);

        if (size < 3) {
            return LibCaptionStatus.ERROR;
        }

        Debug.print("  country: " + (data[0] & 0xFF));
        Debug.print("  provider: " + ((data[1] << 8) | (data[2] & 0xFF)));
        cea708Data.setCountry(ItuT35CountryCode.fromValue(data[0] & 0xFF));
        cea708Data.setProvider(ItuTt35ProviderCode.fromValue((data[1] << 8) | (data[2] & 0xFF)));
        cea708Data.setUserIdentifier(0);
        cea708Data.setUserDataTypeCode((byte) 0);

        int offset = 3;

        Debug.print("  data[0]: " + (data[offset] & 0xFF));
        Debug.print("  data[1]: " + (data[offset + 1] & 0xFF));
        Debug.print("  data[2]: " + (data[offset + 2] & 0xFF));

        if (cea708Data.getProvider() == ItuTt35ProviderCode.T_35_PROVIDER_ATSC) {
            Debug.print("" + size + " - < 4");
            if (size - offset < 4) {
                return LibCaptionStatus.ERROR;
            }

            cea708Data.setUserIdentifier(((data[offset] << 24) | (data[offset + 1] << 16) | (data[offset + 2] << 8) | data[offset + 3]));
            Debug.print("user identifier: " + cea708Data.getUserIdentifier());
            offset += 4;
        }

        if (cea708Data.getProvider() == ItuTt35ProviderCode.T_35_PROVIDER_DIRECT_TV || cea708Data.getProvider() == ItuTt35ProviderCode.T_35_PROVIDER_ATSC) {
            if (size - offset < 1) {
                return LibCaptionStatus.ERROR;
            }
            cea708Data.setUserDataTypeCode(data[offset]);
            Debug.print("user data type code: " + cea708Data.getUserDataTypeCode());
            offset += 1;
        }

        if (cea708Data.getProvider() == ItuTt35ProviderCode.T_35_PROVIDER_DIRECT_TV) {
            if (size - offset < 1) {
                return LibCaptionStatus.ERROR;
            }
            cea708Data.setDirectvUserDataLength(data[offset]);
            Debug.print("user data length: " + cea708Data.getDirectvUserDataLength());
            offset += 1;
        }

        if (cea708Data.getUserDataTypeCode() == 3 && size - offset >= 2) {
            data = Arrays.copyOfRange(data, offset, data.length);
            Debug.print("cea708_parse_user_data_type_strcture before");
            printDataArray(data, data.length);
            UserData userData = parseUserDataTypeStructure(data, data.length, cea708Data.getUserData());
            cea708Data.setUserData(userData);
            Debug.print("cea708_parse_user_data_type_strcture result");
            printDataArray(data, data.length);
        } else {
            throw new TextTrackException("Unsupported CEA708 data type code " + cea708Data.getUserDataTypeCode());
        }
        return LibCaptionStatus.OK;
    }

    /**
     * Converts parsed CEA-708 data into a {@code CaptionFrame}, handling valid CC data.
     *
     * @param frame the {@code CaptionFrame} object to populate
     * @param cea708Data the {@code Cea708Data} containing the parsed data
     * @return the status of the operation, represented as a {@code LibCaptionStatus}
     */
    public static LibCaptionStatus toCaptionFrame(CaptionFrame frame, Cea708Data cea708Data) {
        int count = cea708Data.getUserData().getCcCount();
        LibCaptionStatus status = LibCaptionStatus.OK;

        for (int i = 0; i < count; i++) {
            boolean valid = cea708Data.getUserData().getCcData()[i].isCcValid();
            CcType type = cea708Data.getUserData().getCcData()[i].getCcType();
            int ccData = cea708Data.getUserData().getCcData()[i].getCcPayload();

            if (valid && type == CcType.NTSC_CC_FIELD_1) {
                status = frame.decode(ccData, cea708Data.getTimestamp());
            }
        }

        return status;
    }

    /**
     * Encodes caption data into a {@code CcData} object, equivalent to {@code cea708_encode_cc_data} in C.
     *
     * @param ccValid whether the caption data is valid
     * @param type the type of caption data (e.g., NTSC field)
     * @param ccData the actual closed caption data
     * @return a {@code CcData} object representing the encoded caption data
     */
    public static CcData encodeCcData(boolean ccValid, CcType type, int ccData) {
        return new CcData(0, ccValid, type, ccData);
    }

    /**
     * Retrieves the closed caption data count, equivalent to {@code cea708_cc_count} in C.
     *
     * @param data the {@code UserData} containing closed caption data
     * @return the count of closed caption data units
     */
    public static int getCcCount(UserData data) {
        return data.getCcCount();
    }
}