/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.eval.po;

/**
 * 评估任务状态枚举。
 *
 * @author 董春寅
 * @since 2024-05-28
 */
public enum EvalTaskStatus {
    NOT_START(0),
    IN_PROGRESS(1),
    FAILURE(2),
    FINISH(3);

    private int code;

    EvalTaskStatus(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
