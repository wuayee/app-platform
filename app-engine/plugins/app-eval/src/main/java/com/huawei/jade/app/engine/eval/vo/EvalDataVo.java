/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.eval.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 评估数据前端展示类。
 *
 * @author 董春寅
 * @since 2024-05-28
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EvalDataVo {
    private long id;
    private LocalDateTime createTime;
    private LocalDateTime modifyTime;
    private String input;
    private String output;
}
