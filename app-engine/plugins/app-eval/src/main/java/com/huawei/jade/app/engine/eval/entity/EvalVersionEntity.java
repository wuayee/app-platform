/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.eval.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 表示评估数据集版本的实体对象。
 *
 * @author 何嘉斌
 * @since 2024-08-05
 */
@Data
public class EvalVersionEntity {
    /**
     * 版本号。
     */
    private Long version;

    /**
     * 版本创建时间。
     */
    private LocalDateTime createdTime;
}
