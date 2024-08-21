/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 */

package com.huawei.fit.http.header.support;

import static modelengine.fitframework.inspection.Validation.notNull;

import com.huawei.fit.http.header.ContentType;
import com.huawei.fit.http.header.HeaderValue;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.util.ExceptionUtils;

import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;
import java.util.Optional;

/**
 * 表示 {@link ContentType} 的默认实现。
 *
 * @author 季聿阶
 * @since 2022-09-04
 */
public class DefaultContentType extends DefaultHeaderValue implements ContentType {
    /** 表示字符集参数的 {@link String}。 */
    public static final String CHARSET = "charset";

    private static final Logger log = Logger.get(DefaultContentType.class);
    private static final String BOUNDARY = "boundary";

    public DefaultContentType(HeaderValue headerValue) {
        super(notNull(headerValue, "The header value cannot be null.").value(), headerValue.parameters());
    }

    @Override
    public Optional<Charset> charset() {
        return this.parameters().get(CHARSET).map(this::forName);
    }

    private Charset forName(String contentCharset) {
        try {
            return Charset.forName(contentCharset);
        } catch (IllegalCharsetNameException | UnsupportedCharsetException e) {
            log.warn("Illegal content charset. [charset={}, reason={}]", contentCharset, ExceptionUtils.getReason(e));
            return null;
        }
    }

    @Override
    public Optional<String> boundary() {
        return this.parameters().get(BOUNDARY);
    }
}
