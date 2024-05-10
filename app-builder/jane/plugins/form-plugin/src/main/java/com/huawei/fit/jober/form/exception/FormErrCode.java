package com.huawei.fit.jober.form.exception;

import com.huawei.fit.jane.common.response.ErrorCode;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum FormErrCode implements ErrorCode {
    OK(0, "success"),

    /* ------------ Generic Exception. From 80000000 to 80000999 --------------------- */
    /**
     * 入参不合法
     */
    INPUT_PARAM_IS_INVALID(80000000, "Invalid param: {0}."),

    /**
     * 资源不存在
     */
    NOT_FOUND(80000001, "Cannot find entity: {0}."),

    /**
     * 未知服务器内部错误
     */
    UNKNOWN(80000002, "Server internal error, please contact administrator."),

    /* ------------ Configuration Exception. From 80001000 to 80001999 --------------------- */
    /**
     * form name为空
     */
    FORM_NAME_IS_EMPTY(80001001, "form_name is empty");

    private final int code;
    private final String message;

    /**
     * 获取错误码
     *
     * @return 错误码
     */
    @Override
    public int getErrorCode() {
        return code;
    }

    /**
     * 获取错误提示信息
     *
     * @return 错误信息
     */
    @Override
    public String getMessage() {
        return message;
    }
}
