/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fel.core.formatters.support;

import modelengine.fitframework.inspection.Validation;
import modelengine.fitframework.util.StringUtils;
import modelengine.fel.core.formatters.OutputParser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 表示实现从 markdown 格式字符串解析的  {@link OutputParser}。
 *
 * @param <O> 表示输出对象类型。
 * @author 易文渊
 * @since 2024-05-10
 */
public class MarkdownParser<O> implements OutputParser<O> {
    private final OutputParser<O> outputParser;
    private final Pattern pattern;

    /**
     * 使用输出解析器创建 {@link MarkdownParser} 的实例。
     *
     * @param outputParser 表示对象解析器的 {@link OutputParser}。
     * @param prefix 表示代码类型的 {@link String}。
     */
    public MarkdownParser(OutputParser<O> outputParser, String prefix) {
        Validation.notBlank(prefix, "The prefix cannot be blank.");
        this.outputParser = Validation.notNull(outputParser, "The parser cannot be null.");
        this.pattern = Pattern.compile("```" + prefix + "([\\s\\S]*)");
    }

    @Override
    public String instruction() {
        return outputParser.instruction();
    }

    @Override
    public O parse(String input) {
        Matcher m = this.pattern.matcher(input);
        String json = m.find() ? m.group(1) : input;
        return this.outputParser.parse(StringUtils.trimEnd(json, '`').trim());
    }
}