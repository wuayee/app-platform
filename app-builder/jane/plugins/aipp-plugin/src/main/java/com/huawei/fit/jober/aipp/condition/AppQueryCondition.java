/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.condition;

import com.huawei.fit.http.annotation.RequestQuery;
import com.huawei.fitframework.annotation.Property;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author 邬涨财 w00575064
 * @since 2024-05-06
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppQueryCondition {
    private String tenantId;

    @RequestQuery(name = "type", defaultValue = "app")
    private String type;

    private List<String> ids;

    @RequestQuery(name = "name", required = false)
    private String name;

    @RequestQuery(name = "state", required = false)
    private String state;
}
