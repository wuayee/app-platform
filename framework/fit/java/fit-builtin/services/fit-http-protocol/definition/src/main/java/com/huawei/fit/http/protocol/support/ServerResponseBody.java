/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.http.protocol.support;

import static com.huawei.fitframework.inspection.Validation.notNull;

import com.huawei.fit.http.protocol.ServerResponse;
import com.huawei.fit.http.protocol.WritableMessageBody;
import com.huawei.fitframework.inspection.Nonnull;

import java.io.IOException;
import java.io.OutputStream;

/**
 * 表示 {@link WritableMessageBody} 在 {@link ServerResponse} 中的默认实现。
 *
 * @author 季聿阶 j00559309
 * @since 2023-09-19
 */
public class ServerResponseBody extends OutputStream implements WritableMessageBody {
    private final ServerResponse response;

    public ServerResponseBody(ServerResponse response) {
        this.response = notNull(response, "The server response cannot be null.");
    }

    @Override
    public void write(int b) throws IOException {
        this.response.writeBody(b);
    }

    @Override
    public void write(@Nonnull byte[] bytes, int off, int len) throws IOException {
        this.response.writeBody(bytes, off, len);
    }

    @Override
    public void flush() throws IOException {
        this.response.flush();
    }
}
