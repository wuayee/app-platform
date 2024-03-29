/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.http.protocol.support;

import static com.huawei.fitframework.inspection.Validation.notNull;

import com.huawei.fit.http.protocol.ClientRequest;
import com.huawei.fit.http.protocol.WritableMessageBody;
import com.huawei.fitframework.inspection.Nonnull;

import java.io.IOException;
import java.io.OutputStream;

/**
 * 表示 {@link WritableMessageBody} 在 {@link ClientRequest} 中的默认实现。
 *
 * @author 季聿阶 j00559309
 * @since 2023-09-19
 */
public class ClientRequestBody extends OutputStream implements WritableMessageBody {
    private final ClientRequest request;

    public ClientRequestBody(ClientRequest request) {
        this.request = notNull(request, "The client request cannot be null.");
    }

    @Override
    public void write(int b) throws IOException {
        this.request.writeBody(b);
    }

    @Override
    public void write(@Nonnull byte[] bytes, int off, int len) throws IOException {
        this.request.writeBody(bytes, off, len);
    }

    @Override
    public void flush() throws IOException {
        this.request.flush();
    }
}
