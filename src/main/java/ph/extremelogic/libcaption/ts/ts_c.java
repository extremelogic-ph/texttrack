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
package ph.extremelogic.libcaption.ts;

import ph.extremelogic.libcaption.caption.caption_header;
import ph.extremelogic.libcaption.mpeg.mpeg_c;
import ph.extremelogic.libcaption.mpeg.mpeg_header;
import ph.extremelogic.texttrack.utils.Debug;

public class ts_c {
    public ts_t ts = new ts_t();

    public ts_c() {
        ts_init();
    }

    private static long ts_parse_pts(byte[] data, int offset) {
        long pts = 0;
        pts |= ((long) (data[offset] & 0x0E)) << 29;
        pts |= ((long) (data[offset + 1] & 0xFF)) << 22;
        pts |= ((long) (data[offset + 2] & 0xFE)) << 14;
        pts |= ((long) (data[offset + 3] & 0xFF)) << 7;
        pts |= ((long) (data[offset + 4] & 0xFE)) >> 1;
        return pts & 0xFFFFFFFFFFFFFFFFL;
    }

    public void ts_init() {
        ts.pmtpid = 0;
        ts.ccpid = 0;
        ts.stream_type = 0;
        ts.pts = 0;
        ts.dts = 0;
        ts.size = 0;
        ts.data = null;
    }

    public ts_t getTs() {
        return ts;
    }

    public int ts_parse_packet(byte[] packetData) {
        if (packetData.length != ts_header.TS_PACKET_SIZE) {
            throw new IllegalArgumentException("Packet size must be " + ts_header.TS_PACKET_SIZE + " bytes");
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

        ts.data = null;
        ts.size = 0;

        if (adaptionPresent) {
            int adaptionLength = packetData[i] & 0xFF;
            i += 1 + adaptionLength;
            Debug.print("DEBUG adaption_present: " + i);
        }

        if (pid == 0) {
            if (payloadPresent) {
                i += (packetData[i] & 0xFF) + 1;
            }
            ts.pmtpid = (short) (((packetData[i + 10] & 0x1F) << 8) | (packetData[i + 11] & 0xFF));
        } else if (pid == ts.pmtpid) {
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

                    if (streamType == mpeg_c.STREAM_TYPE_H262 || streamType == mpeg_c.STREAM_TYPE_H264 || streamType == mpeg_header.STREAM_TYPE_H265) {
                        ts.ccpid = elementaryPid;
                        ts.stream_type = streamType;
                    }

                    i += 5 + esinfoLength;
                    descriptorLoopLength -= 5 + esinfoLength;
                }
            }
        } else if (payloadPresent && pid == ts.ccpid) {
            if (pusi) {
                boolean hasPts = (packetData[i + 7] & 0x80) != 0;
                boolean hasDts = (packetData[i + 7] & 0x40) != 0;
                int headerLength = packetData[i + 8] & 0xFF;

                if (hasPts) {
                    ts.pts = ts_parse_pts(packetData, i + 9);
                    ts.dts = hasDts ? ts_parse_pts(packetData, i + 14) : ts.pts;
                }

                i += 9 + headerLength;
            }

            ts.data = new byte[ts_header.TS_PACKET_SIZE - i];
            System.arraycopy(packetData, i, ts.data, 0, ts.data.length);
            ts.size = ts.data.length;
            Debug.print("DEBUG LIBCAPTION_READY");
            return caption_header.libcaption_stauts_t.LIBCAPTION_READY.ordinal();
        }

        Debug.print("DEBUG LIBCAPTION_OK");
        return caption_header.libcaption_stauts_t.LIBCAPTION_OK.ordinal();
    }
}
