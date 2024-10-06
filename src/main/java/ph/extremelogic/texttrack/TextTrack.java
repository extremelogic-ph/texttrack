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
package ph.extremelogic.texttrack;

import ph.extremelogic.libcaption.caption.caption_c;
import ph.extremelogic.libcaption.caption.caption_header;
import ph.extremelogic.libcaption.mpeg.mpeg_header.mpeg_bitstream_t;
import ph.extremelogic.libcaption.ts.ts_c;
import ph.extremelogic.libcaption.ts.ts_header;
import ph.extremelogic.texttrack.utils.Debug;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;

import static ph.extremelogic.libcaption.mpeg.mpeg_c.mpeg_bitstream_parse;
import static ph.extremelogic.libcaption.mpeg.mpeg_header.STREAM_TYPE_H264;

public class TextTrack {

    // Exit failure constant
    public static final int EXIT_FAILURE = 1;
    private static final int TS_PACKET_SIZE = 188;
    public static boolean debug = false;

    public static void main(String[] args) {
        String myData = "";
        int index = 0;
        ts_c ts = new ts_c(); // Equivalent to ts_t
        ts_header th = new ts_header();
        mpeg_bitstream_t mpegbs = new mpeg_bitstream_t(); // Equivalent to mpeg_bitstream_t
        caption_c.caption_frame_t frame = new caption_c.caption_frame_t(); // Equivalent to caption_frame_t
        byte[] pkt = new byte[TS_PACKET_SIZE]; // Packet data

        ts.ts_init();

        frame.caption_frame_init();

        // TODO implement this
        //mpegBitstreamInit(mpegbs);

        String tsFilePath = "./cc_minimum.ts";
        try (FileInputStream fileInputStream = new FileInputStream(tsFilePath)) {
            while (fileInputStream.read(pkt) == TS_PACKET_SIZE) {
                Debug.print("DEBUG index: " + index++);
                if (ts.ts_parse_packet(pkt) == caption_header.libcaption_stauts_t.LIBCAPTION_READY.ordinal()) {
                    double dts = th.ts_dts_seconds(ts.ts); // Parse DTS in seconds
                    double cts = th.ts_cts_seconds(ts.ts); // Parse CTS in seconds

                    Debug.print("DEBUG DTS: " + String.format("%.6f", dts) + ", CTS: " + String.format("%.6f", cts));
                    Debug.print("DEBUG ts.size: " + ts.ts.size);

                    while (ts.ts.size > 0) {
                        int bytesRead = mpeg_bitstream_parse(mpegbs, frame, ts.ts.data, ts.ts.size, STREAM_TYPE_H264, dts, cts, index);
                        ts.ts.data = Arrays.copyOfRange(ts.ts.data, bytesRead, ts.ts.data.length);
                        ts.ts.size -= bytesRead;

                        switch (mpegbs.status) {
                            case LIBCAPTION_OK:
                                break;
                            case LIBCAPTION_READY:
                                System.out.println("-------------------------------");
                                myData = frame.caption_frame_to_text();
                                System.out.println("data:\n" + myData);
                                break;
                            default:
                                System.exit(EXIT_FAILURE);
                                break;
                        }
                    }
                } else {
                    Debug.print("Not yet ready");
                }
            }
        } catch (IOException e) {
            System.out.println("Failed to open input");
            System.exit(EXIT_FAILURE);
        }
    }
}

