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

import ph.extremelogic.libcaption.caption.caption_c;
import ph.extremelogic.libcaption.caption.caption_header;
import ph.extremelogic.texttrack.utils.Debug;

import java.util.Arrays;

import static ph.extremelogic.libcaption.mpeg.mpeg_c.printDataArray;

public class cea708_c {
    public static int cea708_init(cea708_header.cea708_t cea708, double timestamp) {
        cea708.country = cea708_header.itu_t_t35_country_code_t.country_united_states;
        cea708.provider = cea708_header.itu_t_t35_provider_code_t.t35_provider_atsc;
        cea708.user_identifier = 0x47413934; // GA94
        cea708.user_data_type_code = 3;
        cea708.directv_user_data_length = 0;
        cea708.user_data.processEmDataFlag = false;
        cea708.user_data.processCcDataFlag = true;
        cea708.user_data.additionalDataFlag = false;
        cea708.user_data.emData = 0xFF;
        cea708.user_data.ccCount = 0;
        cea708.timestamp = timestamp;
        return 1;
    }

    // Parse the user data type structure (equivalent to cea708_parse_user_data_type_strcture in C)
    public static cea708_header.user_data_t cea708_parse_user_data_type_strcture(byte[] data, int size, cea708_header.user_data_t userData) {
      //  Debug.print("cea708_parse_user_data_type_strcture <<<<<<<<<<<<<<<<<<<<");

        userData.processEmDataFlag = (data[0] & 0x80) != 0;
        userData.processCcDataFlag = (data[0] & 0x40) != 0;
        userData.additionalDataFlag = (data[0] & 0x20) != 0;
        userData.ccCount = data[0] & 0x1F;
        userData.emData = data[1] & 0xFF;
        //Debug.print(" - ccCount: " + userData.ccCount);

        int offset = 2;
        for (int i = 0; offset + 3 <= size && i < userData.ccCount; i++) {
            userData.ccData[i].markerBits = (data[offset] >> 3) & 0xFF;
            userData.ccData[i].ccValid = (((data[offset] >> 2) & 0xFF) & 0x01) != 0;
            userData.ccData[i].ccType = cea708_header.cc_type_t.values()[((data[offset] & 0xFF) & 0x03)];
            userData.ccData[i].ccData = ((data[offset + 1] & 0xFF) << 8) | (data[offset + 2] & 0xFF);
            offset += 3;
        }
        return userData;
    }

    public static caption_header.libcaption_stauts_t cea708_parse_h264(byte[] data, int size, cea708_header.cea708_t cea708, int index) {
        Debug.print("cea708_parse_h264 [START] <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
        printDataArray(data, data.length);
        // TODO bug here exceeds one byte in data. 00 at the end
        if (size < 3) {
            return caption_header.libcaption_stauts_t.LIBCAPTION_ERROR;
        }

        Debug.print("  country: " + (data[0] & 0xFF));
        Debug.print("  provider: " + ((data[1] << 8) | (data[2] & 0xFF)));
        cea708.country = cea708_header.itu_t_t35_country_code_t.fromValue(data[0] & 0xFF);
        cea708.provider = cea708_header.itu_t_t35_provider_code_t.fromValue((data[1] << 8) | (data[2] & 0xFF));
        cea708.user_identifier = 0;
        cea708.user_data_type_code = 0;
        //System.arraycopy(data, 3, data, 0, size - 3);
        int offset = 3;
        //size -= 3;
        Debug.print("  data[0]: " + (data[offset + 0] & 0xFF));
        Debug.print("  data[1]: " + (data[offset + 1] & 0xFF));
        Debug.print("  data[2]: " + (data[offset + 2] & 0xFF));

      //  Debug.print("  country: " + cea708.country);
      //  Debug.print("  provider: " + cea708.provider);

        if (cea708.provider == cea708_header.itu_t_t35_provider_code_t.t35_provider_atsc) {
            System.out.println("" + size + " - < 4");
            if (size - offset < 4) {
                return caption_header.libcaption_stauts_t.LIBCAPTION_ERROR;
            }

            cea708.user_identifier = ((data[offset] << 24) | (data[offset + 1] << 16) | (data[offset + 2] << 8) | data[offset + 3]);
            Debug.print("user identifier: " + cea708.user_identifier);
            offset += 4;
        }

        if (cea708.provider == cea708_header.itu_t_t35_provider_code_t.t35_provider_direct_tv || cea708.provider == cea708_header.itu_t_t35_provider_code_t.t35_provider_atsc) {
            if (size - offset < 1) {
                return caption_header.libcaption_stauts_t.LIBCAPTION_ERROR;
            }
            cea708.user_data_type_code = data[offset];
            Debug.print("user data type code: " + cea708.user_data_type_code);
            offset += 1;
        }

        if (cea708.provider == cea708_header.itu_t_t35_provider_code_t.t35_provider_direct_tv) {
            if (size - offset < 1) {
                return caption_header.libcaption_stauts_t.LIBCAPTION_ERROR;
            }
            cea708.directv_user_data_length = data[offset];
            Debug.print("user data length: " + cea708.directv_user_data_length);
            offset += 1;
        }

        if (cea708.user_data_type_code == 3 && size - offset >= 2) {
            data = Arrays.copyOfRange(data, offset, data.length);
            Debug.print("cea708_parse_user_data_type_strcture before");
            printDataArray(data, data.length);
            cea708_header.user_data_t userData = cea708_parse_user_data_type_strcture(data, data.length, cea708.user_data);
            cea708.user_data = userData;
            Debug.print("cea708_parse_user_data_type_strcture result");
            printDataArray(data, data.length);
        } else if (cea708.user_data_type_code == 4) {
            // handle additional CEA-608 data
        } else if (cea708.user_data_type_code == 5) {
            // handle luma PAM data
        } else {
            // handle ATSC reserved user data
        }
        return caption_header.libcaption_stauts_t.LIBCAPTION_OK;
    }

    public static caption_header.libcaption_stauts_t cea708_to_caption_frame(caption_c.caption_frame_t frame, cea708_header.cea708_t cea708) {
        int count = cea708.user_data.ccCount;
        caption_header.libcaption_stauts_t status = caption_header.libcaption_stauts_t.LIBCAPTION_OK;

        for (int i = 0; i < count; i++) {
            boolean valid = cea708.user_data.ccData[i].ccValid;
            cea708_header.cc_type_t type = cea708.user_data.ccData[i].ccType;
            int ccData = cea708.user_data.ccData[i].ccData;

            if (valid && type == cea708_header.cc_type_t.NTSC_CC_FIELD_1) {
                status = frame.caption_frame_decode(ccData, cea708.timestamp);
            }
        }

        return status;
    }
}

