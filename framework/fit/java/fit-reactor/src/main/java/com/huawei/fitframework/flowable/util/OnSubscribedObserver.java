/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fitframework.flowable.util;

/**
 * 表示订阅关系发生的观察者。
 *
 * @author 何天放 h00679269
 * @since 2024-05-22
 */
public interface OnSubscribedObserver {
    /**
     * 通知订阅关系发生。
     */
    void notifyOnSubscribed();
}
