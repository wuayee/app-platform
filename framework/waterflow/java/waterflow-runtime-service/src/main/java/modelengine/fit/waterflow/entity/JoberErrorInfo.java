/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.entity;

/**
 * jober的失败信息
 *
 * @author 宋永坦
 * @since 2024/4/29
 */
public class JoberErrorInfo {
    /**
     * 错误信息描述
     */
    private String message;

    /**
     * 错误码
     */
    private int code;

    /**
     * 错误信息中的参数
     */
    private String[] args;

    /**
     * JoberErrorInfo
     */
    public JoberErrorInfo() {
    }

    public JoberErrorInfo(String message) {
        this(message, 0);
    }

    public JoberErrorInfo(String message, int code, String... args) {
        this.message = message;
        this.code = code;
        this.args = args;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String[] getArgs() {
        return args;
    }

    public void setArgs(String[] args) {
        this.args = args;
    }
}
