/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2023. All rights reserved.
 */

package com.huawei.fitframework.broker.server.support;

import static com.huawei.fitframework.inspection.Validation.notNull;

import com.huawei.fitframework.broker.server.Response;
import com.huawei.fitframework.serialization.ResponseMetadataV2;

import java.lang.reflect.Type;

/**
 * 表示 {@link Response} 的默认实现。
 *
 * @author 季聿阶 j00559309
 * @since 2022-09-13
 */
public class DefaultResponse implements Response {
    private final ResponseMetadataV2 metadata;
    private final Type type;
    private final Object data;

    public DefaultResponse(ResponseMetadataV2 metadata, Type type, Object data) {
        this.metadata = notNull(metadata, "No metadata.");
        this.type = type;
        this.data = data;
    }

    @Override
    public ResponseMetadataV2 metadata() {
        return this.metadata;
    }

    @Override
    public Type type() {
        return this.type;
    }

    @Override
    public Object data() {
        return this.data;
    }
}
