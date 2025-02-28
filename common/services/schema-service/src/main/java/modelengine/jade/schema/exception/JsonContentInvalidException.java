/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.schema.exception;

import modelengine.fitframework.exception.FitException;

/**
 * 表示 json 内容非法异常。
 *
 * @author 易文渊
 * @since 2024-09-10
 */
public class JsonContentInvalidException extends FitException {
    public JsonContentInvalidException(String message) {
        super(message);
    }
}