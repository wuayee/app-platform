/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jane.common.response;

import com.huawei.fitframework.annotation.Property;

import lombok.Data;

/**
 * HispRsp
 *
 * @author 易文渊
 * @since 2023/9/26
 */
@Data
public class Rsp<T> {
    private static final int OK_CODE = 0;

    private static final String OK_MSG = "success";

    @Property(description = "状态码", example = "0")
    private int code;
    @Property(description = "状态信息", example = "success")
    private String msg;
    @Property(description = "数据")
    private T data;

    private Rsp() {
    }

    /**
     * 静态构造函数
     *
     * @param data 数据
     * @param <T> 数据泛型类型
     * @return HispRsp构造的对象
     */
    public static <T> Rsp<T> ok(T data) {
        Rsp<T> rsp = new Rsp<>();
        rsp.setCode(OK_CODE);
        rsp.setMsg(OK_MSG);
        rsp.setData(data);
        return rsp;
    }

    /**
     * 静态构造函数
     *
     * @param <T> 数据泛型类型
     * @return HispRsp构造对象
     */
    public static <T> Rsp<T> ok() {
        Rsp<T> rsp = new Rsp<>();
        rsp.setCode(OK_CODE);
        rsp.setMsg(OK_MSG);
        rsp.setData(null);
        return rsp;
    }

    /**
     * 静态构造函数
     *
     * @param code 状态码
     * @param msg 消息体
     * @param <T> 数据泛型
     * @return HispRsp构造对象
     */
    public static <T> Rsp<T> err(int code, String msg) {
        Rsp<T> rsp = new Rsp<>();
        rsp.setCode(code);
        rsp.setMsg(msg);
        rsp.setData(null);
        return rsp;
    }

    /**
     * 静态构造函数
     *
     * @param status 状态码
     * @param <T> 数据泛型
     * @return HispRsp构造函数
     */
    public static <T> Rsp<T> err(ErrorCode status) {
        Rsp<T> rsp = new Rsp<>();
        rsp.setCode(status.getErrorCode());
        rsp.setMsg(status.getMessage());
        return rsp;
    }
}