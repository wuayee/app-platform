/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.common;

import com.huawei.fit.jober.aipp.dto.AippDto;
import com.huawei.fit.jober.aipp.dto.AppBuilderAppDto;
import com.huawei.fitframework.util.StringUtils;

import java.util.Map;

/**
 * @author 邬涨财 w00575064
 * @since 2024-04-20
 */
public class ConvertUtils {
    // todo 后续是否可以把 aipp 和 app结合在一起
    public static AippDto toAppDto(AppBuilderAppDto appDto) {
        Map<String, Object> attributes = appDto.getAttributes();
        String description = String.valueOf(attributes.getOrDefault("description", StringUtils.EMPTY));
        String icon = String.valueOf(attributes.getOrDefault("icon", StringUtils.EMPTY));
        String classification = String.valueOf(attributes.getOrDefault("app_type", StringUtils.EMPTY));
        return AippDto.builder()
                .name(appDto.getName())
                .description(description)
                .flowViewData(appDto.getFlowGraph().getAppearance())
                .publishUrl(appDto.getPublishUrl())
                .icon(icon)
                .appId(appDto.getId())
                .version(appDto.getVersion())
                .type(appDto.getType())
                .xiaohaiClassification(classification)
                .build();
    }
}
