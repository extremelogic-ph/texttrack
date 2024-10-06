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
package ph.extremelogic.libcaption.mpeg;

import ph.extremelogic.libcaption.caption.caption_c;
import ph.extremelogic.libcaption.caption.caption_header;
import ph.extremelogic.libcaption.cea708.cea708_c;
import ph.extremelogic.libcaption.cea708.cea708_header;
import ph.extremelogic.texttrack.utils.ArrayUtil;
import ph.extremelogic.texttrack.utils.Debug;

import java.util.Arrays;

public class mpeg_c {
    // Constants
    public static final int STREAM_TYPE_H262 = 0x02;
    public static final int STREAM_TYPE_H264 = 0x1B;
    public static final int STREAM_TYPE_H265 = 0x24;
    public static final int H262_SEI_PACKET = 0xB2;
    public static final int H264_SEI_PACKET = 0x06;
    public static final int H265_SEI_PACKET = 0x27; // There is also 0x28
    public static final int MAX_NALU_SIZE = 6 * 1024 * 1024; // 6 MB
    public static final int MAX_REFERENCE_FRAMES = 64;

    // Utility methods
    public static int find_emulation_prevention_byte(byte[] data, int size) {
        int offset = 2;

        Debug.print("DEBUG " + size + " _find_emulation_prevention_byte input: ");

        if (size > data.length) {
            // size = data.length;
        }
        printDataArray(data, size);

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

    private static int copy_to_rbsp(byte[] destData, int destOffset, int destSize, byte[] srcData, int srcOffset, int srcSize) {
        Debug.print("copy_to_rbsp [START] <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
     //   Debug.print(" - destOffset: " + destOffset);
        Debug.print(" - destSize: " + destSize);
       // Debug.print(" - srcOffset: " + srcOffset);
        Debug.print(" - sorcSize: " + srcSize);
//        printDataArray(destData, destData.length);
//        printDataArray(srcData, srcData.length);
        int toCopy, totalSize = 0;
        int loop = 0;

        while (true) {
            if (destSize >= srcSize) {
                return 0;
            }

            toCopy = find_emulation_prevention_byte(srcData, destSize);
            Debug.print("    DEBUG " + loop++ + " bytes to copy: " + toCopy);
            //if (true) {
            //    return 0;
            //}
            System.arraycopy(srcData, srcOffset - 2, destData, destOffset, toCopy);
//            printDataArray(destData, destSize);
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
        ///////////////////
    }

    public static void printDataArray(byte[] data, int size) {
        if (size > 200) return;
        Debug.print("Data array: [", true);
        for (int i = 0; i < size; i++) {
            Debug.print(String.format("%02X ", data[i] & 0xFF), true);
        }
        Debug.print("] SIZE: " + size, false);
    }

    public static caption_header.libcaption_stauts_t sei_parse(mpeg_header.sei_t sei, byte[] data, int size, double timestamp, int index) {
        Debug.print("DEBUG sei_parse");
        sei.init(timestamp);
        int ret = 0;
        int dataOffset = 0;

        // SEI may contain more than one payload
        while (size > 1) {
            int payloadType = 0;
            int payloadSize = 0;
            printDataArray(data, size);

            // Read payloadType
            while (size > 0 && (data[dataOffset] & 0xFF) == 255) {
                payloadType += 255;
                dataOffset++;
                size--;
            }
            Debug.print("DEBUG A payload type: " + payloadSize + " size " + size);
            printDataArray(data, size);

            if (size == 0) {
                return caption_header.libcaption_stauts_t.LIBCAPTION_ERROR;
            }

            payloadType += (data[dataOffset] & 0xFF);
            dataOffset++;
            data = ArrayUtil.shiftLeftAndShrink(data);
            size--;
            Debug.print("DEBUG B payload type: " + payloadSize + " size " + size);
            printDataArray(data, size);

            // Read payloadSize
            while (size > 0 && (data[dataOffset] & 0xFF) == 255) {
                payloadSize += 255;
                dataOffset++;
                data = ArrayUtil.shiftLeftAndShrink(data);
                size--;
            }
            Debug.print("DEBUG C payload type: " + payloadSize + " size " + size);
            printDataArray(data, size);

            if (size == 0) {
                return caption_header.libcaption_stauts_t.LIBCAPTION_ERROR;
            }

            payloadSize += (data[dataOffset - 1] & 0xFF);
            dataOffset++;
            data = ArrayUtil.shiftLeftAndShrink(data);
            size--;
            Debug.print("DEBUG D payload type: " + payloadSize + " size " + size);
            printDataArray(data, size);

            Debug.print("payload size " + payloadSize);

            if (payloadSize > 0) {
                // Create new sei_message_t
                mpeg_header.sei_message_t msg = new mpeg_header.sei_message_t();
                msg.next = null;
                msg.type = mpeg_header.sei_msgtype_t.fromValue(payloadType);
                msg.size = payloadSize;
                Debug.print("payload type " + msg.type.getValue());
                Debug.print("payload size " + msg.size);
                msg.payload = new byte[payloadSize + 0];

                // Copy data to payload using copy_to_rbsp
                printDataArray(data, size);
                int bytes = copy_to_rbsp(msg.payload, 0, payloadSize, data, dataOffset, size);
                Debug.print("DEBUG bytes " + bytes);
                printDataArray(msg.payload, payloadSize);
                Debug.print("copy_to_rbsp [END] <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");

                sei.messages.add(msg);

                if (bytes < payloadSize) {
                    return caption_header.libcaption_stauts_t.LIBCAPTION_ERROR;
                }

                dataOffset += bytes;
                size -= bytes;
                ret++;
            }
        }

        // There should be one trailing byte, 0x80. But really, we can just ignore that fact.
        return caption_header.libcaption_stauts_t.LIBCAPTION_OK;
    }

    public static int mpeg_bitstream_parse(mpeg_header.mpeg_bitstream_t packet, caption_c.caption_frame_t frame, byte[] data, int size, int streamType, double dts, double cts, int debugindex) {
        Debug.print("mpeg_bitstream_parse");
        Debug.print("MAX_NALU_SIZE: " + MAX_NALU_SIZE);
        Debug.print("packet size: " + packet.size);
        if (MAX_NALU_SIZE <= packet.size) {
            packet.status = caption_header.libcaption_stauts_t.LIBCAPTION_ERROR;
            Debug.print("LIBCAPTION_ERROR");
            return 0;
        }

        // Consume up to MAX_NALU_SIZE bytes
        if (MAX_NALU_SIZE <= packet.size + size) {
            size = MAX_NALU_SIZE - packet.size;
            Debug.print("Consume up to MAX_NALU_SIZE");
        }

        mpeg_header.sei_t seiMsgHolder = new mpeg_header.sei_t(dts + cts);
        caption_header.libcaption_stauts_t newPacketStatus;

        int headerSize, scpos;
        packet.status = caption_header.libcaption_stauts_t.LIBCAPTION_OK;
        System.arraycopy(data, 0, packet.data, packet.size, size);
        packet.size += size;

        headerSize = 4;
        int index = 0;

        Debug.print("Before loop");
        while (packet.status == caption_header.libcaption_stauts_t.LIBCAPTION_OK) {
            Debug.print("loop: " + index++);
            printDataArray(data, size);
            Debug.print("packet size: " + packet.size);
            scpos = find_start_code(packet.data, packet.size);
            if (scpos <= headerSize) {
                break;
            }

            if ((packet.size > 4) && ((packet.data[3] & 0x1F) == H264_SEI_PACKET)) {
                byte[] seiData = Arrays.copyOfRange(packet.data, headerSize, scpos);
                Debug.print("H264_SEI_PACKET");
                newPacketStatus = sei_parse(seiMsgHolder, seiData, scpos - headerSize, dts + cts, index);
                packet.status = caption_header.libcaption_status_update(packet.status, newPacketStatus);

                int count = 0;
                int count2 = 0;

                //for (mpeg_header.sei_message_t msg : seiMsgHolder.messages)
                {
                    mpeg_header.sei_message_t msg = seiMsgHolder.messages.get(0);
                    Debug.print("msg type: " + msg.type.getValue());
                    if (msg != null && msg.type == mpeg_header.sei_msgtype_t.sei_type_user_data_registered_itu_t_t35) {
                        System.out.println("count=" + count++);

                        // Emplace back
                        packet.latent++;
                        cea708_header.cea708_t cea708 = packet.getCEA708At(packet.latent - 1);

                        cea708.init(dts + cts);

                        newPacketStatus = cea708_c.cea708_parse_h264(msg.payload, msg.size, cea708, index);
                        packet.status = caption_header.libcaption_status_update(packet.status, newPacketStatus);

                        mpeg_bitstream_cea708_sort(packet);
//                        packet.sortCEA708();

                        // Loop will terminate on LIBCAPTION_READY
                        while (true) {
                            if (packet.latent == 0) {
                                System.out.println("Exit packet.latent == 0");
                                break;
                            }
                            if (packet.status != caption_header.libcaption_stauts_t.LIBCAPTION_OK) {
                                System.out.println("Exit status != LIBCAPTION_OK");
                                break;
                            }
                            cea708 = mpeg_bitstream_cea708_at(packet, 0);
                            Debug.print(String.format("%.6f", cea708.timestamp) + " >= " + String.format("%.6f", dts));
                            if (cea708.timestamp >= dts) {
                                System.out.println("Exit timestamp >= dts");
                                break;
                            }
                            System.out.println("count2=" + count2++);

                            newPacketStatus = cea708_c.cea708_to_caption_frame(frame, cea708);
                            packet.status = caption_header.libcaption_status_update(caption_header.libcaption_stauts_t.LIBCAPTION_OK, newPacketStatus);
                            packet.front = (packet.front + 1) % MAX_REFERENCE_FRAMES;
                            packet.latent--;
                        }
                    }
                }
                seiMsgHolder.free();
            }

            packet.size -= scpos;
            System.arraycopy(packet.data, scpos, packet.data, 0, packet.size);
        }

        return size;
    }

    private static int find_start_code(byte[] data, int size) {
        int startCode = 0xFFFFFFFF;
        for (int i = 1; i < size; i++) {
            startCode = (startCode << 8) | (data[i] & 0xFF);
            if ((startCode & 0xFFFFFF00) == 0x00000100) {
                Debug.print("find_start_code !0: " + i  + " " + (startCode & 0xFFFFFFFFL));
                return i - 3;
            }
        }
        Debug.print("find_start_code 0 " + size + " " + (startCode & 0xFFFFFFFFL));
        return 0;
    }

    private static void mpeg_bitstream_cea708_sort(mpeg_header.mpeg_bitstream_t packet) {
        // Simple bubble sort for small nearly sorted lists
        boolean swapped;
        do {
            swapped = false;
            for (int i = 1; i < packet.latent; ++i) {
                int posA = (packet.front + i - 1) % MAX_REFERENCE_FRAMES;
                int posB = (packet.front + i) % MAX_REFERENCE_FRAMES;

                cea708_header.cea708_t a = packet.cea708[posA];
                cea708_header.cea708_t b = packet.cea708[posB];

                if (a.timestamp > b.timestamp) {
                    // Swap a and b in the array
                    cea708_header.cea708_t temp = packet.cea708[posA];
                    packet.cea708[posA] = packet.cea708[posB];
                    packet.cea708[posB] = temp;

                    swapped = true;
                }
            }
        } while (swapped);
    }

    private static cea708_header.cea708_t mpeg_bitstream_cea708_at(mpeg_header.mpeg_bitstream_t packet, int pos) {
        return packet.cea708[(packet.front + pos) % MAX_REFERENCE_FRAMES];
    }
}
