/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.common.code;

/**
 * 表示通用返回码的枚举。
 *
 * @author 易文渊
 * @since 2024-07-18
 */
public enum CommonRetCode implements RetCode {
    SUCCESS(0, "Success"),
    BAD_REQUEST(130000400, "Request parameter error, please contact administrator."),
    INTERNAL_ERROR(130000500, "Unknown error");

    private final int code;
    private final String msg;

    CommonRetCode(int code, String msg) {
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