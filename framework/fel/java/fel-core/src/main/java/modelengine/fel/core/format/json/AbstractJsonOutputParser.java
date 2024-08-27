/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fel.core.format.json;

import static modelengine.fitframework.inspection.Validation.notNull;

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
     * 表示 {@link modelengine.fitframework.json.schema.JsonSchema} 序列化形式的 {@link String}。
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
     * @throws IllegalArgumentException 当 {@code serializer}、{@code schemaManager} 为 {@code null} 时。
     */
    protected AbstractJsonOutputParser(ObjectSerializer serializer, Type type, JsonSchemaManager schemaManager) {
        this.serializer = notNull(serializer, "The serializer cannot be null.");
        this.type = notNull(type, "The type cannot be null.");
        JsonSchemaManager usedSchemaManager = ObjectUtils.getIfNull(schemaManager, DefaultJsonSchemaManager::new);
        this.jsonSchema = new String(serializer.serialize(usedSchemaManager.createSchema(type).toJsonObject(),
                StandardCharsets.UTF_8), StandardCharsets.UTF_8);
    }

    @Override
    public O parse(String input) {
        return this.serializer.deserialize(input, this.type);
    }
}