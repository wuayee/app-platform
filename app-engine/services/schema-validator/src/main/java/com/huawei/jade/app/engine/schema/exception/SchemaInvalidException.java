/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.schema.exception;

import static com.huawei.jade.app.engine.schema.code.SchemaValidatorRetCode.VALIDATE_SCHEMA_INVALID_ERROR;

/**
 * 表示 Schema 校验异常。
 *
 * @author 兰宇晨
 * @since 2024-07-29
 */
public class SchemaInvalidException extends SchemaValidateException {
    /**
     * Schema 校验异常构造函数。
     *
     * @param schema 表示非法 Schema 的 {@link String}。
     */
    public SchemaInvalidException(String schema) {
        super(VALIDATE_SCHEMA_INVALID_ERROR, schema);
    }

    /**
     * Schema 校验异常构造函数。
     *
     * @param schema 表示非法 Schema 的 {@link String}。
     * @param cause 表示异常原因的 {@link Throwable}。
     */
    public SchemaInvalidException(String schema, Throwable cause) {
        super(VALIDATE_SCHEMA_INVALID_ERROR, cause, schema);
    }
}
