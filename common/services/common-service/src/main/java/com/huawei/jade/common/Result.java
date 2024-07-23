/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.common;

import java.lang.reflect.Array;
import java.util.List;

/**
 * 表示结果对象的包装类。
 *
 * @author 李金绪
 * @author 季聿阶
 * @since 2024-05-10
 */
@Deprecated
public class Result<T> {
    /**
     * 表示成功的状态码的 {@code int}。
     */
    public static final int CODE_OK = 0;

    /**
     * 表示失败的状态码的 {@code -1}。
     */
    public static final int CODE_FAIL = -1;

    /**
     * 表示成功的状态消息的 {@link String}。
     */
    public static final String MSG_SUCCESS = "success";

    /**
     * 表示失败的状态消息的 {@link String}。
     */
    public static final String MSG_FAIL = "fail";

    private final T data;
    private final int code;
    private final String message;
    private final int total;

    /**
     * 通过数据对象和状态码来初始化 {@link Result} 的新实例。
     *
     * @param data 表示数据对象的 {@link T}。
     * @param code 表示状态码的 {@code int}。
     * @param message 表示状态消息的 {@link String}。
     * @param total 表示状态码的 {@code int}。
     */
    private Result(T data, int code, String message, int total) {
        this.data = data;
        this.code = code;
        this.message = message;
        this.total = total;
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
     * 获取状态码。
     *
     * @return 表示状态码的 {@code int}。
     */
    public int getCode() {
        return this.code;
    }

    /**
     * 获取状态消息。
     *
     * @return 表示状态消息的 {@link String}。
     */
    public String getMessage() {
        return this.message;
    }

    /**
     * 获取数据的数量。
     *
     * @return 表示数据数量的 {@code int}。
     */
    public int getTotal() {
        return this.total;
    }

    /**
     * 创建一个成功数据对象的包装类。
     *
     * @param data 表示数据对象的 {@link T}。
     * @param <T> 表示数据对象的类型的 {@link T}。
     * @return 表示创建出来的成功数据对象的包装类的 {@link Result}{@code <}{@link T}{@code >}。
     */
    public static <T> Result<T> ok(T data) {
        return new Result<>(data, CODE_OK, MSG_SUCCESS, calculateTotal(data));
    }

    private static <T> int calculateTotal(T data) {
        if (data == null) {
            return 0;
        } else if (data instanceof List) {
            return ((List<?>) data).size();
        } else if (data.getClass().isArray()) {
            return Array.getLength(data);
        } else {
            return 1;
        }
    }

    /**
     * 创建一个成功数据对象的包装类。
     *
     * @param data 表示数据对象的 {@link T}。
     * @param total 表示数据总数的 {@code int}。
     * @param <T> 表示数据对象的类型的 {@link T}。
     * @return 表示创建出来的成功数据对象的包装类的 {@link Result}{@code <}{@link T}{@code >}。
     */
    public static <T> Result<T> ok(T data, int total) {
        return new Result<>(data, CODE_OK, MSG_SUCCESS, total);
    }

    /**
     * 创建一个成功数据对象的包装类。
     *
     * @param data 表示数据对象的 {@link T}。
     * @param message 表示状态消息的 {@link String}。
     * @param total 表示数据总数的 {@code int}。
     * @param <T> 表示数据对象的类型的 {@link T}。
     * @return 表示创建出来的成功数据对象的包装类的 {@link Result}{@code <}{@link T}{@code >}。
     */
    public static <T> Result<T> ok(T data, String message, int total) {
        return new Result<>(data, CODE_OK, message, total);
    }

    /**
     * 创建一个失败数据对象的包装类。
     *
     * @return 表示创建出来的失败数据对象的包装类的 {@link Result}{@code <?>}。
     */
    public static Result<?> fail() {
        return new Result<>(null, CODE_FAIL, MSG_FAIL, 0);
    }

    /**
     * 创建一个失败数据对象的包装类。
     *
     * @param message 表示状态消息的 {@link String}。
     * @return 表示创建出来的失败数据对象的包装类的 {@link Result}{@code <?>}。
     */
    public static Result<?> fail(String message) {
        return new Result<>(null, CODE_FAIL, message, 0);
    }

    /**
     * 创建一个失败数据对象的包装类。
     *
     * @param code 表示状态码的 {@code int}。
     * @param message 表示状态消息的 {@link String}。
     * @return 表示创建出来的失败数据对象的包装类的 {@link Result}{@code <?>}。
     */
    public static Result<?> fail(int code, String message) {
        return new Result<>(null, code, message, 0);
    }

    /**
     * 根据页数和每页大小来计算偏移量。
     *
     * @param pageNum 表示页数的 {@code int}。
     * @param pageSize 表示每页大小的 {@code int}。
     * @return 表示计算出来的偏移量的 {@code int}。
     */
    public static int calculateOffset(int pageNum, int pageSize) {
        return pageNum <= 0 || pageSize < 0 ? 0 : (pageNum - 1) * pageSize;
    }
}