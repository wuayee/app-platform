/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.model.openai.entity.chat.message.tool;

import lombok.Data;

import java.util.Map;

/**
 * 模型所使用的工具，用于 OpenAI 请求中 tools 字段。
 *
 * @author 易文渊
 * @author 张庭怿
 * @since 2024-4-30
 */
@Data
public class OpenAiTool {
    private String type;

    private Function function;

    /**
     * 工具所使用的方法，用于序列化为 JSON 中的 function 字段。
     */
    @Data
    public static class Function {
        private String name;

        private String description;

        private Map<String, Object> parameters;
    }
}
