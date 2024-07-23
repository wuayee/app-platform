/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.waterflow.edatamate.enums;

import lombok.Getter;

/**
 * 任务启动类型
 *
 * @author 00558940
 * @since 2024/02/28
 */
@Getter
public enum TaskStartType {
    NORMAL("normal"),
    RETRY("retry");

    private final String code;

    TaskStartType(String code) {
        this.code = code;
    }
}
