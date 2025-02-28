/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.common.schema.validator;

import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.jade.schema.SchemaValidator;
import modelengine.jade.schema.exception.JsonContentInvalidException;
import modelengine.jade.schema.exception.JsonSchemaInvalidException;

import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.annotation.Fitable;
import modelengine.fitframework.serialization.ObjectSerializer;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.StringUtils;

import org.everit.json.schema.Schema;
import org.everit.json.schema.ValidationException;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

/**
 * 表示 {@link SchemaValidator} 的默认实现。
 *
 * @author 兰宇晨
 * @since 2024-07-29
 */
@Component
public class SchemaValidatorImpl implements SchemaValidator {
    private final ObjectSerializer serializer;

    public SchemaValidatorImpl(@Fit(alias = "json") ObjectSerializer serializer) {
        this.serializer = serializer;
    }

    @Override
    @Fitable(id = "default")
    public void validate(Object schema, Object content) {
        notNull(schema, () -> new JsonSchemaInvalidException("The schema cannot be null."));
        notNull(content, () -> new JsonContentInvalidException("Content cannot be null."));
        validateJson(schema, content);
    }

    @Override
    @Fitable(id = "default")
    public void validate(Object schema, List<?> contents) {
        notNull(schema, () -> new JsonSchemaInvalidException("The schema cannot be null."));
        notNull(contents, () -> new JsonContentInvalidException("Contents cannot be null."));
        for (Object content : contents) {
            validateJson(schema, content);
        }
    }

    private void validateJson(Object schema, Object target) {
        try {
            Schema schemaValidator = loadSchema(schema);
            schemaValidator.validate(this.loadJson(target));
        } catch (JSONException e) {
            throw new JsonContentInvalidException(StringUtils.format("The content '{0}' is invalid.", target));
        } catch (ValidationException e) {
            throw new JsonContentInvalidException(StringUtils.format(
                    "The content '{0}' is not conform to schema '{1}', reason [{2}].",
                    this.serializer.serialize(target),
                    this.serializer.serialize(schema),
                    e.getMessage()));
        }
    }

    private Schema loadSchema(Object schema) {
        if (!(schema instanceof String) && !(schema instanceof Map)) {
            throw new JsonSchemaInvalidException(StringUtils.format("Invalid Schema Type {0}.",
                    schema.getClass().getName()));
        }
        try {
            JSONObject rawSchema = loadJsonObject(schema);
            return SchemaLoader.load(rawSchema);
        } catch (JSONException e) {
            throw new JsonSchemaInvalidException(StringUtils.format("The schema '{0}' is invalid.",
                    this.serializer.serialize(schema)));
        }
    }

    private JSONObject loadJsonObject(Object jsonObject) {
        if (jsonObject instanceof String) {
            return new JSONObject(ObjectUtils.<String>cast(jsonObject));
        } else if (jsonObject instanceof Map) {
            return new JSONObject(ObjectUtils.<Map<String, Object>>cast(jsonObject));
        } else {
            throw new JsonContentInvalidException(StringUtils.format("The object '{0}' is not valid json.",
                    this.serializer.serialize(jsonObject)));
        }
    }

    private Object loadJson(Object jsonObject) {
        if (jsonObject instanceof List) {
            return new JSONArray(((List<?>) jsonObject).toArray());
        }
        return this.loadJsonObject(jsonObject);
    }
}