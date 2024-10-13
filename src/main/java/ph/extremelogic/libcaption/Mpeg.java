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

import ph.extremelogic.libcaption.caption.CaptionFrame;
import ph.extremelogic.libcaption.cea708.Cea708;
import ph.extremelogic.libcaption.constant.LibCaptionStatus;
import ph.extremelogic.libcaption.constant.SeiMessageType;
import ph.extremelogic.libcaption.cea708.Cea708Data;
import ph.extremelogic.libcaption.model.MpegBitStream;
import ph.extremelogic.libcaption.model.Sei;
import ph.extremelogic.libcaption.model.SeiMessage;
import ph.extremelogic.texttrack.utils.ArrayUtil;
import ph.extremelogic.texttrack.utils.Debug;

import java.util.Arrays;

/**
 * The {@code Mpeg} class handles the parsing and processing of MPEG bitstreams,
 * specifically focusing on SEI messages and CEA-708 data for captioning.
 */
public class Mpeg {
    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private Mpeg() {
        // Prevent instantiation
    }

    // MPEG stream type constants
    public static final int STREAM_TYPE_H262 = 0x02;
    public static final int STREAM_TYPE_H264 = 0x1B;
    public static final int STREAM_TYPE_H265 = 0x24;

    // SEI packet type constants for different MPEG stream types
    public static final int H262_SEI_PACKET = 0xB2;
    public static final int H264_SEI_PACKET = 0x06;
    public static final int H265_SEI_PACKET = 0x27; // There is also 0x28

    // Constants for maximum sizes
    public static final int MAX_NALU_SIZE = 6 * 1024 * 1024; // 6 MB
    public static final int MAX_REFERENCE_FRAMES = 64;

    /**
     * Finds and returns the offset of the next emulation prevention byte in the provided byte array.
     *
     * @param data The byte array containing the data to scan.
     * @param size The size of the data to scan within the array.
     * @return The offset within the array where the emulation prevention byte is found, or the input size if none.
     */
    public static int findEmulationPreventionByte(byte[] data, int size) {
        int offset = 2;

        Debug.print("DEBUG " + size + " _find_emulation_prevention_byte input: ");
        Debug.printDataArray(data, size);

        while (offset < size) {
            int currentByte = data[offset] & 0xFF;
            int prevByte1 = data[offset - 1] & 0xFF;
            int prevByte2 = data[offset - 2] & 0xFF;

            if (currentByte == 0) {
                // 0 0 X 3 //; we know X is zero
                offset += 1;
                Debug.print(" offset 1 " + offset);
            } else if (currentByte != 3) {
                // 0 0 X 0 0 3; we know X is not 0 and not 3
                offset += 3;
                Debug.print(" offset 2 " + offset);
            } else if (prevByte1 != 0) {
                // 0 X 0 0 3
                offset += 2;
                Debug.print(" offset 3 " + offset);
            } else if (prevByte2 != 0) {
                // X 0 0 3
                offset += 1;
                Debug.print(" offset 4 " + offset);
            } else {
                // 0 0 3
                Debug.print(" offset 5 " + offset);
                return offset;
            }
            if (offset >= data.length) {
                break;
            }
        }
        Debug.print(" return " + size);
        return size;
    }

    /**
     * Copies data from source to destination while handling emulation prevention bytes.
     *
     * @param destData The destination array where data will be copied.
     * @param destOffset The starting position in the destination array.
     * @param destSize The maximum number of bytes to copy to the destination array.
     * @param srcData The source array from which to copy data.
     * @param srcOffset The starting position in the source array.
     * @param srcSize The number of bytes available to copy from the source array.
     * @return The total number of bytes copied to the destination array.
     */
    private static int copyToRbsp(byte[] destData, int destOffset, int destSize, byte[] srcData, int srcOffset, int srcSize) {
        Debug.print("copy_to_rbsp [START] <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
        Debug.print(" - destSize: " + destSize);
        Debug.print(" - sorcSize: " + srcSize);
        int toCopy;
        int totalSize = 0;
        int loop = 0;

        while (true) {
            if (destSize >= srcSize) {
                return 0;
            }

            toCopy = findEmulationPreventionByte(srcData, destSize);
            Debug.print("    DEBUG " + loop++ + " bytes to copy: " + toCopy);
            System.arraycopy(srcData, srcOffset - 2, destData, destOffset, toCopy);

            totalSize += toCopy;
            destOffset += toCopy;
            destSize -= toCopy;

            if (destSize == 0) {
                return totalSize;
            }

            // Skip the emulation prevention byte
            totalSize += 1;
            srcOffset += toCopy + 1;
            srcSize -= toCopy + 1;
        }
    }

    /**
     * Parses SEI messages from the provided data array and updates the given SEI object.
     *
     * @param sei The SEI object to update with parsed messages.
     * @param data The data array containing the SEI messages.
     * @param size The size of the data in the array.
     * @param timestamp The timestamp to assign to parsed SEI messages.
     * @return The status of the parsing process, either OK or ERROR.
     */
    public static LibCaptionStatus seiParse(Sei sei, byte[] data, int size, double timestamp) {
        Debug.print("DEBUG sei_parse");
        sei.init(timestamp);
        int dataOffset = 0;

        // SEI may contain more than one payload
        while (size > 1) {
            int payloadType = 0;
            int payloadSize = 0;
            Debug.printDataArray(data, size);

            // Read payloadType
            while (size > 0 && (data[dataOffset] & 0xFF) == 255) {
                payloadType += 255;
                dataOffset++;
                size--;
            }
            Debug.print("DEBUG A payload type: " + payloadSize + " size " + size);
            Debug.printDataArray(data, size);

            if (size == 0) {
                return LibCaptionStatus.ERROR;
            }

            payloadType += (data[dataOffset] & 0xFF);
            dataOffset++;
            data = ArrayUtil.shiftLeftAndShrink(data);
            size--;
            Debug.print("DEBUG B payload type: " + payloadSize + " size " + size);
            Debug.printDataArray(data, size);

            // Read payloadSize
            while (size > 0 && (data[dataOffset] & 0xFF) == 255) {
                payloadSize += 255;
                dataOffset++;
                data = ArrayUtil.shiftLeftAndShrink(data);
                size--;
            }
            Debug.print("DEBUG C payload type: " + payloadSize + " size " + size);
            Debug.printDataArray(data, size);

            if (size == 0) {
                return LibCaptionStatus.ERROR;
            }

            payloadSize += (data[dataOffset - 1] & 0xFF);
            dataOffset++;
            data = ArrayUtil.shiftLeftAndShrink(data);
            size--;
            Debug.print("DEBUG D payload type: " + payloadSize + " size " + size);
            Debug.printDataArray(data, size);

            Debug.print("payload size " + payloadSize);

            if (payloadSize > 0) {
                // Create new sei_message_t
                SeiMessage msg = new SeiMessage();
                msg.setNext(null);
                msg.setType(SeiMessageType.fromValue(payloadType));
                msg.setSize(payloadSize);
                Debug.print("payload type " + msg.getType().getValue());
                Debug.print("payload size " + msg.getSize());
                msg.setPayload(new byte[payloadSize]);

                // Copy data to payload using copy_to_rbsp
                Debug.printDataArray(data, size);
                int bytes = copyToRbsp(msg.getPayload(), 0, payloadSize, data, dataOffset, size);
                Debug.print("DEBUG bytes " + bytes);
                Debug.printDataArray(msg.getPayload(), payloadSize);
                Debug.print("copy_to_rbsp [END] <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");

                sei.getMessages().add(msg);

                if (bytes < payloadSize) {
                    return LibCaptionStatus.ERROR;
                }

                dataOffset += bytes;
                size -= bytes;
            }
        }

        // There should be one trailing byte, 0x80. But really, we can just ignore that fact.
        return LibCaptionStatus.OK;
    }

    /**
     * Parses MPEG bitstream data, handling SEI messages and updating caption frame data.
     *
     * @param packet The MPEG bitstream packet to process.
     * @param frame The caption frame to update with parsed data.
     * @param data The byte array containing MPEG data.
     * @param size The size of the data to process.
     * @param streamType The type of MPEG stream being processed (e.g., H264).
     * @param dts Decoding time stamp for synchronization.
     * @param cts Composition time stamp for display timing.
     * @return The number of bytes processed in the current batch.
     */
    public static int mpegBitStreamParse(MpegBitStream packet, CaptionFrame frame, byte[] data, int size, int streamType, double dts, double cts) {
        Debug.print("mpeg_bitstream_parse");
        Debug.print("MAX_NALU_SIZE: " + MAX_NALU_SIZE);
        Debug.print("packet size: " + packet.getSize());
        if (packet.getSize() >= MAX_NALU_SIZE) {
            packet.setStatus(LibCaptionStatus.ERROR);
            Debug.print("LIBCAPTION_ERROR");
            return 0;
        }

        // Adjust size to not exceed MAX_NALU_SIZE
        if (packet.getSize() + size > MAX_NALU_SIZE) {
            size = MAX_NALU_SIZE - packet.getSize();
            Debug.print("Consume up to MAX_NALU_SIZE");
        }

        packet.setStatus(LibCaptionStatus.OK);
        System.arraycopy(data, 0, packet.getNaluData(), packet.getSize(), size);
        packet.setSize(packet.getSize() + size);

        final int headerSize = 4;
        int index = 0;

        Debug.print("Before loop");
        while (packet.getStatus() == LibCaptionStatus.OK) {
            Debug.print("loop: " + index++);
            Debug.printDataArray(data, size);
            Debug.print("packet size: " + packet.getSize());
            int scpos = findStartCode(packet.getNaluData(), packet.getSize());
            if (scpos <= headerSize) {
                break;
            }

            if (packet.getSize() > headerSize && (packet.getNaluData()[3] & 0x1F) == H264_SEI_PACKET) {
                byte[] seiData = Arrays.copyOfRange(packet.getNaluData(), headerSize, scpos);
                Debug.print("H264_SEI_PACKET");
                Sei seiMsgHolder = new Sei(dts + cts);
                LibCaptionStatus newPacketStatus = seiParse(seiMsgHolder, seiData, scpos - headerSize, dts + cts);
                packet.setStatus(CaptionFrame.statusUpdate(packet.getStatus(), newPacketStatus));

                int count = 0;
                int count2 = 0;

                // Process each SEI message
                // for (SeiMessage msg : seiMsgHolder.getMessages())
                {
                    SeiMessage msg = seiMsgHolder.getMessages().get(0);
                    Debug.print("msg type: " + msg.getType().getValue());
                    if (msg != null && msg.getType() == SeiMessageType.SEI_TYPE_USER_DATA_REGISTERED_ITU_T_T_35) {
                        System.out.println("count=" + count++);

                        // Emplace back
                        packet.incrementLatent();
                        Cea708Data cea708Data = packet.getCEA708At(packet.getLatent() - 1);

                        cea708Data.init(dts + cts);

                        newPacketStatus = Cea708.parseH264(msg.getPayload(), msg.getSize(), cea708Data);
                        packet.setStatus(CaptionFrame.statusUpdate(packet.getStatus(), newPacketStatus));

                        mpegBitstreamCea708Sort(packet);

                        // Process latent CEA-708 data
                        while (packet.getLatent() > 0 && packet.getStatus() == LibCaptionStatus.OK) {
                            cea708Data = mpegBitstreamCea708At(packet, 0);
                            Debug.print(String.format("%.6f", cea708Data.getTimestamp()) + " >= " + String.format("%.6f", dts));
                            if (cea708Data.getTimestamp() >= dts) {
                                System.out.println("Exit timestamp >= dts");
                                break;
                            }
                            System.out.println("count2=" + count2++);

                            newPacketStatus = Cea708.toCaptionFrame(frame, cea708Data);
                            packet.setStatus(CaptionFrame.statusUpdate(LibCaptionStatus.OK, newPacketStatus));
                            packet.setFront((packet.getFront() + 1) % MAX_REFERENCE_FRAMES);
                            packet.decrementLatent();
                            if (packet.getLatent() == 0) {
                                System.out.println("Exit packet.latent == 0");
                            }
                            if (packet.getStatus() != LibCaptionStatus.OK) {
                                System.out.println("Exit status != LIBCAPTION_OK");
                            }
                        }
                    }
                }
                seiMsgHolder.free();
            }

            // Adjust packet data for the next iteration
            packet.setSize(packet.getSize() - scpos);
            System.arraycopy(packet.getNaluData(), scpos, packet.getNaluData(), 0, packet.getSize());
        }

        return size;
    }

    /**
     * Finds the start code in a byte array that signifies the beginning of a frame or field in video compression.
     *
     * @param data The byte array containing the data to search.
     * @param size The size of the data array to search through.
     * @return The position of the start code or 0 if not found.
     */
    private static int findStartCode(byte[] data, int size) {
        int startCode = 0xFFFFFFFF;
        for (int i = 1; i < size; i++) {
            startCode = (startCode << 8) | (data[i] & 0xFF);
            if ((startCode & 0xFFFFFF00) == 0x00000100) {
                Debug.print("find_start_code !0: " + i + " " + (startCode & 0xFFFFFFFFL));
                return i - 3;
            }
        }
        Debug.print("find_start_code 0 " + size + " " + (startCode & 0xFFFFFFFFL));
        return 0;
    }

    /**
     * Uses a simple bubble sort algorithm to sort CEA708 data in an MPEG bitstream based on timestamps.
     * This method optimizes the process by exiting early if no swaps are needed, indicating that the list is sorted.
     *
     * @param packet the MpegBitStream packet containing CEA708 data.
     */
    private static void mpegBitstreamCea708Sort(MpegBitStream packet) {
        boolean swapped;
        Cea708Data[] cea708Data = packet.getCea708Data();

        for (int i = 0; i < packet.getLatent() - 1; ++i) {
            swapped = false;
            for (int j = 1; j < packet.getLatent() - i; ++j) {
                int posA = (packet.getFront() + j - 1) % MAX_REFERENCE_FRAMES;
                int posB = (packet.getFront() + j) % MAX_REFERENCE_FRAMES;

                if (cea708Data[posA].getTimestamp() > cea708Data[posB].getTimestamp()) {
                    ArrayUtil.swap(cea708Data, posA, posB); // Using a utility method to swap elements
                    swapped = true;
                }
            }
            if (!swapped) break; // Early exit if no swaps occurred
        }

        packet.setCea708Data(cea708Data);
    }

    /**
     * Retrieves a Cea708Data object from an MPEG bitstream at a specified position, accounting for circular indexing.
     *
     * @param packet The MPEG bitstream containing the CEA708 data.
     * @param pos The position in the bitstream from which to retrieve the data.
     * @return The CEA708 data object at the specified position.
     */
    private static Cea708Data mpegBitstreamCea708At(MpegBitStream packet, int pos) {
        return packet.getCea708Data()[(packet.getFront() + pos) % MAX_REFERENCE_FRAMES];
    }
}
