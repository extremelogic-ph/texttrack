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
package ph.extremelogic.libcaption.constant;

import lombok.Getter;

/**
 * The {@code SeiMessageType} enum represents different types of Supplemental Enhancement Information (SEI)
 * messages used in video streams. SEI messages provide auxiliary information to video decoders.
 * Each message type is associated with an integer value.
 */
@Getter
public enum SeiMessageType {

    /** SEI message type for buffering period with a value of 0. */
    SEI_TYPE_BUFFERING_PERIOD(0),

    /** SEI message type for picture timing with a value of 1. */
    SEI_TYPE_PIC_TIMING(1),

    /** SEI message type for pan-scan rectangle with a value of 2. */
    SEI_TYPE_PAN_SCAN_RECT(2),

    /** SEI message type for filler payload with a value of 3. */
    SEI_TYPE_FILLER_PAYLOAD(3),

    /** SEI message type for registered user data with ITU-T T.35 protocol and a value of 4. */
    SEI_TYPE_USER_DATA_REGISTERED_ITU_T_T_35(4),

    /** SEI message type for unregistered user data with a value of 5. */
    SEI_TYPE_USER_DATA_UNREGISTERED(5),

    /** SEI message type for recovery point with a value of 6. */
    SEI_TYPE_RECOVERY_POINT(6),

    /** SEI message type for dec reference picture marking repetition with a value of 7. */
    SEI_TYPE_DEC_REF_PIC_MARKING_REPETITION(7),

    /** SEI message type for spare picture with a value of 8. */
    SEI_TYPE_SPARE_PIC(8),

    /** SEI message type for scene information with a value of 9. */
    SEI_TYPE_SCENE_INFO(9),

    /** SEI message type for sub-sequence information with a value of 10. */
    SEI_TYPE_SUB_SEQ_INFO(10),

    /** SEI message type for sub-sequence layer characteristics with a value of 11. */
    SEI_TYPE_SUB_SEQ_LAYER_CHARACTERISTICS(11),

    /** SEI message type for sub-sequence characteristics with a value of 12. */
    SEI_TYPE_SUB_SEQ_CHARACTERISTICS(12),

    /** SEI message type for full frame freeze with a value of 13. */
    SEI_TYPE_FULL_FRAME_FREEZE(13),

    /** SEI message type for full frame freeze release with a value of 14. */
    SEI_TYPE_FULL_FRAME_FREEZE_RELEASE(14),

    /** SEI message type for full frame snapshot with a value of 15. */
    SEI_TYPE_FULL_FRAME_SNAPSHOT(15),

    /** SEI message type for progressive refinement segment start with a value of 16. */
    SEI_TYPE_PROGRESSIVE_REFINEMENT_SEGMENT_START(16),

    /** SEI message type for progressive refinement segment end with a value of 17. */
    SEI_TYPE_PROGRESSIVE_REFINEMENT_SEGMENT_END(17),

    /** SEI message type for motion constrained slice group set with a value of 18. */
    SEI_TYPE_MOTION_CONSTRAINED_SLICE_GROUP_SET(18),

    /** SEI message type for film grain characteristics with a value of 19. */
    SEI_TYPE_FILM_GRAIN_CHARACTERISTICS(19),

    /** SEI message type for deblocking filter display preference with a value of 20. */
    SEI_TYPE_DEBLOCKING_FILTER_DISPLAY_PREFERENCE(20),

    /** SEI message type for stereo video information with a value of 21. */
    SEI_TYPE_STEREO_VIDEO_INFO(21),

    /** Unknown SEI message type with a value of -1. */
    UNKNOWN(-1);

    /** The integer value associated with the SEI message type. */
    private final int value;

    /**
     * Constructs a {@code SeiMessageType} enum with the specified integer value.
     *
     * @param value the integer value representing the SEI message type
     */
    SeiMessageType(int value) {
        this.value = value;
    }

    /**
     * Returns the {@code SeiMessageType} corresponding to the given integer value.
     * If the value does not match any known SEI message type, {@code UNKNOWN} is returned.
     *
     * @param value the integer value representing the SEI message type
     * @return the corresponding {@code SeiMessageType}, or {@code UNKNOWN} if the value is not recognized
     */
    public static SeiMessageType fromValue(int value) {
        for (SeiMessageType type : values()) {
            if (type.value == value) {
                return type;
            }
        }
        return UNKNOWN;
    }
}