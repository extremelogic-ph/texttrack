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

import java.util.ArrayList;
import java.util.List;

/**
 * The {@code Sei} class represents a collection of SEI (Supplemental Enhancement Information) messages
 * that are associated with a specific timestamp in a video stream. SEI messages provide auxiliary
 * information to video decoders.
 *
 * This class provides methods to initialize and manage the SEI message list.
 */
@Data
public class Sei {
    /** A list of {@code SeiMessage} objects, representing the SEI messages in the stream. */
    private List<SeiMessage> messages;

    /** The timestamp associated with the SEI messages. */
    private double timestamp;

    /**
     * Constructs a {@code Sei} object with the specified timestamp and initializes the message list.
     *
     * @param timestamp the timestamp associated with the SEI messages
     */
    public Sei(double timestamp) {
        this.timestamp = timestamp;
        this.messages = new ArrayList<>();
    }

    /**
     * Initializes the {@code Sei} object by setting a new timestamp and clearing the message list.
     *
     * @param timestamp the new timestamp for the SEI messages
     */
    public void init(double timestamp) {
        this.timestamp = timestamp;
        this.messages.clear();
    }

    /**
     * Frees the resources by clearing the message list.
     */
    public void free() {
        this.messages.clear();
    }
}
