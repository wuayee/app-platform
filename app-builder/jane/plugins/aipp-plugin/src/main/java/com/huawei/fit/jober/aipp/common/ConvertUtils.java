/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.common;

import com.huawei.fit.jober.aipp.dto.AippCreateDto;
import com.huawei.fit.jober.aipp.dto.AippDto;
import com.huawei.fit.jober.aipp.dto.AppBuilderAppDto;
import com.huawei.fit.jober.aipp.genericable.entity.AippCreate;
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

    /**
     * 将 {@link AippCreateDto} 类转化成 {@link AippCreate}。
     *
     * @param aippCreateDto 表示创建 Aipp 响应体的 {@link AippCreateDto}。
     * @return 表示转化后的创建 Aipp 响应体实体类对象的 {@link AippCreate}。
     */
    public static AippCreate toAippCreate(AippCreateDto aippCreateDto) {
        return AippCreate.builder()
                .aippId(aippCreateDto.getAippId())
                .toolUniqueName(aippCreateDto.getToolUniqueName())
                .version(aippCreateDto.getVersion())
                .build();
    }

    /**
     * 将 {@link AippCreate} 类转化成 {@link AippCreateDto}。
     *
     * @param aippCreate 表示创建 Aipp 响应体的 {@link AippCreate}。
     * @return 表示转化后的创建 Aipp 响应体实体类对象的 {@link AippCreateDto}。
     */
    public static AippCreateDto toAippCreateDto(AippCreate aippCreate) {
        return AippCreateDto.builder()
                .aippId(aippCreate.getAippId())
                .toolUniqueName(aippCreate.getToolUniqueName())
                .version(aippCreate.getVersion())
                .build();
    }
}
