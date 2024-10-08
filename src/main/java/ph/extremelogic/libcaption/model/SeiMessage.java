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

import lombok.Data;
import lombok.NoArgsConstructor;
import ph.extremelogic.libcaption.constant.SeiMessageType;

/**
 * The {@code SeiMessage} class represents a single SEI (Supplemental Enhancement Information) message
 * in a video stream. An SEI message contains a type, a payload, and potentially links to the next SEI message.
 *
 * This class provides a constructor for initializing a message with a specific type and payload, and it also supports
 * a default no-argument constructor.
 */
@Data
@NoArgsConstructor
public class SeiMessage {
    /** The size of the SEI message payload in bytes. */
    private int size;

    /** The type of SEI message, represented by a {@code SeiMessageType} enum. */
    private SeiMessageType type;

    /** The payload data of the SEI message, stored as a byte array. */
    private byte[] payload;

    /** A reference to the next SEI message, enabling the creation of a linked list of SEI messages. */
    private SeiMessage next;

    /**
     * Constructs a new {@code SeiMessage} object with the specified type and payload.
     *
     * @param type the {@code SeiMessageType} representing the type of SEI message
     * @param payload the byte array representing the payload data of the SEI message
     */
    public SeiMessage(SeiMessageType type, byte[] payload) {
        this.type = type;
        this.payload = payload;
    }
}
