/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.s3.file.exception;

import modelengine.fit.jade.aipp.s3.file.code.S3FileRetCode;
import modelengine.fitframework.exception.FitException;
import modelengine.fitframework.util.StringUtils;

/**
 * 表示 S3 文件上传异常。
 *
 * @author 兰宇晨
 * @since 2025-01-06
 */
public class S3FileException extends FitException {
    /**
     * S3 文件上传异常构造函数。
     *
     * @param code 表示返回码的 {@link S3FileRetCode}。
     * @param msg 表示异常信息参数的 {@link String}。
     */
    public S3FileException(S3FileRetCode code, String msg) {
        super(code.getCode(), StringUtils.format(code.getMsg(), msg));
    }

    /**
     * S3 文件上传异常构造函数（带异常原因）。
     *
     * @param code 表示返回码的 {@link S3FileRetCode}。
     * @param cause 表示异常原因的 {@link Throwable}。
     * @param msg 表示异常信息参数的 {@link String}。
     */
    public S3FileException(S3FileRetCode code, Throwable cause, String msg) {
        super(code.getCode(), StringUtils.format(code.getMsg(), msg), cause);
    }
}
