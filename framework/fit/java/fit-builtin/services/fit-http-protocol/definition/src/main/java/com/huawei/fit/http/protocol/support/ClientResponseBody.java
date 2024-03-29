/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.http.protocol.support;

import static com.huawei.fitframework.inspection.Validation.notNull;

import com.huawei.fit.http.protocol.ClientResponse;
import com.huawei.fit.http.protocol.ReadableMessageBody;
import com.huawei.fitframework.inspection.Nonnull;

import java.io.IOException;
import java.io.InputStream;

/**
 * 表示 {@link ReadableMessageBody} 在 {@link ClientResponse} 中的默认实现。
 *
 * @author 季聿阶 j00559309
 * @since 2023-09-19
 */
public class ClientResponseBody extends InputStream implements ReadableMessageBody {
    private final ClientResponse response;

    public ClientResponseBody(ClientResponse response) {
        this.response = notNull(response, "The client response cannot be null.");
    }

    @Override
    public int read() throws IOException {
        return this.response.readBody();
    }

    @Override
    public int read(@Nonnull byte[] bytes, int off, int len) throws IOException {
        return this.response.readBody(bytes, off, len);
    }
}
