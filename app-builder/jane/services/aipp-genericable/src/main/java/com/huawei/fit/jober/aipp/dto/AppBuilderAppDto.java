/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.dto;

import com.huawei.fitframework.annotation.Property;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 表示 app 应用的 dto 对象。
 *
 * @author 邬涨财 w00575064
 * @since 2024-04-17
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppBuilderAppDto {
    private String name;
    private String type;
    private String createBy;
    private String updateBy;
    private String version;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;
    private AppBuilderConfigDto config;
    private AppBuilderFlowGraphDto flowGraph;
    private String id;
    private Map<String, Object> attributes;
    private String state;

    @Property(description = "aipp 发布链接")
    private String publishUrl;

    @Property(description = "aipp 发布描述")
    private String publishedDescription;

    @Property(description = "aipp 发布更新日志")
    private String publishedUpdateLog;
}
