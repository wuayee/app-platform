/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.dataengine.rest.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import modelengine.fit.jober.entity.Filter;

import java.util.Map;

/**
 * 创建定时任务请求类
 *
 * @author 晏钰坤
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
