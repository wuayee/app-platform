/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.http.call.enums;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import modelengine.fit.http.HttpMessage;
import modelengine.fit.http.entity.Entity;
import modelengine.fit.jade.aipp.http.call.HttpBody;
import modelengine.fit.jade.aipp.http.call.command.HttpCallCommand;
import modelengine.fitframework.model.MultiValueMap;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.StringUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

/**
 * http请求体类型。
 *
 * @author 张越
 * @since 2024-11-22
 */
@Getter
public enum HttpBodyType {
    X_WWW_FORM_URLENCODED("x-www-form-urlencoded", "application/x-www-form-urlencoded", new FormURLEncodedConverter()),
    JSON("json", "application/json", new JSONConverter()),
    RAW_TEXT("text", "application/text", new TextConverter());

    private final String key;
    private final String contentType;
    private final Function<ConverterEntity, Entity> converter;

    HttpBodyType(String key, String contentType, Function<ConverterEntity, Entity> converter) {
        this.key = key;
        this.contentType = contentType;
        this.converter = converter;
    }

    /**
     * 将key转换为 {@link HttpBodyType} .
     *
     * @param key 键值.
     * @return {@link Optional}{@code <}{@link HttpBodyType}{@code >} 对象.
     */
    public static Optional<HttpBodyType> fromKey(String key) {
        return Arrays.stream(HttpBodyType.values()).filter(type -> StringUtils.equals(type.key, key)).findFirst();
    }

    /**
     * form data 转换器。
     *
     * @author 张越
     * @since 2024-11-22
     */
    private static class FormURLEncodedConverter implements Function<ConverterEntity, Entity> {
        @Override
        public Entity apply(ConverterEntity entity) {
            Object body = entity.getHttpBody().getData();
            if (body == null) {
                return null;
            }
            Map<String, String> map = ObjectUtils.cast(body);
            MultiValueMap<String, String> multiValueMap = MultiValueMap.create();
            Optional.of(map)
                    .orElseGet(HashMap::new)
                    .forEach((k, v) -> multiValueMap.add(entity.getCommand().render(k), entity.getCommand().render(v)));
            return Entity.createMultiValue(entity.getMessage(), multiValueMap);
        }
    }

    /**
     * json data 转换器。
     *
     * @author 张越
     * @since 2024-11-22
     */
    private static class JSONConverter extends BaseTemplateConverter {
        @Override
        protected Entity convert(ConverterEntity entity, String template) {
            String jsonBodyString = entity.getCommand().render(template);
            return Entity.createObject(entity.getMessage(), com.alibaba.fastjson.JSON.parseObject(jsonBodyString));
        }
    }

    /**
     * text data 转换器。
     *
     * @author 张越
     * @since 2024-11-22
     */
    private static class TextConverter extends BaseTemplateConverter {
        @Override
        protected Entity convert(ConverterEntity entity, String template) {
            return Entity.createText(entity.getMessage(), entity.getCommand().render(template));
        }
    }

    /**
     * 模板转换器基类。
     *
     * @author 张越
     * @since 2024-11-25
     */
    private abstract static class BaseTemplateConverter implements Function<ConverterEntity, Entity> {
        @Override
        public Entity apply(ConverterEntity entity) {
            Object body = entity.getHttpBody().getData();
            if (body == null) {
                return null;
            }
            String template = ObjectUtils.cast(body);
            return this.convert(entity, template);
        }

        /**
         * 将模板转换为 {@link Entity} 对象.
         *
         * @param entity {@link ConverterEntity} 对象.
         * @param template 模板字符串.
         * @return {@link Entity} 对象.
         */
        protected abstract Entity convert(ConverterEntity entity, String template);
    }

    /**
     * 转换器参数。
     *
     * @author 张越
     * @since 2024-12-15
     */
    @Data
    @AllArgsConstructor
    public static class ConverterEntity {
        private HttpMessage message;
        private HttpBody httpBody;
        private HttpCallCommand command;
    }
}
