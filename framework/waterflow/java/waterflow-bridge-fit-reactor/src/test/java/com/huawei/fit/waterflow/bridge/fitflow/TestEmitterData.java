/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.waterflow.bridge.fitflow;

import lombok.Data;

/**
 * 用于测试的EmitterData
 *
 * @author 夏斐
 * @since 1.0
 */
@Data
public class TestEmitterData<T> implements FiniteEmitterData {
    private T data;

    private boolean isEnd;

    private boolean isError;

    private String errorMessage;

    public TestEmitterData(T data, boolean isEnd, boolean isError, String message) {
        this.data = data;
        this.isEnd = isEnd;
        this.isError = isError;
        this.errorMessage = message;
    }
}
