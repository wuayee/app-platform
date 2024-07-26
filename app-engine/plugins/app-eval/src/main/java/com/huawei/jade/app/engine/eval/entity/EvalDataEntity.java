/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.eval.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 表示评估数据的实体对象
 *
 * @author 兰宇晨
 * @since 2024-07-24
 */
@Data
public class EvalDataEntity {
    /**
     * 主键。
     */
    private Long id;

    /**
     * 内容。
     */
    private String content;

    /**
     * 创建时间。
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间。
     */
    private LocalDateTime updatedAt;
}
