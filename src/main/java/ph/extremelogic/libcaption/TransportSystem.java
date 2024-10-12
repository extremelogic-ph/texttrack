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
package ph.extremelogic.libcaption;

import lombok.Getter;
import lombok.Setter;
import ph.extremelogic.libcaption.constant.LibCaptionStatus;
import ph.extremelogic.texttrack.utils.Debug;

import java.nio.ByteBuffer;

import static ph.extremelogic.libcaption.Mpeg.STREAM_TYPE_H265;

/**
 * The {@code TransportSystem} class provides functionality to parse MPEG transport stream packets
 * and extract data such as Program Map Table (PMT), elementary streams, and presentation timestamps (PTS/DTS).
 * It includes methods to parse packets, calculate timestamps, and extract stream data for further processing.
 */
public class TransportSystem {
    /** MPEG timebase frequency used for PTS/DTS calculations (90 kHz). */
    public static final double MPEG_TIMEBASE = 90000.0;

    /** The size of a standard MPEG transport stream packet in bytes. */
    public static final int TS_PACKET_SIZE = 188;

    /** Program Map Table PID. */
    private short pmtpId;

    /** Elementary stream PID for closed captions. */
    private short ccpId;

    /** The stream type (e.g., H.264, H.265). */
    private short streamType;

    /** The Presentation Timestamp (PTS) in MPEG timestamp format. */
    private long pts;

    /** The Decode Timestamp (DTS) in MPEG timestamp format. */
    private long dts;

    /** The size of the extracted data payload. */
    @Getter
    @Setter
    private int size;

    /** The extracted data payload from the transport stream packet. */
    @Getter
    @Setter
    private byte[] data;

    public TransportSystem() {
        init();
    }

    /**
     * Initializes the transport system by resetting all fields to their default values.
     */
    private void init() {
        pmtpId = 0;
        ccpId = 0;
        streamType = 0;
        pts = 0;
        dts = 0;
        size = 0;
        data = null;
    }

    /**
     * Parses the Presentation Timestamp (PTS) from the specified byte array at the given offset.
     *
     * @param data   the byte array containing the transport stream data
     * @param offset the offset in the byte array where the PTS is located
     * @return the parsed PTS as a long value
     */
    private long parsePts(byte[] data, int offset) {
        long p = 0;
        p |= ((long) (data[offset] & 0x0E)) << 29;
        p |= ((long) (data[offset + 1] & 0xFF)) << 22;
        p |= ((long) (data[offset + 2] & 0xFE)) << 14;
        p |= ((long) (data[offset + 3] & 0xFF)) << 7;
        p |= ((long) (data[offset + 4] & 0xFE)) >> 1;
        return p;
    }

    /**
     * Parses a transport stream packet and extracts relevant information such as PTS, DTS, and payload data.
     *
     * @param packetData the byte array containing the transport stream packet
     * @return the status of the parsing operation, represented by the ordinal value of {@code LibCaptionStatus}
     * @throws IllegalArgumentException if the packet size is not equal to {@link #TS_PACKET_SIZE}
     */
    public int parsePacket(ByteBuffer packetData) {
        if (packetData.limit() != TS_PACKET_SIZE) {
            throw new IllegalArgumentException("Packet size must be " + TS_PACKET_SIZE + " bytes");
        }

        int i = 0;
        boolean pusi = (packetData.get(i + 1) & 0x40) != 0; // Payload Unit Start Indicator
        Debug.print("DEBUG pusi: " + (pusi ? 1 : 0));
        short pid = (short) (((packetData.get(i + 1) & 0x1F) << 8) | (packetData.get(i + 2) & 0xFF));
        Debug.print("DEBUG pid: " + pid);
        boolean adaptionPresent = (packetData.get(i + 3) & 0x20) != 0;
        Debug.print("DEBUG adaption_present: " + (adaptionPresent ? 1 : 0));
        boolean payloadPresent = (packetData.get(i + 3) & 0x10) != 0;
        Debug.print("DEBUG payload_present: " + (payloadPresent ? 1 : 0));
        i += 4;

        this.data = null;
        this.size = 0;

        if (adaptionPresent) {
            int adaptionLength = packetData.get(i) & 0xFF;
            i += 1 + adaptionLength;
            Debug.print("DEBUG adaption_present: " + i);
        }

        if (pid == 0) {
            if (payloadPresent) {
                i += (packetData.get(i) & 0xFF) + 1;
            }
            this.pmtpId = (short) (((packetData.get(i + 10) & 0x1F) << 8) | (packetData.get(i + 11) & 0xFF));
        } else if (pid == this.pmtpId) {
            if (payloadPresent) {
                i += (packetData.get(i) & 0xFF) + 1;
            }

            int sectionLength = ((packetData.get(i + 1) & 0x0F) << 8) | (packetData.get(i + 2) & 0xFF);
            boolean current = (packetData.get(i + 5) & 0x01) != 0;
            int programInfoLength = ((packetData.get(i + 10) & 0x0F) << 8) | (packetData.get(i + 11) & 0xFF);
            int descriptorLoopLength = sectionLength - (9 + programInfoLength + 4);

            i += 12 + programInfoLength;

            if (current) {
                while (descriptorLoopLength >= 5) {
                    short streamType = (short) (packetData.get(i) & 0xFF);
                    short elementaryPid = (short) (((packetData.get(i + 1) & 0x1F) << 8) | (packetData.get(i + 2) & 0xFF));
                    int esinfoLength = ((packetData.get(i + 3) & 0x0F) << 8) | (packetData.get(i + 4) & 0xFF);

                    if (streamType == Mpeg.STREAM_TYPE_H262 || streamType == Mpeg.STREAM_TYPE_H264 || streamType == STREAM_TYPE_H265) {
                        this.ccpId = elementaryPid;
                        this.streamType = streamType;
                    }

                    i += 5 + esinfoLength;
                    descriptorLoopLength -= 5 + esinfoLength;
                }
            }
        } else if (payloadPresent && pid == this.ccpId) {
            if (pusi) {
                boolean hasPts = (packetData.get(i + 7) & 0x80) != 0;
                boolean hasDts = (packetData.get(i + 7) & 0x40) != 0;
                int headerLength = packetData.get(i + 8) & 0xFF;

                if (hasPts) {
                    this.pts = parsePts(packetData.array(), i + 9);
                    this.dts = hasDts ? parsePts(packetData.array(), i + 14) : this.pts;
                }

                i += 9 + headerLength;
            }

            this.data = new byte[TS_PACKET_SIZE - i];
            System.arraycopy(packetData.array(), i, this.data, 0, this.data.length);
            this.size = this.data.length;
            Debug.print("DEBUG LIBCAPTION_READY");
            return LibCaptionStatus.READY.ordinal();
        }

        Debug.print("DEBUG LIBCAPTION_OK");
        return LibCaptionStatus.OK.ordinal();
    }

    /**
     * Returns the Decode Timestamp (DTS) in seconds.
     *
     * @return the DTS in seconds
     */
    public double dtsSeconds() {
        return this.dts / MPEG_TIMEBASE;
    }

    /**
     * Returns the Presentation Timestamp (PTS) in seconds.
     *
     * @return the PTS in seconds
     */
    public double ptsSeconds() {
        return this.pts / MPEG_TIMEBASE;
    }

    /**
     * Returns the Composition Time Stamp (CTS) in seconds, calculated as the difference between PTS and DTS.
     *
     * @return the CTS in seconds
     */
    public double ctsSeconds() {
        return ((double) this.pts - this.dts) / MPEG_TIMEBASE;
    }
}
