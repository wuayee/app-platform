/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.util;

import com.huawei.fit.jober.aipp.common.exception.AippJsonDecodeException;
import com.huawei.fit.jober.aipp.common.exception.AippJsonEncodeException;
import com.huawei.fit.jober.aipp.init.serialization.custom.LocalDateTimeDeserializer;
import com.huawei.fit.jober.aipp.init.serialization.custom.LocalDateTimeSerializer;
import com.huawei.fitframework.util.StringUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public interface JsonUtils {
    static Map<String, Object> parseObject(String jsonString) {
        try {
            return new ObjectMapper().readValue(jsonString, new TypeReference<Map<String, Object>>() {});
        } catch (JsonProcessingException e) {
            throw new AippJsonDecodeException(e.getMessage());
        }
    }

    static <T> T parseObject(String jsonString, Class<T> clazz) {
        try {
            return new ObjectMapper().readValue(jsonString, clazz);
        } catch (JsonProcessingException e) {
            throw new AippJsonDecodeException(e.getMessage());
        }
    }

    static <T> List<T> parseArray(String jsonString, Class<T[]> clazz) {
        try {
            return Arrays.asList(new ObjectMapper().readValue(jsonString, clazz));
        } catch (JsonProcessingException e) {
            throw new AippJsonDecodeException(e.getMessage());
        }
    }

    static String toJsonString(Object object) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            SimpleModule module = new SimpleModule();
            module.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(StringUtils.EMPTY));
            module.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(StringUtils.EMPTY));
            mapper.registerModule(module);
            return mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new AippJsonEncodeException(e.getMessage());
        }
    }

    static boolean isValidJson(String jsonLikeString) {
        boolean valid;
        try {
            TreeNode node =
                    new ObjectMapper().enable(DeserializationFeature.FAIL_ON_TRAILING_TOKENS).readTree(jsonLikeString);
            valid = Objects.nonNull(node);
        } catch (JsonProcessingException e) {
            valid = false;
        }
        return valid;
    }
}
