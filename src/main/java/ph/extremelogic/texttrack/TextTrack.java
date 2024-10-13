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

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import ph.extremelogic.libcaption.TransportSystem;
import ph.extremelogic.libcaption.caption.CaptionFrame;
import ph.extremelogic.libcaption.constant.LibCaptionStatus;
import ph.extremelogic.libcaption.model.MpegBitStream;
import ph.extremelogic.texttrack.utils.Debug;

import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import static ph.extremelogic.libcaption.Mpeg.STREAM_TYPE_H264;
import static ph.extremelogic.libcaption.Mpeg.mpegBitStreamParse;
import static ph.extremelogic.libcaption.TransportSystem.TS_PACKET_SIZE;

/**
 * The TextTrack class is responsible for processing transport stream files to extract caption data.
 */
public class TextTrack {
    private static final int EXIT_FAILURE = 1;
    public static final boolean debug = false;

    /**
     * Main entry point for the TextTrack application.
     * @param args Command line arguments, expects the first argument to be the path to the transport stream file.
     */
    public static void main(String[] args) {
        long startTime = System.nanoTime();
        if (args.length < 1) {
            System.err.println("Usage: java TextTrack <path_to_transport_stream_file>");
            System.exit(EXIT_FAILURE);
        }
        String tsFilePath = args[0];

        try (FileChannel fileChannel = FileChannel.open(Path.of(tsFilePath), StandardOpenOption.READ)) {
            processTransportStream(fileChannel);
        } catch (IOException e) {
            System.err.println("Failed to open input file: " + tsFilePath);
            System.exit(EXIT_FAILURE);
        }

        logProcessingTime(startTime);
    }

    /**
     * Processes the transport stream file to extract and process packets.
     * @param fileChannel The file channel associated with the transport stream file.
     * @throws IOException If there is an issue reading the file.
     */
    private static void processTransportStream(FileChannel fileChannel) throws IOException {
        TransportSystem ts = new TransportSystem();
        MpegBitStream mpegbs = new MpegBitStream();
        CaptionFrame frame = new CaptionFrame();
        ByteBuffer pkt = ByteBuffer.allocateDirect(TS_PACKET_SIZE);

        int index = 0;
        while (fileChannel.read(pkt) == TS_PACKET_SIZE) {
            pkt.flip();
            Debug.print("DEBUG index: " + index++);

            if (ts.parsePacket(pkt) == LibCaptionStatus.READY.ordinal()) {
                processPacket(ts, mpegbs, frame);
            } else {
                Debug.print("Not yet ready");
            }

            pkt.clear();
        }
    }

    /**
     * Processes each packet extracted from the transport stream.
     * @param ts The transport system handling the stream packets.
     * @param mpegbs The MPEG bit stream to parse.
     * @param frame The caption frame to update.
     */
    private static void processPacket(TransportSystem ts, MpegBitStream mpegbs, CaptionFrame frame) {
        double dts = ts.dtsSeconds();
        double cts = ts.ctsSeconds();

        Debug.print("DEBUG DTS: " + String.format("%.6f", dts) + ", CTS: " + String.format("%.6f", cts));
        Debug.print("DEBUG ts.size: " + ts.getSize());

        while (ts.getSize() > 0) {
            int bytesRead = mpegBitStreamParse(mpegbs, frame, ts.getData(), ts.getSize(), STREAM_TYPE_H264, dts, cts);
            ts.setData(Arrays.copyOfRange(ts.getData(), bytesRead, ts.getData().length));
            ts.setSize(ts.getSize() - bytesRead);

            handleMpegBitStreamStatus(mpegbs, frame);
        }
    }

    /**
     * Handles the status of the MPEG bitstream after processing a packet.
     * @param mpegbs The MPEG bitstream.
     * @param frame The caption frame to display if ready.
     */
    private static void handleMpegBitStreamStatus(MpegBitStream mpegbs, CaptionFrame frame) {
        switch (mpegbs.getStatus()) {
            case OK:
                break;
            case READY:
                System.out.println("-------------------------------");
                String captionData = frame.toText();
                System.out.println("data:\n" + captionData);
                break;
            default:
                System.exit(EXIT_FAILURE);
                break;
        }
    }

    /**
     * Logs the processing time from the start to the end of the application run.
     * @param startTime The start time of the processing in nanoseconds.
     */
    private static void logProcessingTime(long startTime) {
        long endTime = System.nanoTime();
        double durationInSeconds = (endTime - startTime) / 1_000_000_000.0;
        // System.out.println("Processing time: " + durationInSeconds);
    }
}