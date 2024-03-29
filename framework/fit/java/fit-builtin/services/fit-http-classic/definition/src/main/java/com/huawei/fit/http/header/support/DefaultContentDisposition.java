/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 */

package com.huawei.fit.http.header.support;

import static com.huawei.fitframework.inspection.Validation.notNull;

import com.huawei.fit.http.header.ContentDisposition;
import com.huawei.fit.http.header.HeaderValue;

import java.util.Optional;

/**
 * 表示 {@link ContentDisposition} 的默认实现。
 *
 * @author 季聿阶 j00559309
 * @since 2022-09-04
 */
public class DefaultContentDisposition extends DefaultHeaderValue implements ContentDisposition {
    private static final String NAME = "name";
    private static final String FILENAME = "filename";
    private static final String FILENAME_STAR = "filename*";

    public DefaultContentDisposition(HeaderValue headerValue) {
        super(notNull(headerValue, "The header value cannot be null.").value(), headerValue.parameters());
    }

    @Override
    public Optional<String> name() {
        return this.parameters().get(NAME);
    }

    @Override
    public Optional<String> fileName() {
        return this.parameters().get(FILENAME);
    }

    @Override
    public Optional<String> fileNameStar() {
        return this.parameters().get(FILENAME_STAR);
    }
}
