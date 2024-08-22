/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fel.pipeline.huggingface.tts;

import modelengine.fitframework.annotation.Property;
import modelengine.fitframework.resource.web.Media;

import lombok.Data;

/**
 * 表示语音合成任务的输出参数。
 *
 * @author 易文渊
 * @since 2024-06-05
 */
@Data
public class TtsOutput {
    /**
     * 表示输出音频的 {@link Media}。
     */
    private Media audio;

    @Property(name = "sampling_rate")
    private Integer samplingRate;
}