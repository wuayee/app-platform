/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.common.model;

import lombok.Getter;

/**
 * 全局返回体基类。
 *
 * @author 陈镕希
 * @since 2023-06-27
 */
@Getter
public class Response<T> {
    private final T data;

    /**
     * 返回基类默认构造器。
     *
     * @param data 返回的数据。
     */
    public Response(T data) {
        this.data = data;
    }
}
