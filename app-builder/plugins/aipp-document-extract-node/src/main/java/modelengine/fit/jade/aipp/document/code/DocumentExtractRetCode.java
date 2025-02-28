/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.document.code;

import modelengine.jade.common.code.RetCode;

/**
 * 文档提取的调用错误码。
 *
 * @author 马朝阳
 * @since 2024-12-12
 */
public enum DocumentExtractRetCode implements RetCode {
    /**
     * 提取文档内容失败。
     */
    DOCUMENT_EXTRACT_ERROR(131002004, "Unsupported file type, fileUrl:{0}."),

    /**
     * 多模态文件提取工具不存在。
     */
    MULTI_MODAL_FILE_EXTRACT_TOOL_NOT_FOUND(131002005, "Unable to find multi-modal file extract tool, fileUrl:{0}."),

    /**
     * 错误的文件链接。
     */
    WRONG_FILE_URL(131002006, "Invalid file url, fileUrl:{0}."),

    /**
     * 提取参数不存在。
     */
    EMPTY_EXTRACT_PARAM(1310020067, "Empty extract param, param:{0}.");

    private final int code;
    private final String msg;

    DocumentExtractRetCode(int code, String msg) {
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