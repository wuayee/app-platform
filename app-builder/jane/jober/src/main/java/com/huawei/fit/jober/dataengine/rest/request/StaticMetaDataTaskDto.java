/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.dataengine.rest.request;

import com.huawei.fit.jober.entity.Filter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

/**
 * 创建定时任务请求类
 *
 * @author 00693950
 * @since 2023/6/16
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StaticMetaDataTaskDto {
    /**
     * 任务数据源定义唯一标识
     */
    private String taskSourceId;

    /**
     * 任务定义ID
     */
    private String taskDefinitionId;

    /**
     * 任务类型ID
     */
    private String taskTypeId;

    /**
     * 数据来源平台
     */
    private String sourceApp;

    /**
     * 数据获取过滤条件
     */
    private Filter filter;

    /**
     * 任务其他条件
     */
    private Map<String, String> properties;
}
