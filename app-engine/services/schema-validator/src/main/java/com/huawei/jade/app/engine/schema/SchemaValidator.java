/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.schema;

import com.huawei.fitframework.annotation.Genericable;

import java.util.List;

/**
 * 根据 Schema 校验数据接口。
 *
 * @author 兰宇晨
 * @since 2024-07-29
 */
public interface SchemaValidator {
    /**
     * 根据 Schema 校验评估内容。
     *
     * @param schema 表示用于校验评估数据的 Schema {@link String}。
     * @param contents 表示评估内容集合的 {@link List}{@code <}{@link String}{@code >}。
     * @throws com.huawei.jade.app.engine.schema.exception.SchemaValidateException 当{@code schema} 为无效数据时。
     * @throws com.huawei.jade.app.engine.schema.exception.ContentInvalidException 当{@code contents} 中含有无效数据时。
     */
    @Genericable(id = "com.huawei.jade.app.engine.schema")
    void validate(String schema, List<String> contents);
}
