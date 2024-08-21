/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.task.dto;

import modelengine.fitframework.annotation.Property;
import modelengine.fitframework.validation.constraints.NotBlank;
import com.huawei.jade.common.query.PageQueryParam;

import lombok.Data;

/**
 * 表示评估任务询参数。
 *
 * @author 何嘉斌
 * @since 2024/08/12
 */
@Data
public class EvalTaskQueryParam extends PageQueryParam {
    @Property(description = "应用唯一标识", required = true)
    @NotBlank(message = "appId should not be null")
    private String appId;
}
