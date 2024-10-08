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

import lombok.Data;
import ph.extremelogic.libcaption.constant.ItuT35CountryCode;
import ph.extremelogic.libcaption.constant.ItuTt35ProviderCode;
import ph.extremelogic.libcaption.model.UserData;

import static ph.extremelogic.libcaption.constant.ItuTt35ProviderCode.T_35_PROVIDER_ATSC;

/**
 * The {@code Cea708Data} class represents data related to CEA-708 closed captioning,
 * including the country code, provider code, user identifier, user data type,
 * and timestamp. It also manages DirectTV user data length and user data flags.
 *
 * This class provides constructors to create instances of CEA-708 data and includes
 * an initialization method to set default values for certain fields.
 */
@Data
public class Cea708Data {
    /** The country code for the CEA-708 data, usually {@code COUNTRY_UNITED_STATES}. */
    private ItuT35CountryCode country;

    /** The provider code, identifying the source of the CEA-708 data. */
    private ItuTt35ProviderCode provider;

    /** A unique user identifier for the CEA-708 data. */
    private int userIdentifier;

    /** The user data type code. */
    private byte userDataTypeCode;

    /** The length of the DirectTV user data. */
    private byte directvUserDataLength;

    /** A {@code UserData} object containing additional user-specific information. */
    private UserData userData;

    /** The timestamp associated with the CEA-708 data. */
    private double timestamp;

    /**
     * Constructs a new {@code Cea708Data} object by copying the fields from another
     * {@code Cea708Data} instance.
     *
     * @param t the {@code Cea708Data} object to copy
     */
    public Cea708Data(Cea708Data t) {
        this.country = t.country;
        this.provider = t.provider;
        this.userIdentifier = t.userIdentifier;
        this.userDataTypeCode = t.userDataTypeCode;
        this.directvUserDataLength = t.directvUserDataLength;
        this.userData = t.userData;
        this.timestamp = t.timestamp;
    }

    /**
     * Constructs a new {@code Cea708Data} object with default {@code UserData}.
     * Initializes {@code userData} with flags set to false for {@code processEmDataFlag}
     * and {@code additionalDataFlag}, and true for {@code processCcDataFlag}.
     */
    public Cea708Data() {
        this.userData = new UserData(false, true, false, 0, 0xFF);
    }

    /**
     * Constructs a new {@code Cea708Data} object with the specified parameters.
     *
     * @param country the country code of the CEA-708 data
     * @param provider the provider code of the CEA-708 data
     * @param userIdentifier the user identifier for the CEA-708 data
     * @param userDataTypeCode the user data type code
     * @param directvUserDataLength the length of DirectTV user data
     * @param userData a {@code UserData} object containing additional information
     * @param timestamp the timestamp for the CEA-708 data
     */
    public Cea708Data(ItuT35CountryCode country, ItuTt35ProviderCode provider, int userIdentifier, byte userDataTypeCode, byte directvUserDataLength, UserData userData, double timestamp) {
        this.country = country;
        this.provider = provider;
        this.userIdentifier = userIdentifier;
        this.userDataTypeCode = userDataTypeCode;
        this.directvUserDataLength = directvUserDataLength;
        this.userData = userData;
        this.timestamp = timestamp;
    }

    /**
     * Initializes the {@code Cea708Data} object with default values for CEA-708 standards.
     *
     * @param timestamp the timestamp to associate with the CEA-708 data
     * @return {@code true} to indicate successful initialization
     */
    public boolean init(double timestamp) {
        this.country = ItuT35CountryCode.COUNTRY_UNITED_STATES;
        this.provider = T_35_PROVIDER_ATSC;
        this.userIdentifier = 0x47413934; // GA94;
        this.userDataTypeCode = 3;
        this.directvUserDataLength = 0;
        this.userData.setProcessEmDataFlag(false);
        this.userData.setProcessCcDataFlag(true);
        this.userData.setAdditionalDataFlag(false);
        this.userData.setEmData(0xFF);
        this.userData.setCcCount(0);
        this.timestamp = timestamp;
        return true;
    }
}
