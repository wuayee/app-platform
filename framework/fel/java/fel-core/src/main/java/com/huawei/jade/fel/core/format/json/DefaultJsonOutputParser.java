/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.core.format.json;

import com.huawei.fitframework.json.schema.JsonSchemaManager;
import com.huawei.fitframework.serialization.ObjectSerializer;
import com.huawei.fitframework.util.StringUtils;

import java.lang.reflect.Type;

/**
 * 表示 {@link JsonOutputParser} 的默认实现。
 *
 * @param <O> 表示输出对象类型。
 * @author 易文渊
 * @since 2024-04-28
 */
public class DefaultJsonOutputParser<O> extends AbstractJsonOutputParser<O> {
    /**
     * 创建 {@link DefaultJsonOutputParser} 的实例。
     *
     * @param serializer 表示对象序列化器的 {@link ObjectSerializer}。
     * @param type 表示输出类型 {@link O} 的 {@link Type}。
     * @param schemaManager 表示 jsonSchema 管理器的 {@link JsonSchemaManager}。
     */
    public DefaultJsonOutputParser(ObjectSerializer serializer, Type type, JsonSchemaManager schemaManager) {
        super(serializer, type, schemaManager);
    }

    @Override
    public String instruction() {
        String template = "The output should be formatted as a JSON instance that conforms to the JSON schema below.\n"
                + "Here is the output schema:\n```\n{0}\n```";
        return StringUtils.format(template, super.jsonSchema);
    }
}