/*
 *  Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.bff.client.flowsengine.request;

import com.huawei.fitframework.annotation.Component;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Map;

/**
 * 分页查询清洗列表request body
 *
 * @author y00679285
 * @since 2024/2/23
 */
@Component
@Getter
@Setter
public class CleanDataListQuery implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 任务id
     */
    private String taskId;

    /**
     * 任务名称
     */
    private String title;

    /**
     * 来源数据集版本
     */
    private String version;

    /**
     * 排序规则
     * key:[title:名称; version：版本; created_date：创建时间]
     * value:[type:排序方式; order:排序优先级]
     */
    private Map<String, Map<String, Object>> sortMap;

    /**
     * 查询数量
     */
    private int pageSize;

    /**
     * 查询页数
     */
    private int pageNum;
}
