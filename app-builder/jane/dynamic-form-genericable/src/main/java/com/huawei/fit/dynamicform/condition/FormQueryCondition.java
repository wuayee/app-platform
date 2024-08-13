/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.dynamicform.condition;

import com.huawei.fit.http.annotation.RequestQuery;
import com.huawei.fitframework.annotation.Property;

import lombok.Data;

/**
 * 表单排序条件
 *
 * @author 熊以可
 * @since 2023/12/12
 */
@Data
public class FormQueryCondition {
    @Property(description = "表单名称过滤", example = "看图说话表单")
    @RequestQuery(name = "form_name", required = false)
    private String formName;

    @Property(description = "表单创建人过滤", example = "张三 z00000001")
    @RequestQuery(name = "create_user", required = false)
    private String createUser;

    @Property(description = "排序条件,支持字段:create_time/update_time", example = "update_time")
    @RequestQuery(name = "sort", required = false, defaultValue = "update_time")
    private String sort;

    @Property(description = "排序方向,descend表示降序，ascend表示升序", example = "descend")
    @RequestQuery(name = "order", required = false, defaultValue = "descend")
    private String order;
}
