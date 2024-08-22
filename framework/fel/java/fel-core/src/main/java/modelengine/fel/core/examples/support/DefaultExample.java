/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fel.core.examples.support;

import modelengine.fitframework.inspection.Validation;
import modelengine.fel.core.examples.Example;

/**
 * 表示 {@link Example} 的默认实现。
 *
 * @author 易文渊
 * @since 2024-04-25
 */
public class DefaultExample implements Example {
    private final String question;
    private final String answer;

    /**
     * 使用问答对创建 {@link DefaultExample} 的实例。
     *
     * @param question 表示问题的 {@link String}。
     * @param answer 表示答案的 {@link String}。
     */
    public DefaultExample(String question, String answer) {
        this.question = Validation.notBlank(question, "The question cannot be blank.");
        this.answer = Validation.notBlank(answer, "The answer cannot be blank.");
    }

    @Override
    public String question() {
        return this.question;
    }

    @Override
    public String answer() {
        return this.answer;
    }
}