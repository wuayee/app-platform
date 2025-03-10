/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.common.vo;

import modelengine.fitframework.annotation.Property;
import modelengine.jade.common.code.CommonRetCode;
import modelengine.jade.common.code.RetCode;

import java.beans.ConstructorProperties;

/**
 * 表示统一返回结果的实体类。
 *
 * @param <T> 表示数据对象的泛型。
 * @author 易文渊
 * @since 2024-07-18
 */
public class Result<T> {
    @Property(description = "状态码", example = "0")
    private int code;

    @Property(description = "信息", example = "success")
    private String msg;

    @Property(description = "响应数据")
    private T data;

    /**
     * 通过数据对象和状态码来初始化 {@link Result} 的新实例。
     *
     * @param code 表示状态码的 {@code int}。
     * @param msg 表示状态消息的 {@link String}。
     * @param data 表示数据对象的 {@link T}。
     */
    @ConstructorProperties({"code", "msg", "data"})
    private Result(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    /**
     * 创建一个成功数据对象的包装类。
     *
     * @return 表示创建出来的成功数据对象的包装类的 {@link Result}{@code <}{@link Void}{@code >}。
     */
    public static Result<Void> ok() {
        return ok(null);
    }

    /**
     * 创建一个成功数据对象的包装类。
     *
     * @param data 表示数据对象的 {@link T}。
     * @param <T> 表示数据对象的类型的 {@link T}。
     * @return 表示创建出来的成功数据对象的包装类的 {@link Result}{@code <}{@link T}{@code >}。
     */
    public static <T> Result<T> ok(T data) {
        return new Result<>(CommonRetCode.SUCCESS.getCode(), CommonRetCode.SUCCESS.getMsg(), data);
    }

    /**
     * 创建一个错误数据对象的包装类。
     *
     * @param retCode 表示错误码枚举类型的 {@link RetCode}。
     * @return 表示创建出来的成功数据对象的包装类的 {@link Result}{@code <}{@link Void}{@code >}。
     */
    public static Result<Void> error(RetCode retCode) {
        return error(retCode.getCode(), retCode.getMsg());
    }

    /**
     * 创建一个错误数据对象的包装类。
     *
     * @param code 表示状态码的 {@code int}。
     * @param msg 表示状态消息的 {@link String}。
     * @return 表示创建出来的成功数据对象的包装类的 {@link Result}{@code <}{@link Void}{@code >}。
     */
    public static Result<Void> error(int code, String msg) {
        return new Result<>(code, msg, null);
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