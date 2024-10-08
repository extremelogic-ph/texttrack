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
 * The {@code ItuT35CountryCode} enum defines country codes as per the ITU-T T.35 specification.
 * These country codes are used in digital television systems, such as for metadata in MPEG streams
 * and closed captioning in digital video broadcasts.
 *
 * Each enum constant represents a country or region, associated with a unique integer code
 * as specified by ITU-T T.35.
 */
@Getter
public enum ItuT35CountryCode {
    COUNTRY_UNITED_STATES(181),      // 0xB5
    COUNTRY_CANADA(49),              // 0x31
    COUNTRY_JAPAN(33),               // 0x21
    COUNTRY_KOREA(45),               // 0x2D
    COUNTRY_CHINA(36),               // 0x24
    COUNTRY_UNITED_KINGDOM(165),     // 0xA5
    COUNTRY_FRANCE(70),              // 0x46
    COUNTRY_GERMANY(68),             // 0x44
    COUNTRY_AUSTRALIA(12),           // 0x0C
    COUNTRY_BRAZIL(83),              // 0x53
    COUNTRY_INDIA(356),              // 0x164
    COUNTRY_RUSSIA(112),             // 0x70
    COUNTRY_MEXICO(151),             // 0x97
    COUNTRY_SOUTH_AFRICA(83),        // 0x53
    COUNTRY_ITALY(86),               // 0x56
    COUNTRY_ARGENTINA(10),           // 0x0A
    COUNTRY_SPAIN(83),               // 0x53
    COUNTRY_NEW_ZEALAND(12),         // 0x0C
    COUNTRY_SINGAPORE(55),           // 0x37
    COUNTRY_THAILAND(58),            // 0x3A
    COUNTRY_VIETNAM(132),            // 0x84
    COUNTRY_HONG_KONG(158),          // 0x9E
    COUNTRY_TAIWAN(166),             // 0xA6
    COUNTRY_PHILIPPINES(52),         // 0x34
    COUNTRY_INDONESIA(48),           // 0x30
    COUNTRY_MALAYSIA(57),            // 0x39
    COUNTRY_NETHERLANDS(73),         // 0x49
    COUNTRY_BELGIUM(84),             // 0x54
    COUNTRY_SWITZERLAND(67),         // 0x43
    COUNTRY_AUSTRIA(79),             // 0x4F
    COUNTRY_SWEDEN(90),              // 0x5A
    COUNTRY_NORWAY(93),              // 0x5D
    COUNTRY_DENMARK(95),             // 0x5F
    COUNTRY_FINLAND(101),            // 0x65
    COUNTRY_PORTUGAL(163),           // 0xA3
    COUNTRY_GREECE(201),             // 0xC9
    COUNTRY_ISRAEL(39);              // 0x27

    private final int value;

    /**
     * Constructs an {@code ItuT35CountryCode} enum constant with the specified integer value.
     *
     * @param value the integer value representing the country code
     */
    ItuT35CountryCode(int value) {
        this.value = value;
    }

    /**
     * Retrieves the {@code ItuT35CountryCode} corresponding to the given value.
     *
     * @param value the integer value of the country code
     * @return the matching {@code ItuT35CountryCode}
     * @throws IllegalArgumentException if no matching value is found
     */
    public static ItuT35CountryCode fromValue(int value) {
        for (ItuT35CountryCode countryCode : ItuT35CountryCode.values()) {
            if (countryCode.getValue() == value) {
                return countryCode;
            }
        }
        throw new IllegalArgumentException("Invalid value: " + value);
    }
}
