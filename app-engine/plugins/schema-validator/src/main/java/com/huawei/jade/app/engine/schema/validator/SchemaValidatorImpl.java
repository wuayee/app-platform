/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.schema.validator;

import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Fitable;
import com.huawei.jade.app.engine.schema.SchemaValidator;
import com.huawei.jade.app.engine.schema.exception.ContentInvalidException;
import com.huawei.jade.app.engine.schema.exception.SchemaInvalidException;

import org.everit.json.schema.Schema;
import org.everit.json.schema.ValidationException;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * 表示 {@link SchemaValidator} 的默认实现。
 *
 * @author 兰宇晨
 * @since 2024-07-29
 */
@Component
public class SchemaValidatorImpl implements SchemaValidator {
    @Override
    @Fitable(id = "com.huawei.jade.app.engine.schema.validator.SchemaValidatorImpl.validate")
    public void validate(String schema, List<String> contents) throws SchemaInvalidException, ContentInvalidException {
        if (schema == null) {
            throw new SchemaInvalidException(null);
        }
        if (contents == null) {
            throw new ContentInvalidException(null, schema, "Contents cannot be null.");
        }
        int index = 0;
        try {
            JSONObject rawSchema = new JSONObject(schema);
            Schema schemaValidator = SchemaLoader.load(rawSchema);
            for (index = 0; index < contents.size(); index++) {
                String content = contents.get(index);
                schemaValidator.validate(new JSONObject(content));
            }
        } catch (JSONException e) {
            throw new SchemaInvalidException(schema, e);
        } catch (ValidationException e) {
            throw new ContentInvalidException(contents.get(index), schema, e.getMessage(), e);
        }
    }
}