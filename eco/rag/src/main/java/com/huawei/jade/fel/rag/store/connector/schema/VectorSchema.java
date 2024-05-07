/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.rag.store.connector.schema;

import com.huawei.fitframework.log.Logger;

import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

/**
 * 向量数据库的表结构。
 *
 * @since 2024-05-07
 */
@Getter
public class VectorSchema {
    private static final Logger log = Logger.get(VectorSchema.class);

    private final List<VectorField> fields;

    /**
     * 构建 {@link VectorSchema} 的实例。
     */
    public VectorSchema() {
        this.fields = new ArrayList<>();
    }

    /**
     * 添加一个字段。
     *
     * @param name 表示字段名。
     * @param dataType 表示字段类型。
     * @param params 表示其他属性。
     */
    public void addField(String name, VectorFieldDataType dataType, List<Object> params) {
        Map<String, Object> paramsMap = new HashMap<>();

        IntStream.range(0, params.size() / 2)
                .forEach(i -> {
                    if (params.get(i * 2) instanceof String) {
                        paramsMap.put((String) params.get(i * 2), params.get(i * 2 + 1));
                    }
                });
        VectorField field = new VectorField(name, dataType, paramsMap);
        fields.add(field);
    }
}