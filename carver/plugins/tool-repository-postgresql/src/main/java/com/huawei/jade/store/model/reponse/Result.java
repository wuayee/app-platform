/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.store.model.reponse;

import lombok.Getter;

import java.util.Collection;

/**
 * Http 调用的返回结果类。
 *
 * @author 李金绪 l00878072
 * @since 2024/5/10
 */
@Getter
public class Result<T> {
    /**
     * 表示构造结果的输入。
     */
    private final T data;

    /**
     * 表示构造结果的代码。
     */
    private final int code;

    /**
     * 表示构造结果的输入的数量。
     */
    private int total;

    /**
     * 自定义构造方法。
     *
     * @param data 表示输入对象的 {@link T}。
     * @param code 表示 Http 返回代码的 {@link Integer}。
     */
    public Result(T data, Integer code) {
        this.data = data;
        this.code = code;
        this.initializeBasedOnType(data);
    }

    /**
     * 初始化输入的数量。
     *
     * @param data 构造结果的输入 {@link T}。
     */
    private void initializeBasedOnType(T data) {
        if (data == null) {
            this.total = 0;
        } else if (data instanceof Collection) {
            Collection<?> dataList = (Collection<?>) data;
            this.total = dataList.size();
        } else {
            this.total = 1;
        }
    }

    /**
     * 创建返回对象的静态方法。
     *
     * @param data 表示操作返回对象的 {@link T}。
     * @param code 表示构造结果的代码。
     * @param <T> 表示操作返回对象的泛型。
     * @return 返回结果的 {@link Result}{@code <}{@link T}{@code >}。
     */
    public static <T> Result<T> createResult(T data, Integer code) {
        return new Result<>(data, code);
    }
}