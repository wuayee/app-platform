/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.uid.po;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 机器 ORM 对象。
 *
 * @author 何嘉斌
 * @since 2024-07-29
 */
@Data
public class WorkerPo {
    /**
     * 表示主键，机器号。
     */
    private int workerId;

    /**
     * 表示创建时间。
     */
    private LocalDateTime createdAt;

    /**
     * 表示更新时间。
     */
    private LocalDateTime updatedAt;

    /**
     * 表示创建者。
     */
    private String createdBy;

    /**
     * 表示更新者。
     */
    private String updatedBy;
}
