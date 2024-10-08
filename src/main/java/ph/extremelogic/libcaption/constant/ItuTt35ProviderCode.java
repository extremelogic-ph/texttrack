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
package ph.extremelogic.libcaption.constant;

import lombok.Getter;

/**
 * The {@code ItuTt35ProviderCode} enum defines provider codes as per the ITU-T T.35 specification.
 * These provider codes identify organizations or services that use ITU-T T.35 standards in digital
 * video broadcasts, such as DirectTV, ATSC, and others.
 *
 * Each enum constant represents a provider and is associated with a unique integer value defined
 * by the ITU-T T.35 standard.
 */

@Getter
public enum ItuTt35ProviderCode {
    T_35_PROVIDER_DIRECT_TV(47),            // 0x2F
    T_35_PROVIDER_ATSC(49),                 // 0x31
    T_35_PROVIDER_CABLELABS(52),            // 0x34
    T_35_PROVIDER_MICROSOFT(103),           // 0x67
    T_35_PROVIDER_NBC_UNIVERSAL(105),       // 0x69
    T_35_PROVIDER_TIME_WARNER(131),         // 0x83
    T_35_PROVIDER_APPLE(117),               // 0x75
    T_35_PROVIDER_SONY(95),                 // 0x5F
    T_35_PROVIDER_SAMSUNG(45),              // 0x2D
    T_35_PROVIDER_GOOGLE(113),              // 0x71
    T_35_PROVIDER_INTEL(81);                // 0x51


    private final int value;

    /**
     * Constructs an {@code ItuTt35ProviderCode} enum constant with the specified integer value.
     *
     * @param value the integer value representing the provider code
     */
    ItuTt35ProviderCode(int value) {
        this.value = value;
    }

    /**
     * Retrieves the {@code ItuTt35ProviderCode} constant corresponding to the given integer value.
     *
     * @param value the integer value of the provider code
     * @return the matching {@code ItuTt35ProviderCode} constant
     * @throws IllegalArgumentException if no matching provider code is found
     */
    public static ItuTt35ProviderCode fromValue(int value) {
        for (ItuTt35ProviderCode providerCode : ItuTt35ProviderCode.values()) {
            if (providerCode.getValue() == value) {
                return providerCode;
            }
        }
        throw new IllegalArgumentException("Invalid value: " + value);
    }
}
