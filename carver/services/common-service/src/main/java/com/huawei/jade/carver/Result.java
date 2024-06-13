/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.carver;

/**
 * 表示结果对象的包装类。
 *
 * @author 李金绪 l00878072
 * @since 2024/5/10
 */
public class Result<T> {
    private final T data;
    private final int code;
    private final int total;

    /**
     * 通过数据对象和状态码来初始化 {@link Result} 的新实例。
     *
     * @param data 表示数据对象的 {@link T}。
     * @param code 表示状态码的 {@code int}。
     * @param total 表示状态码的 {@code int}。
     */
    public Result(T data, int code, int total) {
        this.data = data;
        this.code = code;
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
     * 获取数据的数量。
     *
     * @return 表示数据数量的 {@code int}。
     */
    public int getTotal() {
        return this.total;
    }

    /**
     * 创建一个数据对象的包装类。
     *
     * @param data 表示数据对象的 {@link T}。
     * @param code 表示状态码的 {@code int}。
     * @param total 表示状态码的 {@code int}。
     * @param <T> 表示数据对象的类型的 {@link T}。
     * @return 表示创建出来的数据对象的包装类的 {@link Result}{@code <}{@link T}{@code >}。
     */
    public static <T> Result<T> create(T data, int code, int total) {
        return new Result<>(data, code, total);
    }

    /**
     * 根据页数和每页大小来计算偏移量。
     *
     * @param pageNum 表示页数的 {@code int}。
     * @param pageSize 表示每页大小的 {@code int}。
     * @return 表示计算出来的偏移量的 {@code int}。
     */
    public static int calculateOffset(int pageNum, int pageSize) {
        return pageNum < 0 || pageSize < 0 ? 0 : (pageNum - 1) * pageSize;
    }
}