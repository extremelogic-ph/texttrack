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

import ph.extremelogic.libcaption.caption.caption_header;
import ph.extremelogic.libcaption.cea708.cea708_header;

import java.util.ArrayList;
import java.util.List;

public class mpeg_header {
    // Constants for stream types
    public static final int STREAM_TYPE_H262 = 0x02;
    public static final int STREAM_TYPE_H264 = 0x1B;
    public static final int STREAM_TYPE_H265 = 0x24;
    public static final int H262_SEI_PACKET = 0xB2;
    public static final int H264_SEI_PACKET = 0x06;
    public static final int H265_SEI_PACKET = 0x27; // There is also 0x28
    public static final int MAX_NALU_SIZE = (6 * 1024 * 1024);
    public static final int MAX_REFERENCE_FRAMES = 64;

    // Method to initialize an MPEG bitstream (equivalent to mpeg_bitstream_init in C)
    public static void mpeg_bitstream_init(mpeg_bitstream_t bitstream) {
        bitstream.size = 0;
        bitstream.dts = 0.0;
        bitstream.cts = 0.0;
        bitstream.status = caption_header.libcaption_stauts_t.LIBCAPTION_OK;
        bitstream.front = 0;
        bitstream.latent = 0;
    }

    // SEI Message Type Enum sei_msgtype_t
    public enum sei_msgtype_t {
        sei_type_buffering_period(0),
        sei_type_pic_timing(1),
        sei_type_pan_scan_rect(2),
        sei_type_filler_payload(3),
        sei_type_user_data_registered_itu_t_t35(4),
        sei_type_user_data_unregistered(5),
        sei_type_recovery_point(6),
        sei_type_dec_ref_pic_marking_repetition(7),
        sei_type_spare_pic(8),
        sei_type_scene_info(9),
        sei_type_sub_seq_info(10),
        sei_type_sub_seq_layer_characteristics(11),
        sei_type_sub_seq_characteristics(12),
        sei_type_full_frame_freeze(13),
        sei_type_full_frame_freeze_release(14),
        sei_type_full_frame_snapshot(15),
        sei_type_progressive_refinement_segment_start(16),
        sei_type_progressive_refinement_segment_end(17),
        sei_type_motion_constrained_slice_group_set(18),
        sei_type_film_grain_characteristics(19),
        sei_type_deblocking_filter_display_preference(20),
        sei_type_stereo_video_info(21),
        UNKNOWN(-1);

        private int value;

        sei_msgtype_t(int value) {
            this.value = value;
        }

        public static sei_msgtype_t fromValue(int value) {
            for (sei_msgtype_t type : values()) {
                if (type.value == value) {
                    return type;
                }
            }
            return UNKNOWN;
        }

        public int getValue() {
            return value;
        }
    }

    // mpeg_bitstream_t
    public static class mpeg_bitstream_t {
        public int size;
        public byte[] data = new byte[MAX_NALU_SIZE + 1]; // NALU data
        public double dts; // Decoding timestamp
        public double cts; // Composition timestamp
        public caption_header.libcaption_stauts_t status; // Caption status

        // Priority queue for out of order frame processing
        // Should probablly be a linked list
        public int front;
        public int latent;
        public cea708_header.cea708_t[] cea708;

        // Constructor to initialize the MPEG bitstream
        public mpeg_bitstream_t() {
            this.data = new byte[MAX_NALU_SIZE + 1];
            this.size = 0;
            this.dts = 0.0;
            this.cts = 0.0;
            this.status = caption_header.libcaption_stauts_t.LIBCAPTION_OK;
            this.front = 0;
            this.latent = 0;
            cea708 = new cea708_header.cea708_t[MAX_REFERENCE_FRAMES];
            for (int i = 0; i < MAX_REFERENCE_FRAMES; i++) {
                cea708[i] = new cea708_header.cea708_t();
            }
        }

        public cea708_header.cea708_t getCEA708At(int pos) {
            return cea708[(front + pos) % MAX_REFERENCE_FRAMES];
        }
    }

    // sei_message_t
    public static class sei_message_t {
        int size;
        mpeg_header.sei_msgtype_t type;
        byte[] payload;
        sei_message_t next;

        public sei_message_t() {
        }

        public sei_message_t(mpeg_header.sei_msgtype_t type, byte[] payload) {
            this.type = type;
            this.payload = payload;
        }

        public int getSize() {
            return payload.length;
        }

        public sei_msgtype_t getType() {
            return type;
        }

        public byte[] getPayload() {
            return payload;
        }
    }

    // TODO it seems class above is being set directly, and it should had been through below

    // SEI Class
    public static class sei_t {
        public List<sei_message_t> messages;
        public double timestamp;

        public sei_t(double timestamp) {
            this.timestamp = timestamp;
            this.messages = new ArrayList<>();
        }

        public void init(double timestamp) {
            this.timestamp = timestamp;
            this.messages.clear();
        }

        public void free() {
            this.messages.clear();
        }
    }
}
