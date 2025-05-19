/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fel.plugin.huggingface.code;

import modelengine.jade.common.code.RetCode;

/**
 * huggingface-plugin 模块返回码枚举。
 *
 * @author 邱晓霞
 * @since 2024-09-18
 */
public enum HuggingfacePluginRetCode implements RetCode {
    /**
     * 任务无可用模型。
     */
    MODEL_NOT_FOUND(130101001, "Model not found. [taskId={0}].");

    private final int code;
    private final String msg;

    HuggingfacePluginRetCode(int code, String msg) {
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
