/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.document.exception;

import modelengine.fit.jade.aipp.document.code.DocumentExtractRetCode;
import modelengine.fitframework.exception.FitException;
import modelengine.fitframework.util.StringUtils;

/**
 * 表示文件提取异常。
 *
 * @author 兰宇晨
 * @since 2025-01-16
 */
public class DocumentExtractException extends FitException {
    /**
     * 应用评估异常构造函数。
     *
     * @param code 表示返回码的 {@link DocumentExtractRetCode}。
     * @param arg 表示异常信息参数的 {@link String}。
     */
    public DocumentExtractException(DocumentExtractRetCode code, String arg) {
        super(code.getCode(), StringUtils.format(code.getMsg(), arg));
    }

    /**
     * 应用评估异常构造函数。
     *
     * @param code 表示返回码的 {@link DocumentExtractRetCode}。
     * @param cause 表示异常原因的 {@link Throwable}。
     * @param arg 表示异常信息参数的 {@link String}。
     */
    public DocumentExtractException(DocumentExtractRetCode code, Throwable cause, String arg) {
        super(code.getCode(), StringUtils.format(code.getMsg(), arg), cause);
    }
}
