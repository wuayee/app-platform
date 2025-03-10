/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.schema.exception;

import modelengine.fitframework.exception.FitException;

/**
 * 表示通过 Json 解析数据约束的相关异常。
 *
 * @author 兰宇晨
 * @since 2024-08-10
 */
public class JsonSchemaInvalidException extends FitException {
    public JsonSchemaInvalidException(String message) {
        super(message);
    }
}