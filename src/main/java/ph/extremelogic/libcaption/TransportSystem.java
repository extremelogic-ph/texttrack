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

    private static final int PTS_OFFSET = 9;
    private static final int HEADER_LENGTH_OFFSET = 8;
    private static final int ADAPTION_FIELD_PRESENT_MASK = 0x20;
    private static final int PAYLOAD_PRESENT_MASK = 0x10;

    /** Program Map Table PID. */
    @Getter @Setter
    private short pmtpId;

    /** Elementary stream PID for closed captions. */
    private short ccpId;

    /** The stream type (e.g., H.264, H.265). */
    private short streamType;

    /** The Presentation Timestamp (PTS) in MPEG timestamp format. */
    @Getter @Setter
    private long pts;

    /** The Decode Timestamp (DTS) in MPEG timestamp format. */
    @Getter @Setter
    private long dts;

    /** The size of the extracted data payload. */
    @Getter @Setter
    private int size;

    /** The extracted data payload from the transport stream packet. */
    @Getter @Setter
    private byte[] data;

    public TransportSystem() {
        init();
    }

    /**
     * Initializes the transport system by resetting all fields to their default values.
     */
    private void init() {
        pmtpId = ccpId = streamType = 0;
        pts = dts = 0;
        size = 0;
        data = null;
    }

    /**
     * Parses the Presentation Timestamp (PTS) from the specified byte array at the given offset.
     *
     * @param buffer   the byte buffer containing the transport stream data
     * @param offset the offset in the byte array where the PTS is located
     * @return the parsed PTS as a long value
     */
    private long parsePts(ByteBuffer buffer, int offset) {
        return ((long) (buffer.get(offset) & 0x0E) << 29) |
                ((long) (buffer.get(offset + 1) & 0xFF) << 22) |
                ((long) (buffer.get(offset + 2) & 0xFE) << 14) |
                ((long) (buffer.get(offset + 3) & 0xFF) << 7) |
                ((long) (buffer.get(offset + 4) & 0xFE) >> 1);
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

        boolean pusi = (packetData.get(1) & 0x40) != 0;
        short pid = (short) (((packetData.get(1) & 0x1F) << 8) | (packetData.get(2) & 0xFF));
        boolean adaptionPresent = (packetData.get(3) & ADAPTION_FIELD_PRESENT_MASK) != 0;
        boolean payloadPresent = (packetData.get(3) & PAYLOAD_PRESENT_MASK) != 0;

        Debug.print("DEBUG pusi: " + (pusi ? 1 : 0));
        Debug.print("DEBUG pid: " + pid);
        Debug.print("DEBUG adaption_present: " + (adaptionPresent ? 1 : 0));
        Debug.print("DEBUG payload_present: " + (payloadPresent ? 1 : 0));

        packetData.position(4);

        this.data = null;
        this.size = 0;

        if (adaptionPresent) {
            int adaptionLength = packetData.get() & 0xFF;
            packetData.position(packetData.position() + adaptionLength);
            Debug.print("DEBUG adaption_present: " + packetData.position());
        }

        if (pid == 0) {
            return handleProgramAssociationTable(packetData, payloadPresent);
        } else if (pid == this.pmtpId) {
            return handleProgramMapTable(packetData, payloadPresent);
        } else if (payloadPresent && pid == this.ccpId) {
            return handleClosedCaptionPayload(packetData, pusi);
        }

        Debug.print("DEBUG LIBCAPTION_OK");
        return LibCaptionStatus.OK.ordinal();
    }

    /**
     * Handles the Program Association Table (PAT) to set the Program Map Table (PMT) PID.
     *
     * @param packetData the byte buffer containing the transport stream packet
     * @param payloadPresent flag indicating if the payload is present in the packet
     * @return the status of the handling operation, represented by the ordinal value of {@code LibCaptionStatus}
     */
    private int handleProgramAssociationTable(ByteBuffer packetData, boolean payloadPresent) {
        if (payloadPresent) {
            packetData.position(packetData.position() + (packetData.get() & 0xFF) + 1);
        }
        this.pmtpId = (short) (((packetData.get(packetData.position() + 10) & 0x1F) << 8) | (packetData.get(packetData.position() + 11) & 0xFF));
        return LibCaptionStatus.OK.ordinal();
    }

    /**
     * Handles the Program Map Table (PMT) to identify and set the closed caption PID.
     *
     * @param packetData the byte buffer containing the transport stream packet
     * @param payloadPresent flag indicating if the payload is present in the packet
     * @return the status of the handling operation, represented by the ordinal value of {@code LibCaptionStatus}
     */
    private int handleProgramMapTable(ByteBuffer packetData, boolean payloadPresent) {
        if (payloadPresent) {
            packetData.position(packetData.position() + (packetData.get() & 0xFF) + 1);
        }

        int sectionLength = ((packetData.get(packetData.position() + 1) & 0x0F) << 8) | (packetData.get(packetData.position() + 2) & 0xFF);
        boolean current = (packetData.get(packetData.position() + 5) & 0x01) != 0;
        int programInfoLength = ((packetData.get(packetData.position() + 10) & 0x0F) << 8) | (packetData.get(packetData.position() + 11) & 0xFF);
        int descriptorLoopLength = sectionLength - (9 + programInfoLength + 4);

        packetData.position(packetData.position() + 12 + programInfoLength);

        if (current) {
            while (descriptorLoopLength >= 5) {
                short streamType = (short) (packetData.get() & 0xFF);
                short elementaryPid = (short) (((packetData.get() & 0x1F) << 8) | (packetData.get() & 0xFF));
                int esinfoLength = ((packetData.get() & 0x0F) << 8) | (packetData.get() & 0xFF);

                if (streamType == ph.extremelogic.libcaption.Mpeg.STREAM_TYPE_H262 ||
                        streamType == ph.extremelogic.libcaption.Mpeg.STREAM_TYPE_H264 ||
                        streamType == STREAM_TYPE_H265) {
                    this.ccpId = elementaryPid;
                    this.streamType = streamType;
                }

                packetData.position(packetData.position() + 1 + esinfoLength);
                descriptorLoopLength -= 5 + esinfoLength;
            }
        }
        return LibCaptionStatus.OK.ordinal();
    }

    /**
     * Handles the closed caption payload for packets identified with closed caption PID.
     *
     * @param packetData the byte buffer containing the transport stream packet
     * @param pusi flag indicating if the payload unit start indicator (PUSI) is present
     * @return the status of the payload handling operation, represented by the ordinal value of {@code LibCaptionStatus}
     */
    private int handleClosedCaptionPayload(ByteBuffer packetData, boolean pusi) {
        if (pusi) {
            boolean hasPts = (packetData.get(packetData.position() + 7) & 0x80) != 0;
            boolean hasDts = (packetData.get(packetData.position() + 7) & 0x40) != 0;
            int headerLength = packetData.get(packetData.position() + 8) & 0xFF;

            if (hasPts) {
                this.pts = parsePts(packetData, packetData.position() + 9);
                this.dts = hasDts ? parsePts(packetData, packetData.position() + 14) : this.pts;
            }

            packetData.position(packetData.position() + 9 + headerLength);
        }

        this.size = packetData.remaining();
        this.data = new byte[this.size];
        packetData.get(this.data);
        Debug.print("DEBUG LIBCAPTION_READY");
        return LibCaptionStatus.READY.ordinal();
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
        return (this.pts - this.dts) / MPEG_TIMEBASE;
    }
}
