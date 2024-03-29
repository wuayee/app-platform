/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fitframework.protocol.jar;

import java.util.zip.ZipEntry;

/**
 * 表示 JAR 中条目的压缩方法。
 *
 * @author 梁济时 l00815032
 * @since 2022-09-16
 */
public enum CompressionMethod {
    /**
     * 表示 JAR 条目未被压缩。
     */
    NONE(ZipEntry.STORED, "Store"),

    /**
     * 表示使用 Deflate 算法进行压缩。
     */
    DEFLATED(ZipEntry.DEFLATED, "Deflate");

    private final Integer id;
    private final String code;

    CompressionMethod(int id, String code) {
        this.id = id;
        this.code = code;
    }

    public int id() {
        return this.id;
    }

    public String code() {
        return this.code;
    }

    public static CompressionMethod fromId(int id) {
        for (CompressionMethod method : values()) {
            if (method.id() == id) {
                return method;
            }
        }
        return null;
    }
}
