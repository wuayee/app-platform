/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.schema.exception;

import static com.huawei.jade.app.engine.schema.code.SchemaValidatorRetCode.VALIDATE_CONTENT_INVALID_ERROR;

/**
 * 表示数据校验异常。
 *
 * @author 兰宇晨
 * @since 2024-07-29
 */
public class ContentInvalidException extends SchemaValidateException {
    /**
     * 数据校验异常构造函数。
     *
     * @param content 表示非法数据的 {@link String}。
     * @param schema 表示校验用 Schema 的 {@link String}。
     * @param error 表示异常信息参数的 {@link String}。
     */
    public ContentInvalidException(String content, String schema, String error) {
        super(VALIDATE_CONTENT_INVALID_ERROR, content, schema, error);
    }

    /**
     * 数据校验异常构造函数。
     *
     * @param content 表示非法数据的 {@link String}。
     * @param schema 表示校验用 Schema 的 {@link String}。
     * @param error 表示异常信息参数的 {@link String}。
     * @param cause 表示异常原因的 {@link Throwable}。
     */
    public ContentInvalidException(String content, String schema, String error, Throwable cause) {
        super(VALIDATE_CONTENT_INVALID_ERROR, cause, content, schema, error);
    }
}
