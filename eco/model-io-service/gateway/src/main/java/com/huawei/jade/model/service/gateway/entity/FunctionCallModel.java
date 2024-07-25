/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.model.service.gateway.entity;

import lombok.Data;

import java.util.Optional;

/**
 * 模型工具调用模板。
 *
 * @author 王浩冉
 * @since 2024-07-15
 */
@Data
public class FunctionCallModel {
    private String fnCallTemplateEn;

    private String fnCallTemplateZh;

    private String fnName;

    private String fnArgs;

    private String fnResult;

    private String fnExit;

    public FunctionCallModel() {
        this.fnName = "✿FUNCTION✿";
        this.fnArgs = "✿ARGS✿";
        this.fnResult = "✿RESULT✿";
        this.fnExit = "✿RETURN✿";

        this.fnCallTemplateZh = String.format(System.lineSeparator() + "# 工具" + System.lineSeparator()
                + System.lineSeparator() + "## 你拥有如下工具：" + System.lineSeparator()
                + System.lineSeparator() + "{tool_descs}"
                + "## 你必须在回复中插入零次、一次或多次以下命令以调用工具："
                + System.lineSeparator() + "%s: 工具名称，必须是[{tool_names}]之一。"
                + System.lineSeparator() + "%s: 工具输入"
                + System.lineSeparator() + "%s: 工具结果，需将图片用![](url)渲染出来。"
                + System.lineSeparator() + "%s: 根据工具结果进行回复",
                this.fnName, this.fnArgs, this.fnResult, this.fnExit);
        this.fnCallTemplateEn = String.format(System.lineSeparator() + "# Tools" + System.lineSeparator()
                + System.lineSeparator() + "## You have access to the following tools:" + System.lineSeparator()
                + System.lineSeparator() + "{tool_descs}"
                + "## When you need to call a tool, please insert the following command in your reply, "
                + "which can be called zero or multiple times according to your needs:"
                + System.lineSeparator() + "%s: The tool to use, should be one of [{tool_names}]."
                + System.lineSeparator() + "%s: Tool input" + System.lineSeparator()
                + "%s: The result returned by the tool. The image needs to be rendered as ![](url)."
                + System.lineSeparator() + "%s: Reply based on the tool result", this.fnName, this.fnArgs,
                this.fnResult, this.fnExit);
    }

    /**
     * 去除特殊字符。
     *
     * @param text 待处理的文本
     * @return 处理后的文本
     */
    public Optional<String> removeSpecialTokens(String text) {
        String content = text.replace(System.lineSeparator(), "");
        content = content.trim();
        content = content.replaceAll("\\{\\s+", "{");
        content = content.replaceAll("\\s+\\}", "}");
        content = content.replaceAll("✿:", "✿");
        boolean isSpecial = false;
        StringBuilder result = new StringBuilder();
        for (char c : content.toCharArray()) {
            if (c == '✿') {
                isSpecial = !isSpecial;
                continue;
            }
            if (!isSpecial) {
                result.append(c);
            }
        }

        return Optional.of(result.toString());
    }
}
