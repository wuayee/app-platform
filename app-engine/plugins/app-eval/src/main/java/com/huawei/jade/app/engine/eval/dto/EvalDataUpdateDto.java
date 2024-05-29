/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.eval.dto;

import com.huawei.fitframework.annotation.Property;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 更新评估数据所需参数。
 *
 * @author 董春寅
 * @since 2024-05-28
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EvalDataUpdateDto {
    @Property(description = "需要编辑的数据id")
    private long id;

    @Property(description = "输入")
    private String input;

    @Property(description = "期望输出")
    private String output;
}
