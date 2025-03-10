/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.formatter;

import modelengine.jade.schema.SchemaValidator;

import modelengine.fit.jade.aipp.formatter.constant.Constant;
import modelengine.fit.jade.aipp.formatter.constant.OutputFormatterOrder;
import modelengine.fit.jade.aipp.formatter.message.ReferenceOutputMessage;
import modelengine.fit.jade.aipp.formatter.message.item.DefaultMessageItem;
import modelengine.fit.jade.aipp.formatter.message.item.ReferenceMessageItem;
import modelengine.fit.jade.aipp.formatter.util.SerializerUtils;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.annotation.Order;
import modelengine.fitframework.exception.FitException;
import modelengine.fitframework.inspection.Nonnull;
import modelengine.fitframework.inspection.Validation;
import modelengine.fitframework.serialization.ObjectSerializer;
import modelengine.fitframework.util.IoUtils;
import modelengine.fitframework.util.ObjectUtils;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 大模型节点输出报文的格式化器。
 *
 * @author 刘信宏
 * @since 2024-11-21
 */
@Order(OutputFormatterOrder.LLM_OUTPUT)
@Component
public class LlmOutputFormatter implements OutputFormatter {
    private static final String LLM_OUTPUT_SCHEMA = "/llmOutputSchema.json";
    private static final String LLM_OUTPUT_KEY = "llmOutput";
    private static final String REFERENCE_KEY = "reference";

    private final ObjectSerializer serializer;
    private final SchemaValidator validator;
    private final String schema;

    LlmOutputFormatter(@Fit(alias = "json") ObjectSerializer serializer, SchemaValidator validator) throws IOException {
        this.serializer = Validation.notNull(serializer, "The serializer cannot be null.");
        this.validator = Validation.notNull(validator, "The validator cannot be null.");
        this.schema = IoUtils.content(LlmOutputFormatter.class, LLM_OUTPUT_SCHEMA);
    }

    @Override
    @Nonnull
    public String name() {
        return Constant.LLM_OUTPUT;
    }

    @Override
    public Optional<OutputMessage> format(Object data) {
        Validation.notNull(data, "The input data cannot be null.");
        if (!this.match(data)) {
            return Optional.empty();
        }

        Map<String, Object> finalOutput = ObjectUtils.cast(data);
        List<MessageItem> messageItems = finalOutput.values().stream().filter(Objects::nonNull).map(entity -> {
            if (this.validate(entity)) {
                Map<String, Object> llmOutput = ObjectUtils.cast(entity);
                return new ReferenceMessageItem(ObjectUtils.cast(llmOutput.get(LLM_OUTPUT_KEY)),
                        ObjectUtils.cast(llmOutput.get(REFERENCE_KEY)));
            }
            return new DefaultMessageItem(SerializerUtils.serialize(this.serializer, entity));
        }).collect(Collectors.toList());
        return Optional.of(new ReferenceOutputMessage(this.serializer, messageItems));
    }

    private boolean match(Object data) {
        if (!(data instanceof Map)) {
            return false;
        }
        Map<String, Object> finalOutput = ObjectUtils.cast(data);
        return finalOutput.values().stream().anyMatch(this::validate);
    }

    private boolean validate(Object data) {
        try {
            this.validator.validate(this.schema, data);
        } catch (FitException ignore) {
            return false;
        }
        return true;
    }
}
