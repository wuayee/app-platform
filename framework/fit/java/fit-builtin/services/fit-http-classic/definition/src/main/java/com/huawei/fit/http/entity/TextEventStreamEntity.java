/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.http.entity;

import com.huawei.fitframework.flowable.Choir;

/**
 * 表示文本事件流的消息体数据。
 *
 * @author 易文渊
 * @since 2024-07-16
 */
public interface TextEventStreamEntity extends Entity {
    /**
     * 获取文本事件流。
     *
     * @return 表示文本事件流的 {@link Choir}{@code <}{@link TextEvent}{@code >}。
     */
    Choir<TextEvent> stream();
}