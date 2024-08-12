/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.dynamicform.condition;

import com.huawei.fit.http.annotation.RequestQuery;
import com.huawei.fitframework.annotation.Property;

import lombok.Data;

/**
 * 分页条件
 *
 * @author 熊以可
 * @since 2023/12/13
 */
@Data
public class PaginationCondition {
    @Property(description = "页码(1开始)", example = "1")
    @RequestQuery(name = "pageNum", required = false)
    private Integer pageNum = 1;

    @Property(description = "每页大小", example = "10")
    @RequestQuery(name = "pageSize", required = false)
    private Integer pageSize = 10;
}
