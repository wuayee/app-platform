/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.eval.dto;

import com.huawei.fitframework.annotation.Property;
import com.huawei.fitframework.validation.constraints.NotEmpty;
import com.huawei.fitframework.validation.constraints.Range;

import lombok.Data;

/**
 * 表示评估数据修改传输对象。
 *
 * @author 何嘉斌
 * @see com.huawei.jade.app.engine.eval.controller.EvalDataController#updateEvalData。
 * @since 2024-07-25
 */
@Data
public class EvalDataUpdateDto {
    @Property(description = "数据集编号", required = true, defaultValue = "1")
    @Range(min = 1, max = Long.MAX_VALUE, message = "The data id is invalid.")
    private Long dataId;

    @Property(description = "评估内容", required = true)
    @NotEmpty(message = "The content cannot be empty.")
    private String content;
}
