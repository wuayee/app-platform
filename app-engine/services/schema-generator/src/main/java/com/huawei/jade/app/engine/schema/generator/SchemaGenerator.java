/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.schema.generator;

import modelengine.fitframework.annotation.Genericable;

/**
 * 生成数据约束接口。
 *
 * @author 兰宇晨
 * @since 2024-08-07
 */
public interface SchemaGenerator {
    /**
     * 根据 json 生成数据约束。
     *
     * @param json 表示用于生成评估数据约束的 {@link String}。
     * @return 表示 json 对应数据约束的 {@link String}。
     * @throws com.huawei.jade.app.engine.schema.exception.JsonInvalidException 当 {@code json} 为无效数据时。
     */
    @Genericable(id = "com.huawei.jade.app.engine.schema.generator")
    String generateSchema(String json);
}
