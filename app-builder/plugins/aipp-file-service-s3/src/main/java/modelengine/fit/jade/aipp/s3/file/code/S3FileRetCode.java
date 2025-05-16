/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.s3.file.code;

import modelengine.jade.common.code.RetCode;
import modelengine.jade.common.model.ModelInfo;

/**
 * S3 文件上传模块返回码枚举，返回码最大值为 256。
 *
 * @author 兰宇晨
 * @since 2025-01-06
 */
public enum S3FileRetCode implements RetCode, ModelInfo {
    /**
     * 上传 S3 文件失败。
     */
    S3_FILE_UPLOAD_FAILED(131102001, "Upload s3 file failed, file name: {0}."),

    /**
     * S3 文件名非法。
     */
    S3_FILE_NAME_INVALID(131102002, "S3 file name invalid, file name: {0}."),

    /**
     * 下载 S3 文件失败。
     */
    S3_FILE_DOWNLOAD_FAILED(131102003, "Download s3 file failed, file url: {0}.");

    private final int code;
    private final String msg;

    S3FileRetCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    @Override
    public int getCode() {
        return this.code;
    }

    @Override
    public String getMsg() {
        return this.msg;
    }
}
