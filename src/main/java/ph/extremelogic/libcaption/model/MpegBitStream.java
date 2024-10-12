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

public class MpegBitStream {
    @Getter
    @Setter
    private int size;
    public byte[] data = new byte[MAX_NALU_SIZE + 1]; // NALU data

    @Getter
    @Setter
    private LibCaptionStatus status; // Caption status

    // Priority queue for out of order frame processing
    // Should probablly be a linked list
    @Getter
    @Setter
    private int front;

    @Getter
    @Setter
    private int latent;

    public Cea708Data[] cea708Data;

    public void incrementLatent() {
        latent++;
    }

    public void decrementLatent() {
        latent--;
    }

    // Constructor to initialize the MPEG bitstream
    public MpegBitStream() {
        init();
    }

    private void init() {
        this.data = new byte[MAX_NALU_SIZE + 1];
        this.size = 0;
        this.status = LibCaptionStatus.OK;
        this.front = 0;
        this.latent = 0;
        cea708Data = new Cea708Data[MAX_REFERENCE_FRAMES];
        for (int i = 0; i < MAX_REFERENCE_FRAMES; i++) {
            cea708Data[i] = new Cea708Data();
        }
    }

    public Cea708Data getCEA708At(int pos) {
        return cea708Data[(front + pos) % MAX_REFERENCE_FRAMES];
    }
}