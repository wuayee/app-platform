/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.core.examples.support;

import com.huawei.fitframework.inspection.Validation;
import com.huawei.fitframework.util.MapBuilder;
import com.huawei.jade.fel.core.examples.Example;
import com.huawei.jade.fel.core.examples.ExampleSelector;
import com.huawei.jade.fel.core.template.StringTemplate;
import com.huawei.jade.fel.core.template.support.DefaultStringTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

/**
 * 表示 {@link ExampleSelector} 的默认实现。
 *
 * @author 易文渊
 * @since 2024-04-25
 */
public class DefaultExampleSelector implements ExampleSelector {
    private final StringTemplate template;
    private final List<String> input;
    private final List<Example> examples;
    private final BiFunction<List<Example>, String, List<Example>> filter;
    private final String delimiter;

    private DefaultExampleSelector(Builder builder) {
        this.template = builder.template;
        this.input = builder.input;
        this.examples = builder.examples;
        this.filter = builder.filter;
        this.delimiter = builder.delimiter;
    }

    @Override
    public String select(String question) {
        return this.filter.apply(this.examples, question)
                .stream()
                .map(e -> MapBuilder.<String, String>get()
                        .put(this.input.get(0), e.question())
                        .put(this.input.get(1), e.answer())
                        .build())
                .map(this.template::render)
                .collect(Collectors.joining(this.delimiter));
    }

    /**
     * {@link DefaultExampleSelector} 的构建器。
     */
    public static class Builder {
        private final List<Example> examples = new ArrayList<>();
        private StringTemplate template;
        private List<String> input;
        private BiFunction<List<Example>, String, List<Example>> filter = (examples, ignore) -> examples;
        private String delimiter = "\n\n";

        /**
         * 向构建器中设置模板和关键词。
         *
         * @param template 表示使用 mustache 模板语法的 {@link String}。
         * @param questionKey 表示用户问题占位符的 {@link String}。
         * @param answerKey 表示用户答案占位符的 {@link String}。
         * @return 表示当前构建器的 {@link Builder}。
         * @throws IllegalArgumentException 当模板和输入占位符不匹配时。
         * @see <a href="https://mustache.github.io/">mustache</a>。
         */
        public Builder template(String template, String questionKey, String answerKey) {
            return this.template(new DefaultStringTemplate(template), questionKey, answerKey);
        }

        /**
         * 向构建器中设置模板和关键词。
         *
         * @param template 表示字符串模板的 {@link StringTemplate}。
         * @param questionKey 表示用户问题占位符的 {@link String}。
         * @param answerKey 表示用户答案占位符的 {@link String}。
         * @return 表示当前构建器的 {@link Builder}。
         * @throws IllegalArgumentException 当模板和输入占位符不匹配时。
         */
        public Builder template(StringTemplate template, String questionKey, String answerKey) {
            Set<String> placeholder = template.placeholder();
            Validation.isTrue(placeholder.contains(questionKey),
                    "Template '{0}' not match question key '{1}'.",
                    template,
                    questionKey);
            Validation.isTrue(placeholder.contains(answerKey),
                    "Template '{0}' not match answer key '{1}'.",
                    template,
                    answerKey);
            this.template = template;
            this.input = Arrays.asList(questionKey, answerKey);
            return this;
        }

        /**
         * 向构建器中添加例子。
         *
         * @param example 表示例子数组的 {@link Example}{@code []}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        public Builder example(Example... example) {
            return this.example(Arrays.asList(example));
        }

        /**
         * 向构建器中添加例子。
         *
         * @param examples 表示例子列表的 {@link Example}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        public Builder example(List<Example> examples) {
            this.examples.addAll(examples);
            return this;
        }

        /**
         * 向构建器中设置过滤器。
         *
         * @param filter 表示例子过滤器的 {@link BiFunction}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        public Builder filter(BiFunction<List<Example>, String, List<Example>> filter) {
            this.filter = filter;
            return this;
        }

        /**
         * 向构建器中设置分隔符。
         *
         * @param delimiter 表示分割符的 {@link String}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        public Builder delimiter(String delimiter) {
            this.delimiter = delimiter;
            return this;
        }

        /**
         * 构建对象。
         *
         * @return 表示构建出来的 {@link DefaultExampleSelector}。
         * @throws IllegalArgumentException 当模板为空时。
         */
        public DefaultExampleSelector build() {
            Validation.notNull(template, "The template cannot be null.");
            return new DefaultExampleSelector(this);
        }
    }
}