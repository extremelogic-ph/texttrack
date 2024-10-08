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

import ph.extremelogic.libcaption.constant.LibCaptionStatus;
import ph.extremelogic.texttrack.utils.Debug;

import static ph.extremelogic.libcaption.Mpeg.STREAM_TYPE_H265;

public class TransportSystem {
    public static final int TS_PACKET_SIZE = 188;
    private short pmtpId;
    private short ccpId;
    private short streamType;
    private long pts;
    private long dts;
    private int size;
    private byte[] data;

    public TransportSystem() {
        init();
    }

    private void init() {
        pmtpId = 0;
        ccpId = 0;
        streamType = 0;
        pts = 0;
        dts = 0;
        size = 0;
        data = null;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    private long parsePts(byte[] data, int offset) {
        long p = 0;
        p |= ((long) (data[offset] & 0x0E)) << 29;
        p |= ((long) (data[offset + 1] & 0xFF)) << 22;
        p |= ((long) (data[offset + 2] & 0xFE)) << 14;
        p |= ((long) (data[offset + 3] & 0xFF)) << 7;
        p |= ((long) (data[offset + 4] & 0xFE)) >> 1;
        return p;
    }


    public int parsePacket(byte[] packetData) {
        if (packetData.length != TS_PACKET_SIZE) {
            throw new IllegalArgumentException("Packet size must be " + TS_PACKET_SIZE + " bytes");
        }

        int i = 0;
        boolean pusi = (packetData[i + 1] & 0x40) != 0; // Payload Unit Start Indicator
        Debug.print("DEBUG pusi: " + (pusi ? 1 : 0));
        short pid = (short) (((packetData[i + 1] & 0x1F) << 8) | (packetData[i + 2] & 0xFF));
        Debug.print("DEBUG pid: " + pid);
        boolean adaptionPresent = (packetData[i + 3] & 0x20) != 0;
        Debug.print("DEBUG adaption_present: " + (adaptionPresent ? 1 : 0));
        boolean payloadPresent = (packetData[i + 3] & 0x10) != 0;
        Debug.print("DEBUG payload_present: " + (payloadPresent ? 1 : 0));
        i += 4;

        this.data = null;
        this.size = 0;

        if (adaptionPresent) {
            int adaptionLength = packetData[i] & 0xFF;
            i += 1 + adaptionLength;
            Debug.print("DEBUG adaption_present: " + i);
        }

        if (pid == 0) {
            if (payloadPresent) {
                i += (packetData[i] & 0xFF) + 1;
            }
            this.pmtpId = (short) (((packetData[i + 10] & 0x1F) << 8) | (packetData[i + 11] & 0xFF));
        } else if (pid == this.pmtpId) {
            if (payloadPresent) {
                i += (packetData[i] & 0xFF) + 1;
            }

            int sectionLength = ((packetData[i + 1] & 0x0F) << 8) | (packetData[i + 2] & 0xFF);
            boolean current = (packetData[i + 5] & 0x01) != 0;
            int programInfoLength = ((packetData[i + 10] & 0x0F) << 8) | (packetData[i + 11] & 0xFF);
            int descriptorLoopLength = sectionLength - (9 + programInfoLength + 4);

            i += 12 + programInfoLength;

            if (current) {
                while (descriptorLoopLength >= 5) {
                    short streamType = (short) (packetData[i] & 0xFF);
                    short elementaryPid = (short) (((packetData[i + 1] & 0x1F) << 8) | (packetData[i + 2] & 0xFF));
                    int esinfoLength = ((packetData[i + 3] & 0x0F) << 8) | (packetData[i + 4] & 0xFF);

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
                boolean hasPts = (packetData[i + 7] & 0x80) != 0;
                boolean hasDts = (packetData[i + 7] & 0x40) != 0;
                int headerLength = packetData[i + 8] & 0xFF;

                if (hasPts) {
                    this.pts = parsePts(packetData, i + 9);
                    this.dts = hasDts ? parsePts(packetData, i + 14) : this.pts;
                }

                i += 9 + headerLength;
            }

            this.data = new byte[TS_PACKET_SIZE - i];
            System.arraycopy(packetData, i, this.data, 0, this.data.length);
            this.size = this.data.length;
            Debug.print("DEBUG LIBCAPTION_READY");
            return LibCaptionStatus.READY.ordinal();
        }

        Debug.print("DEBUG LIBCAPTION_OK");
        return LibCaptionStatus.OK.ordinal();
    }

    public double dtsSeconds() {
        return this.dts / 90000.0;
    }

    public double ptsSeconds() {
        return this.pts / 90000.0;
    }

    public double ctsSeconds() {
        return ((double) this.pts - this.dts) / 90000.0;
    }
}
