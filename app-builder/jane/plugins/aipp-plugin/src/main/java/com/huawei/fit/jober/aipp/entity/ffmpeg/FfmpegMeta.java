/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.entity.ffmpeg;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * FfmpegMeta
 *
 * @author y00612997
 * @since 2024/1/9
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FfmpegMeta {
    private int duration;

    private String videoExt;
}