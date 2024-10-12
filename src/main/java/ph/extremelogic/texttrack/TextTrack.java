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

import ph.extremelogic.libcaption.TransportSystem;
import ph.extremelogic.libcaption.caption.CaptionFrame;
import ph.extremelogic.libcaption.constant.LibCaptionStatus;
import ph.extremelogic.libcaption.model.MpegBitStream;
import ph.extremelogic.texttrack.utils.Debug;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;

import static ph.extremelogic.libcaption.Mpeg.STREAM_TYPE_H264;
import static ph.extremelogic.libcaption.Mpeg.mpegBitStreamParse;
import static ph.extremelogic.libcaption.TransportSystem.TS_PACKET_SIZE;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class TextTrack {
    public static final int EXIT_FAILURE = 1;
    public static boolean debug = false;

    public static void main(String[] args) {
        long startTime = System.nanoTime();
        String myData = "";
        int index = 0;
        TransportSystem ts = new TransportSystem();
        MpegBitStream mpegbs = new MpegBitStream();
        CaptionFrame frame = new CaptionFrame();
        byte[] pkt = new byte[TS_PACKET_SIZE];

        String tsFilePath = "./cc_minimum.ts";

        // Start timer

        try (FileInputStream fileInputStream = new FileInputStream(tsFilePath)) {
            while (fileInputStream.read(pkt) == TS_PACKET_SIZE) {
                Debug.print("DEBUG index: " + index++);
                ByteBuffer byteBuffer = ByteBuffer.wrap(pkt);
                if (ts.parsePacket(byteBuffer) == LibCaptionStatus.READY.ordinal()) {
                    double dts = ts.dtsSeconds();
                    double cts = ts.ctsSeconds();

                    Debug.print("DEBUG DTS: " + String.format("%.6f", dts) + ", CTS: " + String.format("%.6f", cts));
                    Debug.print("DEBUG ts.size: " + ts.getSize());

                    while (ts.getSize() > 0) {
                        int bytesRead = mpegBitStreamParse(mpegbs, frame, ts.getData(), ts.getSize(), STREAM_TYPE_H264, dts, cts, index);
                        ts.setData(Arrays.copyOfRange(ts.getData(), bytesRead, ts.getData().length));
                        ts.setSize(ts.getSize() - bytesRead);

                        switch (mpegbs.status) {
                            case OK:
                                break;
                            case READY:
                                System.out.println("-------------------------------");
                                myData = frame.toText();
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

        // End timer
        long endTime = System.nanoTime();

        // Calculate elapsed time
        long durationInNano = endTime - startTime;
        double durationInSeconds = durationInNano / 1_000_000_000.0;

        // Output time taken to process
        // Current processing time is 5.661783692
        // System.out.println("Processing time: " + durationInSeconds + " seconds");
    }
}
