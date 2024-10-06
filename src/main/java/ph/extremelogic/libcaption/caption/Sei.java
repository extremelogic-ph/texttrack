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
package ph.extremelogic.libcaption.caption;

import java.util.ArrayList;
import java.util.List;

public class Sei {
    private double timestamp;
    private List<SeiMessage> messages;

    public Sei() {
        this.timestamp = 0;
        this.messages = new ArrayList<>();
    }

    public void init(double timestamp) {
        this.timestamp = timestamp;
        this.messages.clear();
    }

    public void addMessage(SeiMessage message) {
        this.messages.add(message);
    }

    public List<SeiMessage> getMessages() {
        return messages;
    }

    public caption_header.libcaption_stauts_t parse(byte[] data, double timestamp) {
        init(timestamp);
        int offset = 0;

        while (offset < data.length - 1) {
            int payloadType = 0;
            int payloadSize = 0;

            // Parse payload type
            while (offset < data.length && data[offset] == (byte) 0xFF) {
                payloadType += 255;
                offset++;
            }
            if (offset >= data.length) {
                return caption_header.libcaption_stauts_t.LIBCAPTION_ERROR;
            }
            payloadType += data[offset] & 0xFF;
            offset++;

            // Parse payload size
            while (offset < data.length && data[offset] == (byte) 0xFF) {
                payloadSize += 255;
                offset++;
            }
            if (offset >= data.length) {
                return caption_header.libcaption_stauts_t.LIBCAPTION_ERROR;
            }
            payloadSize += data[offset] & 0xFF;
            offset++;

            if (payloadSize > 0 && offset + payloadSize <= data.length) {
                byte[] payload = new byte[payloadSize];
                System.arraycopy(data, offset, payload, 0, payloadSize);
                SeiMessage message = new SeiMessage(payloadType, payload);
                addMessage(message);
                offset += payloadSize;
            } else if (payloadSize > 0) {
                return caption_header.libcaption_stauts_t.LIBCAPTION_ERROR;
            }
        }

        return caption_header.libcaption_stauts_t.LIBCAPTION_OK;
    }

    public int renderSize() {
        int size = 0;
        for (SeiMessage message : messages) {
            size += message.getSize() + 2; // +2 for type and size bytes
        }
        return size + 1; // +1 for the trailing byte
    }

    public byte[] render() {
        byte[] data = new byte[renderSize()];
        int offset = 0;

        for (SeiMessage message : messages) {
            int type = message.getType();
            int size = message.getSize();

            // Write payload type
            while (type > 255) {
                data[offset++] = (byte) 0xFF;
                type -= 255;
            }
            data[offset++] = (byte) type;

            // Write payload size
            while (size > 255) {
                data[offset++] = (byte) 0xFF;
                size -= 255;
            }
            data[offset++] = (byte) size;

            // Write payload
            System.arraycopy(message.getPayload(), 0, data, offset, message.getSize());
            offset += message.getSize();
        }

        // Add trailing byte
        data[offset] = (byte) 0x80;

        return data;
    }

    public void dump() {
        System.out.println("SEI Messages:");
        for (SeiMessage message : messages) {
            System.out.println("Type: " + message.getType() + ", Size: " + message.getSize());
            // You can add more detailed dumping if needed
        }
    }
}
