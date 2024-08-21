/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.common.filter;

import modelengine.fitframework.annotation.Property;
import com.huawei.jade.common.code.CommonRetCode;

/**
 * 表示统一 http 返回结果的实体类。
 *
 * @param <T> 表示数据对象的泛型。
 * @author 易文渊
 * @since 2024-07-18
 */
public class HttpResult<T> {
    @Property(description = "状态码", example = "0")
    private int code;

    @Property(description = "信息", example = "success")
    private String msg;

    @Property(description = "响应数据")
    private T data;

    /**
     * 表示默认构造函数
     */
    public HttpResult() {}

    /**
     * 通过数据对象和状态码来初始化 {@link HttpResult} 的新实例。
     *
     * @param code 表示状态码的 {@code int}。
     * @param msg 表示状态消息的 {@link String}。
     * @param data 表示数据对象的 {@link T}。
     */
    private HttpResult(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    /**
     * 创建一个成功数据对象的包装类。
     *
     * @return 表示创建出来的成功数据对象的包装类的 {@link HttpResult}{@code <}{@link Void}{@code >}。
     */
    public static HttpResult<Void> ok() {
        return ok(null);
    }

    /**
     * 创建一个成功数据对象的包装类。
     *
     * @param data 表示数据对象的 {@link T}。
     * @param <T> 表示数据对象的类型的 {@link T}。
     * @return 表示创建出来的成功数据对象的包装类的 {@link HttpResult}{@code <}{@link T}{@code >}。
     */
    public static <T> HttpResult<T> ok(T data) {
        return new HttpResult<>(CommonRetCode.SUCCESS.getCode(), CommonRetCode.SUCCESS.getMsg(), data);
    }

    /**
     * 创建一个错误数据对象的包装类。
     *
     * @param errorCode 表示错误码枚举类型的 {@link CommonRetCode}。
     * @return 表示创建出来的成功数据对象的包装类的 {@link HttpResult}{@code <}{@link Void}{@code >}。
     */
    public static HttpResult<Void> error(CommonRetCode errorCode) {
        return error(errorCode.getCode(), errorCode.getMsg());
    }

    /**
     * 创建一个错误数据对象的包装类。
     *
     * @param code 表示状态码的 {@code int}。
     * @param msg 表示状态消息的 {@link String}。
     * @return 表示创建出来的成功数据对象的包装类的 {@link HttpResult}{@code <}{@link Void}{@code >}。
     */
    public static HttpResult<Void> error(int code, String msg) {
        return new HttpResult<>(code, msg, null);
    }

    /**
     * 获取状态码。
     *
     * @return 表示状态码的 {@code int}。
     */
    public int getCode() {
        return this.code;
    }

    /**
     * 设置状态码。
     *
     * @param code 表示状态码的 {@code int}。
     */
    public void setCode(int code) {
        this.code = code;
    }

    /**
     * 获取状态消息。
     *
     * @return 表示状态消息的 {@link String}。
     */
    public String getMsg() {
        return this.msg;
    }

    /**
     * 设置状态消息。
     *
     * @param msg 表示状态消息的 {@link String}。
     */
    public void setMsg(String msg) {
        this.msg = msg;
    }

    /**
     * 获取数据对象。
     *
     * @return 表示数据对象的 {@link T}。
     */
    public T getData() {
        return this.data;
    }

    /**
     * 设置数据对象。
     *
     * @param data 表示数据对象的 {@link T}。
     */
    public void setData(T data) {
        this.data = data;
    }
}