/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2024. All rights reserved.
 */

package com.huawei.fit.http.entity.support;

import com.huawei.fit.http.HttpMessage;
import com.huawei.fit.http.entity.ObjectEntity;
import com.huawei.fit.http.protocol.MimeType;
import modelengine.fitframework.inspection.Nonnull;

/**
 * 表示 {@link ObjectEntity} 的默认实现。
 *
 * @param <T> 表示对象类型的 {@link T}。
 * @author 季聿阶
 * @since 2022-07-14
 */
public class DefaultObjectEntity<T> extends AbstractEntity implements ObjectEntity<T> {
    private final T obj;

    /**
     * 创建对象类型的消息体数据对象。
     *
     * @param httpMessage 表示消息体数据所属的 Http 消息的 {@link HttpMessage}。
     * @param obj 表示指定对象的 {@link Object}。
     */
    public DefaultObjectEntity(HttpMessage httpMessage, T obj) {
        super(httpMessage);
        this.obj = obj;
    }

    @Override
    public T object() {
        return this.obj;
    }

    @Nonnull
    @Override
    public MimeType resolvedMimeType() {
        return MimeType.APPLICATION_JSON;
    }
}
