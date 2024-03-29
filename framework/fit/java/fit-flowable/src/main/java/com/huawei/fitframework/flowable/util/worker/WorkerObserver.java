/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fitframework.flowable.util.worker;

/**
 * 表示 {@link Worker} 接收到待消费元素、异常终结信号或正常终结信号时的观察者。
 *
 * @author 何天放 h00679269
 * @since 2024-02-20
 */
public interface WorkerObserver {
    /**
     * 表示 {@link Worker} 接收到应消费数据时回传应消费数据的接口。
     *
     * @param data 表示应消费的数据 {@code Object}。
     * @param id 表示 Worker 标识的 {@code long}。
     */
    void onWorkerConsumed(Object data, long id);

    /**
     * 表示 {@link Worker} 接收到异常终结信号时回传异常终结信号的接口。
     *
     * @param cause 表示应消费异常的 {@code cause}。
     */
    void onWorkerFailed(Exception cause);

    /**
     * 表示 {@link Worker} 接收到正常终结信号时回传正常终结信号的接口。
     */
    void onWorkerCompleted();
}
