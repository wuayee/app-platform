/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2023. All rights reserved.
 */

package com.huawei.fit.client.support;

import static com.huawei.fitframework.inspection.Validation.notNull;

import com.huawei.fit.client.Response;
import com.huawei.fitframework.serialization.ResponseMetadataV2;

/**
 * 表示 {@link Response} 的默认实现。
 *
 * @author 季聿阶 j00559309
 * @since 2022-09-19
 */
public class DefaultResponse implements Response {
    private final ResponseMetadataV2 metadata;
    private final Object data;

    public DefaultResponse(ResponseMetadataV2 metadata, Object data) {
        this.metadata = notNull(metadata, "The metadata cannot be null.");
        this.data = data;
    }

    @Override
    public ResponseMetadataV2 metadata() {
        return this.metadata;
    }

    @Override
    public Object data() {
        return this.data;
    }
}
