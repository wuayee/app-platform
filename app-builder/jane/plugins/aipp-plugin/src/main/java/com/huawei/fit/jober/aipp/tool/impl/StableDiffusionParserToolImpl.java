/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.tool.impl;

import com.huawei.fit.jober.aipp.dto.image.StableDiffusionInput;
import com.huawei.fit.jober.aipp.tool.StableDiffusionParserTool;

import modelengine.fel.core.formatters.Parser;
import modelengine.fel.core.formatters.json.JsonOutputParser;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.annotation.Fitable;
import modelengine.fitframework.serialization.ObjectSerializer;

/**
 * StableDiffusion模型json入参解析器实现
 *
 * @author 易文渊
 * @since 2024-06-28
 */
@Component
public class StableDiffusionParserToolImpl implements StableDiffusionParserTool {
    private final Parser<String, StableDiffusionInput> parser;

    public StableDiffusionParserToolImpl(@Fit(alias = "json") ObjectSerializer objectSerializer) {
        this.parser = JsonOutputParser.create(objectSerializer, StableDiffusionInput.class);
    }

    @Override
    @Fitable("default")
    public StableDiffusionInput parse(String input) {
        return parser.parse(input);
    }
}