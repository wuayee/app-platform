/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fitframework.json.schema.support;

import static com.huawei.fitframework.util.ObjectUtils.cast;
import static com.huawei.fitframework.util.ObjectUtils.nullIf;

import com.huawei.fitframework.annotation.Property;
import com.huawei.fitframework.json.schema.JsonSchema;
import com.huawei.fitframework.serialization.ObjectSerializer;
import com.huawei.fitframework.util.CollectionUtils;
import com.huawei.fitframework.util.MapBuilder;
import com.huawei.fitframework.util.MapUtils;
import com.huawei.fitframework.util.ObjectUtils;
import com.huawei.fitframework.util.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.OptionalInt;
import java.util.Stack;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 表示 {@link JsonSchema} 的对象实现。
 *
 * @author 季聿阶 j00559309
 * @since 2024-03-31
 */
public class ObjectSchema extends AbstractJsonSchema {
    private final List<String> required = new ArrayList<>();
    private final Map<String, JsonSchema> properties = new LinkedHashMap<>();

    ObjectSchema(Type type, ObjectSerializer serializer) {
        super(type, serializer);
    }

    /**
     * 添加指定的格式规范。
     *
     * @param schema 表示待添加的格式规范的 {@link JsonSchema}。
     */
    public void addSchema(JsonSchema schema) {
        this.addSchema(schema, false);
    }

    /**
     * 添加指定的格式规范。
     *
     * @param schema 表示待添加的格式规范的 {@link JsonSchema}。
     * @param required 表示待添加的格式规范是否必须的标志的 {@code boolean}。
     */
    public void addSchema(JsonSchema schema, boolean required) {
        if (schema == null) {
            return;
        }
        this.properties.put(schema.name(), schema);
        if (required) {
            this.required.add(schema.name());
        }
    }

    @Override
    public Map<String, Object> toMap() {
        MapBuilder<String, Object> builder = MapBuilder.<String, Object>get().put("type", "object");
        if (CollectionUtils.isNotEmpty(this.required)) {
            builder.put("required", this.required);
        }
        if (MapUtils.isNotEmpty(this.properties)) {
            builder.put("properties",
                    this.properties.entrySet()
                            .stream()
                            .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().toMap())));
        }
        if (StringUtils.isNotBlank(this.description())) {
            builder.put("description", this.description());
        }
        return builder.build();
    }

    /**
     * 创建一个结构体的格式规范。
     *
     * @param type 表示结构体类型的 {@link Type}。
     * @param referencedPrefix 表示如果存在引用，其引用前缀的 {@link String}。
     * @param referencedSchemas 表示引用数据结果的 {@link Map}{@code <}{@link Type}{@code , }{@link ObjectSchema}{@code >}。
     * @param serializer 表示 Json 序列化其的 {@link ObjectSerializer}。
     * @return 表示创建的结构体的格式规范的 {@link ArraySchema}。
     */
    public static ObjectSchema create(Type type, String referencedPrefix, Map<Type, ObjectSchema> referencedSchemas,
            ObjectSerializer serializer) {
        ObjectSchema schema = new ObjectSchema(type, serializer);
        schema.setProperties(ObjectUtils.nullIf(referencedPrefix, StringUtils.EMPTY),
                ObjectUtils.getIfNull(referencedSchemas, Collections::emptyMap));
        return schema;
    }

    void setProperties(String referencedPrefix, Map<Type, ObjectSchema> referencedSchemas) {
        if (this.type() instanceof ParameterizedType) {
            ParameterizedType parameterizedType = cast(this.type());
            Class<?> rawType = cast(parameterizedType.getRawType());
            Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
            this.generateProperties(rawType, actualTypeArguments, referencedPrefix, referencedSchemas);
            return;
        }
        if (this.type() instanceof Class) {
            Class<?> clazz = (Class<?>) this.type();
            this.generateProperties(clazz, null, referencedPrefix, referencedSchemas);
        }
    }

    private void generateProperties(Class<?> clazz, Type[] actualTypeArguments, String referencedPrefix,
            Map<Type, ObjectSchema> referencedSchemas) {
        TypeVariable<?>[] typeVariables = clazz.getTypeParameters();
        for (Field field : getDeclaredFields(clazz)) {
            Type actualFieldType;
            Type fieldType = field.getGenericType();
            if (fieldType instanceof TypeVariable && actualTypeArguments != null) {
                int index = findTypeVariableIndex(cast(fieldType),
                        typeVariables).orElseThrow(() -> new IllegalStateException(StringUtils.format(
                        "Could not find matching type variable. [field={0}]",
                        field.getName())));
                actualFieldType = actualTypeArguments[index];
            } else {
                actualFieldType = fieldType;
            }
            JsonSchema fieldSchema;
            if (MapUtils.isNotEmpty(referencedSchemas) && referencedSchemas.containsKey(actualFieldType)) {
                fieldSchema = new ReferenceSchema(referencedPrefix,
                        referencedSchemas.get(actualFieldType),
                        this.serializer());
            } else {
                fieldSchema = JsonSchema.create(actualFieldType,
                        nullIf(referencedPrefix, StringUtils.EMPTY),
                        referencedSchemas,
                        this.serializer());
            }
            Property property = field.getDeclaredAnnotation(Property.class);
            String propertyDescription = StringUtils.EMPTY;
            boolean isRequired = false;
            if (property != null) {
                propertyDescription = property.description();
                isRequired = property.required();
            }
            this.addSchema(new DecoratedSchema(field.getName(), propertyDescription, fieldSchema, this.serializer()),
                    isRequired);
        }
    }

    private static List<Field> getDeclaredFields(Class<?> clazz) {
        List<Field> fields = new ArrayList<>();
        Stack<Class<?>> classes = new Stack<>();
        classes.push(clazz);
        Class<?> actualClass = clazz;
        while (actualClass.getSuperclass() != null) {
            actualClass = actualClass.getSuperclass();
            classes.push(actualClass);
        }
        while (!classes.isEmpty()) {
            Class<?> current = classes.pop();
            Stream.of(current.getDeclaredFields()).filter(Objects::nonNull).forEach(fields::add);
        }
        return fields;
    }

    private static OptionalInt findTypeVariableIndex(TypeVariable<?> typeVariable, TypeVariable<?>[] typeVariables) {
        for (int i = 0; i < typeVariables.length; i++) {
            if (Objects.equals(typeVariables[i], typeVariable)) {
                return OptionalInt.of(i);
            }
        }
        return OptionalInt.empty();
    }
}
