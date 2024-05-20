/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.waterflow.bridge.fitflow;

/**
 * 测试用的数据构建
 *
 * @param <D> 原始数据
 * @param <T> TestEmitterData中的实际数据类型
 * @since 1.0
 */
class TestFiniteEmitterDataBuilder<D, T> implements FiniteEmitterDataBuilder<D, TestEmitterData<T>> {
    @Override
    public TestEmitterData<T> data(D data) {
        return new TestEmitterData(data, false, false, null);
    }

    @Override
    public TestEmitterData<T> end() {
        return new TestEmitterData<>(null, true, false, null);
    }

    @Override
    public TestEmitterData<T> error(String message) {
        return new TestEmitterData<>(null, false, true, message);
    }
}
