/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.http.websocket.support;

import static com.huawei.fitframework.inspection.Validation.between;
import static com.huawei.fitframework.inspection.Validation.isTrue;
import static com.huawei.fitframework.inspection.Validation.notNull;

import com.huawei.fit.http.websocket.CloseReason;
import com.huawei.fit.http.websocket.Session;
import com.huawei.fitframework.util.StringUtils;
import com.huawei.fitframework.util.UuidUtils;

import java.nio.charset.StandardCharsets;

/**
 * 表示 {@link Session} 的抽象实现类。
 *
 * @author 季聿阶
 * @since 2024-04-30
 */
public abstract class AbstractSession implements Session {
    private final String id = UuidUtils.randomUuidString();
    private volatile int closeCode;
    private volatile String closeReason;

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public void close(int code, String reason) {
        between(code, 1000, 4999, "The close code is out of range. [code={0}]", code);
        String actualReason = StringUtils.blankIf(reason, StringUtils.EMPTY);
        int length = actualReason.getBytes(StandardCharsets.UTF_8).length;
        isTrue(length <= 123, "The close reason is too long. [length={0}]", length);
        this.closeCode = code;
        this.closeReason = reason;
        close0(code, reason);
    }

    /**
     * 表示子类需要实现的特定的关闭方法，默认为空。
     *
     * @param code 表示关闭的状态码的 {@code int}。
     * @param reason 表示关闭的原因的 {@link String}。
     */
    protected void close0(int code, String reason) {}

    @Override
    public void close(CloseReason closeReason) {
        notNull(closeReason, "The close reason cannot be null.");
        this.close(closeReason.getCode(), closeReason.getReason());
    }

    @Override
    public int getCloseCode() {
        return this.closeCode;
    }

    @Override
    public String getCloseReason() {
        return this.closeReason;
    }
}
