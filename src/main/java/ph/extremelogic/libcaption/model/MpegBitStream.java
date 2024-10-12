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
package ph.extremelogic.libcaption.model;

import lombok.Getter;
import lombok.Setter;
import ph.extremelogic.libcaption.cea708.Cea708Data;
import ph.extremelogic.libcaption.constant.LibCaptionStatus;

import static ph.extremelogic.libcaption.Mpeg.MAX_NALU_SIZE;
import static ph.extremelogic.libcaption.Mpeg.MAX_REFERENCE_FRAMES;

/**
 * Represents an MPEG bitstream containing caption data, frame management, and status.
 * This class manages the storage and retrieval of compressed caption data within the context of MPEG streams.
 */
public class MpegBitStream {
    /**
     * Size of the data stored in this bitstream.
     */
    @Getter
    @Setter
    private int size;

    /**
     * Buffer to hold NALU (Network Abstraction Layer Unit) data.
     */
    @Getter
    private byte[] naluData = new byte[MAX_NALU_SIZE + 1];

    /**
     * The current status of the caption data being processed.
     */
    @Getter
    @Setter
    private LibCaptionStatus status;

    /**
     * Index representing the front of the queue for out-of-order frame processing.
     */
    @Getter
    @Setter
    private int front;

    /**
     * Index representing the latency or delayed frames in processing.
     */
    @Getter
    @Setter
    private int latent;

    /**
     * Array of Cea708Data objects representing the decoded caption data.
     */
    @Getter
    @Setter
    private Cea708Data[] cea708Data;

    /**
     * Constructs a new {@code MpegBitStream} and initializes it.
     */
    public MpegBitStream() {
        init();
    }

    /**
     * Initializes the bitstream fields to their default states.
     */
    private void init() {
        this.naluData = new byte[MAX_NALU_SIZE + 1];
        this.size = 0;
        this.status = LibCaptionStatus.OK;
        this.front = 0;
        this.latent = 0;
        this.cea708Data = new Cea708Data[MAX_REFERENCE_FRAMES];
        for (int i = 0; i < MAX_REFERENCE_FRAMES; i++) {
            cea708Data[i] = new Cea708Data();
        }
    }

    /**
     * Retrieves the CEA708 data at the specified position adjusted by the front index.
     * This method handles the circular queue nature of frame references.
     *
     * @param pos the position to retrieve the CEA708 data from
     * @return the CEA708 data at the adjusted position
     */
    public Cea708Data getCEA708At(int pos) {
        return cea708Data[(front + pos) % MAX_REFERENCE_FRAMES];
    }

    /**
     * Increments the latency index to track delayed processing.
     */
    public void incrementLatent() {
        latent++;
    }

    /**
     * Decrements the latency index to reduce the delay in processing.
     */
    public void decrementLatent() {
        latent--;
    }
}
