/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.code.code;

import modelengine.jade.common.code.RetCode;

/**
 * Aipp 代码节点返回码枚举，返回码最大值为 256。
 *
 * @author 何嘉斌
 * @since 2024-10-18
 */
public enum CodeExecuteRetCode implements RetCode {
    /**
     * 表示代码节点内部抛出异常。
     */
    CODE_EXECUTE_ERROR(131001001, "Code node execution failed, failure information: {0}."),

    /**
     * 表示代码节点因超时多次而被限制抛出的异常。
     */
    CODE_EXECUTE_RESTRICTION(131001002,
            "The code execution has been restricted. Please carefully check the code for any potential issues.");
    private final int code;
    private final String msg;

    CodeExecuteRetCode(int code, String msg) {
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