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
package ph.extremelogic.libcaption.model;

import lombok.Data;
import ph.extremelogic.libcaption.constant.CcType;

/**
 * The {@code UserData} class represents user data that includes processing flags, caption count,
 * and embedded data along with an array of closed caption data. It stores various flags and data
 * fields relevant to EIA-608 or similar closed captioning formats.
 */
@Data
public class UserData {
    /**
     * Flag indicating whether to process embedded data (1 bit).
     */
    private boolean processEmDataFlag; // 1 bit

    /**
     * Flag indicating whether to process closed caption data (1 bit).
     */
    private boolean processCcDataFlag; // 1 bit

    /**
     * Flag for additional data presence (1 bit).
     */
    private boolean additionalDataFlag; // 1 bit

    /**
     * Number of closed caption data units (5 bits).
     */
    private int ccCount; // 5 bits

    /**
     * Embedded data (8 bits), typically used to store auxiliary information.
     */
    private int emData; // 8 bits

    /**
     * Array of {@code CcData} objects representing the closed caption data (up to 32 units).
     */
    private CcData[] ccData = new CcData[32]; // Array of caption data

    /**
     * Constructs a {@code UserData} object with the specified parameters,
     * initializing the {@code ccData} array with default {@code CcData} instances.
     *
     * @param processEmDataFlag flag indicating whether to process embedded data
     * @param processCcDataFlag flag indicating whether to process closed caption data
     * @param additionalDataFlag flag indicating whether additional data is present
     * @param ccCount number of closed caption data units
     * @param emData embedded data field
     */
    public UserData(boolean processEmDataFlag, boolean processCcDataFlag, boolean additionalDataFlag, int ccCount, int emData) {
        this.processEmDataFlag = processEmDataFlag;
        this.processCcDataFlag = processCcDataFlag;
        this.additionalDataFlag = additionalDataFlag;
        this.ccCount = ccCount;
        this.emData = emData;
        // Initialize the ccData array
        for (int i = 0; i < 32; i++) {
            ccData[i] = new CcData(0, false, CcType.NTSC_CC_FIELD_1, 0);
        }
    }
}
