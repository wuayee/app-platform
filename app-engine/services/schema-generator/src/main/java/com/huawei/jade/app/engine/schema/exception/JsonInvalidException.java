/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.schema.exception;

import static com.huawei.jade.app.engine.schema.code.SchemaGeneratorRetCode.JSON_INVALID_ERROR;

import modelengine.fitframework.exception.FitException;
import modelengine.fitframework.util.StringUtils;

/**
 * 表示通过 Json 解析数据约束的相关异常。
 *
 * @author 兰宇晨
 * @since 2024-08-10
 */
public class JsonInvalidException extends FitException {
    /**
     * 通过 Json 解析数据约束异常构造函数。
     *
     * @param json 表示用于解析数据约束 Json 的 {@link String}。
     * @param errMsg 表示异常信息的 {@link String}。
     */
    public JsonInvalidException(String json, String errMsg) {
        super(JSON_INVALID_ERROR.getCode(), StringUtils.format(JSON_INVALID_ERROR.getMsg(), json, errMsg));
    }

    /**
     * 通过 Json 解析数据约束异常构造函数。
     *
     * @param json 表示用于解析数据约束 Json 的 {@link String}。
     * @param errMsg 表示异常信息的 {@link String}。
     * @param cause 表示异常原因的 {@link Throwable}。
     */
    public JsonInvalidException(String json, String errMsg, Throwable cause) {
        super(JSON_INVALID_ERROR.getCode(), StringUtils.format(JSON_INVALID_ERROR.getMsg(), json, errMsg), cause);
    }
}
