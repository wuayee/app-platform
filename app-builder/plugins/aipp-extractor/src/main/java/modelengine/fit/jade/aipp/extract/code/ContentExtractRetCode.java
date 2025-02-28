/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.extract.code;

import modelengine.jade.common.code.RetCode;

/**
 * Aipp 信息提取返回码枚举，返回码最大值为 256。
 *
 * @author 何嘉斌
 * @since 2024-10-28
 */
public enum ContentExtractRetCode implements RetCode {
    /**
     * 表示信息提取节点内部抛出异常。
     */
    DESERIALIZE_ERROR(131002001, "Failed to deserialize json schema by Jackson. Json schema: {0}."),

    /**
     * 表示模型输出 Toolcall 数量不为 1。
     */
    TOOLCALL_SIZE_ERROR(131002002, "Invalid model toolcalls size: {0}, expected: {1}."),

    /**
     * 表示模型无输出消息。
     */
    MODEL_RESPONSE_ERROR(131002003, "Model respond no message.");

    private final int code;
    private final String msg;

    ContentExtractRetCode(int code, String msg) {
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