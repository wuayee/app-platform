/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 */

package com.huawei.fit.http.protocol.support;

import com.huawei.fit.http.protocol.ConfigurableStatusLine;
import com.huawei.fit.http.protocol.HttpVersion;
import com.huawei.fitframework.inspection.Validation;
import com.huawei.fitframework.util.StringUtils;

/**
 * 表示 {@link ConfigurableStatusLine} 的默认实现。
 *
 * @author 季聿阶
 * @since 2022-11-27
 */
public class DefaultStatusLine implements ConfigurableStatusLine {
    private final HttpVersion httpVersion;
    private int statusCode;
    private String reasonPhrase;

    public DefaultStatusLine(HttpVersion httpVersion, int statusCode, String reasonPhrase) {
        this.httpVersion = Validation.notNull(httpVersion, "The http version cannot be null.");
        this.statusCode = statusCode;
        this.reasonPhrase = reasonPhrase;
    }

    @Override
    public void statusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    @Override
    public void reasonPhrase(String reasonPhrase) {
        if (StringUtils.isNotBlank(reasonPhrase)) {
            this.reasonPhrase = reasonPhrase;
        }
    }

    @Override
    public int statusCode() {
        return this.statusCode;
    }

    @Override
    public String reasonPhrase() {
        return this.reasonPhrase;
    }

    @Override
    public HttpVersion httpVersion() {
        return this.httpVersion;
    }
}
