/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.entity;

/**
 * jober的失败信息
 *
 * @author s00558940
 * @since 2024/4/29
 */
public class JoberErrorInfo {
    private String message;

    public JoberErrorInfo() {
    }

    public JoberErrorInfo(String message) {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
