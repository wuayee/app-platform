/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.data.repository.entity;

/**
 * 表示元数据的类型。
 *
 * @author 邬涨财
 * @since 2024-01-23
 */
public enum MetadataType {
    STRING("str"),
    BYTES("bytes");

    private final String code;

    MetadataType(String code) {
        this.code = code;
    }

    /**
     * 获取元数据的类型名。
     *
     * @return 表示获取到的元数据的类型名的 {@link String}。
     */
    public String code() {
        return this.code;
    }
}
