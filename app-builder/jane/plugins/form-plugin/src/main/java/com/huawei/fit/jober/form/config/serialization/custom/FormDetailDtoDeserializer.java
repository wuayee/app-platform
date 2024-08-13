/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.form.config.serialization.custom;

import com.huawei.fit.jober.form.dto.FormDetailDto;
import com.huawei.fit.jober.form.dto.FormDto;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;

/**
 * {@link FormDetailDto} 类型的自定义反序列化器
 *
 * @author 熊以可
 * @since 2024-02-27
 */
public class FormDetailDtoDeserializer extends JsonDeserializer<FormDetailDto> {
    @Override
    public FormDetailDto deserialize(JsonParser jsonParser, DeserializationContext context) throws IOException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        JsonNode dataObjectNode = node.get("data");
        FormDto meta = jsonParser.getCodec().treeToValue(node.get("meta"), FormDto.class);
        String data = dataObjectNode.isValueNode() ? dataObjectNode.asText() : dataObjectNode.toString();
        return new FormDetailDto(meta, data);
    }
}
