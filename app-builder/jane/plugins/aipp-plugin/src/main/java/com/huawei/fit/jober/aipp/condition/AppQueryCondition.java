/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.condition;

import com.huawei.fit.http.annotation.RequestParam;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 为app提供查询条件
 *
 * @author 邬涨财 w00575064
 * @since 2024-05-06
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppQueryCondition {
    private String tenantId;

    @RequestParam(name = "type", required = false, defaultValue = "app")
    private String type;

    private List<String> ids;

    @RequestParam(name = "name", required = false)
    private String name;

    @RequestParam(name = "state", required = false)
    private String state;

    private List<String> excludeNames;
}
