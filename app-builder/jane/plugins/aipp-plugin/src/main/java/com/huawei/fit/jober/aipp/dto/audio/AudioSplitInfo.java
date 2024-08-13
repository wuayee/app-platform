/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.dto.audio;

import com.huawei.fitframework.annotation.Property;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * AudioSplitInfo
 *
 * @author 易文渊
 * @since 2024/1/9
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AudioSplitInfo {
    @Property(description = "音频目录")
    private String dirPath;
    @Property(description = "音频分段大小")
    private int segmentSize;
}