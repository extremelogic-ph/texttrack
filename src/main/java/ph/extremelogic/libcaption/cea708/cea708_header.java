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

import static ph.extremelogic.libcaption.cea708.cea708_header.itu_t_t35_provider_code_t.t35_provider_atsc;

// Method to return the appropriate tab offset based on the value
public class cea708_header {

    // Method to encode caption data (equivalent to cea708_encode_cc_data in C)
    public static cc_data_t encodeCcData(boolean ccValid, cc_type_t type, int ccData) {
        return new cc_data_t(0, ccValid, type, ccData);
    }

    // Method to get the count of caption data (equivalent to cea708_cc_count in C)
    public static int getCcCount(user_data_t data) {
        return data.ccCount;
    }

    // Enumeration for CEA708 caption types
    public enum cc_type_t {
        NTSC_CC_FIELD_1(0),
        NTSC_CC_FIELD_2(1),
        DTVCC_PACKET_DATA(2),
        DTVCC_PACKET_START(3);

        private final int value;

        cc_type_t(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    public enum itu_t_t35_country_code_t {
        country_united_states(181);

        private final int value;

        itu_t_t35_country_code_t(int value) {
            this.value = value;
        }

        // Static method to retrieve enum constant by value
        public static itu_t_t35_country_code_t fromValue(int value) {
            for (itu_t_t35_country_code_t countryCode : itu_t_t35_country_code_t.values()) {
                if (countryCode.getValue() == value) {
                    return countryCode;
                }
            }
            throw new IllegalArgumentException("Invalid value: " + value);
        }

        public int getValue() {
            return value;
        }
    }

    public enum itu_t_t35_provider_code_t {
        t35_provider_direct_tv(47),
        t35_provider_atsc(49);

        private final int value;

        itu_t_t35_provider_code_t(int value) {
            this.value = value;
        }

        public static itu_t_t35_provider_code_t fromValue(int value) {
            for (itu_t_t35_provider_code_t providerCode : itu_t_t35_provider_code_t.values()) {
                if (providerCode.getValue() == value) {
                    return providerCode;
                }
            }
            throw new IllegalArgumentException("Invalid value: " + value);
        }

        public int getValue() {
            return value;
        }
    }

    // Class to represent caption data (cc_data_t in C)
    public static class cc_data_t {
        public int markerBits; // 5 bits
        public boolean ccValid; // 1 bit
        public cc_type_t ccType; // 2 bits
        public int ccData; // 16 bits

        // Constructor to initialize the values
        public cc_data_t(int markerBits, boolean ccValid, cc_type_t ccType, int ccData) {
            this.markerBits = markerBits;
            this.ccValid = ccValid;
            this.ccType = ccType;
            this.ccData = ccData;
        }
    }

    // Class to represent user data (user_data_t in C)
    public static class user_data_t {
        public boolean processEmDataFlag; // 1 bit
        public boolean processCcDataFlag; // 1 bit
        public boolean additionalDataFlag; // 1 bit
        public int ccCount; // 5 bits
        public int emData; // 8 bits
        public cc_data_t[] ccData = new cc_data_t[32]; // Array of caption data

        // Constructor to initialize the user data
        public user_data_t() {
        }

        public user_data_t(boolean processEmDataFlag, boolean processCcDataFlag, boolean additionalDataFlag, int ccCount, int emData) {
            this.processEmDataFlag = processEmDataFlag;
            this.processCcDataFlag = processCcDataFlag;
            this.additionalDataFlag = additionalDataFlag;
            this.ccCount = ccCount;
            this.emData = emData;
            // Initialize the ccData array
            for (int i = 0; i < 32; i++) {
                ccData[i] = new cc_data_t(0, false, cc_type_t.NTSC_CC_FIELD_1, 0);
            }
        }
    }

    public static class cea708_t {
        public itu_t_t35_country_code_t country;
        public itu_t_t35_provider_code_t provider;
        public int user_identifier;
        public byte user_data_type_code;
        public byte directv_user_data_length;
        public user_data_t user_data;
        public double timestamp;

        public cea708_t(cea708_t t) {
            this.country = t.country;
            this.provider = t.provider;
            this.user_identifier = t.user_identifier;
            this.user_data_type_code = t.user_data_type_code;
            this.directv_user_data_length = t.directv_user_data_length;
            this.user_data = t.user_data;
            this.timestamp = t.timestamp;
        }

        public cea708_t() {
            this.user_data = new user_data_t(false, true, false, 0, 0xFF);
        }

        public cea708_t(itu_t_t35_country_code_t country, itu_t_t35_provider_code_t provider, int user_identifier, byte user_data_type_code, byte directv_user_data_length, user_data_t user_data, double timestamp) {
            this.country = country;
            this.provider = provider;
            this.user_identifier = user_identifier;
            this.user_data_type_code = user_data_type_code;
            this.directv_user_data_length = directv_user_data_length;
            this.user_data = user_data;
            this.timestamp = timestamp;
        }

        public boolean init(double timestamp) {
            this.country = itu_t_t35_country_code_t.country_united_states;
            this.provider = t35_provider_atsc;
            this.user_identifier = 0x47413934; // GA94;
            this.user_data_type_code = 3;
            this.directv_user_data_length = 0;
            this.user_data.processEmDataFlag = false;
            this.user_data.processCcDataFlag = true;
            this.user_data.additionalDataFlag = false;
            this.user_data.emData = 0xFF;
            this.user_data.ccCount = 0;
            this.timestamp = timestamp;
            return true;
        }
    }
}
