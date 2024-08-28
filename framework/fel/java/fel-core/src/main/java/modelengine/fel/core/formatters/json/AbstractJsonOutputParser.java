/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fel.core.formatters.json;

import modelengine.fitframework.inspection.Validation;
import modelengine.fitframework.json.schema.JsonSchema;
import modelengine.fitframework.json.schema.JsonSchemaManager;
import modelengine.fitframework.json.schema.support.DefaultJsonSchemaManager;
import modelengine.fitframework.serialization.ObjectSerializer;
import modelengine.fitframework.util.ObjectUtils;

import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;

/**
 * 表示 {@link JsonOutputParser} 的抽象实现。
 *
 * @param <O> 表示输出对象类型。
 * @author 易文渊
 * @since 2024-04-28
 */
public abstract class AbstractJsonOutputParser<O> implements JsonOutputParser<O> {
    /**
     * 表示 {@link JsonSchema} 序列化形式的 {@link String}。
     */
    protected final String jsonSchema;
    private final ObjectSerializer serializer;
    private final Type type;

    /**
     * 创建 {@link AbstractJsonOutputParser} 的实例。
     *
     * @param serializer 表示对象序列化器的 {@link ObjectSerializer}。
     * @param type 表示输出类型 {@link O} 的 {@link Type}。
     * @param schemaManager 表示 jsonSchema 管理器的 {@link JsonSchemaManager}。
     */
    protected AbstractJsonOutputParser(ObjectSerializer serializer, Type type, JsonSchemaManager schemaManager) {
        this.serializer = Validation.notNull(serializer, "The serializer cannot be null.");
        this.type = Validation.notNull(type, "The type cannot be null.");
        JsonSchemaManager usedSchemaManager = ObjectUtils.getIfNull(schemaManager, DefaultJsonSchemaManager::new);
        this.jsonSchema = new String(serializer.serialize(usedSchemaManager.createSchema(type).toJsonObject(),
                StandardCharsets.UTF_8), StandardCharsets.UTF_8);
    }

    @Override
    public O parse(String input) {
        return this.serializer.deserialize(input, this.type);
    }
}