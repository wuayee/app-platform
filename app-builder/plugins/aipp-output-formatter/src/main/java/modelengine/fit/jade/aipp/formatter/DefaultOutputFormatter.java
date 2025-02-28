/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.formatter;

import modelengine.fit.jade.aipp.formatter.constant.Constant;
import modelengine.fit.jade.aipp.formatter.constant.OutputFormatterOrder;
import modelengine.fit.jade.aipp.formatter.message.DefaultOutputMessage;
import modelengine.fit.jade.aipp.formatter.message.item.DefaultMessageItem;
import modelengine.fit.jade.aipp.formatter.util.SerializerUtils;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.annotation.Order;
import modelengine.fitframework.inspection.Nonnull;
import modelengine.fitframework.serialization.ObjectSerializer;
import modelengine.fitframework.util.ObjectUtils;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 默认格式化器。
 *
 * @author 刘信宏
 * @since 2024-11-21
 */
@Order(OutputFormatterOrder.DEFAULT)
@Component
public class DefaultOutputFormatter implements OutputFormatter {
    private static final String DEFAULT_SEPARATOR = "\n";

    private final ObjectSerializer serializer;

    DefaultOutputFormatter(@Fit(alias = "json") ObjectSerializer serializer) {
        this.serializer = serializer;
    }

    @Override
    @Nonnull
    public String name() {
        return Constant.DEFAULT;
    }

    @Override
    public Optional<OutputMessage> format(Object data) {
        if (!this.match(data)) {
            return Optional.empty();
        }
        return Optional.of(DefaultOutputMessage.from(new DefaultMessageItem(this.formatMessageData(data))));
    }

    private boolean match(Object data) {
        if (data == null) {
            return false;
        }
        if (data instanceof Map) {
            return !ObjectUtils.<Map<String, Object>>cast(data).values().stream().allMatch(Objects::isNull);
        }
        return true;
    }

    private String formatMessageData(Object data) {
        if (data instanceof Map) {
            return ObjectUtils.<Map<String, Object>>cast(data)
                    .values()
                    .stream()
                    .filter(Objects::nonNull)
                    .map(input -> SerializerUtils.serialize(this.serializer, input))
                    .collect(Collectors.joining(DEFAULT_SEPARATOR));
        }
        return SerializerUtils.serialize(this.serializer, data);
    }
}
