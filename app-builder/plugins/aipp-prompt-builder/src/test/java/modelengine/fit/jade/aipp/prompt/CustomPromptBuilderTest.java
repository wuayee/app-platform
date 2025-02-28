/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.prompt;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import modelengine.jade.common.globalization.LocaleService;

import modelengine.fit.jade.aipp.prompt.builder.CustomPromptBuilder;
import modelengine.fit.jade.aipp.prompt.constant.InternalConstant;
import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.test.annotation.FitTestWithJunit;
import modelengine.fitframework.test.annotation.Mock;
import modelengine.fitframework.util.MapBuilder;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

/**
 * {@link CustomPromptBuilder} 提示词构造器的测试。
 *
 * @author 刘信宏
 * @since 2024-12-04
 */
@FitTestWithJunit(includeClasses = {CustomPromptBuilder.class})
class CustomPromptBuilderTest {
    @Mock
    private LocaleService localeService;

    @Fit
    private PromptBuilder promptBuilder;

    @BeforeEach
    void setup() {
        when(this.localeService.localize(eq(InternalConstant.BACKGROUND_KEY))).thenReturn("# 人设与回复逻辑");
    }

    @Test
    void shouldOkWhenCustomBuilderWithNormalUserAdvice() {
        assertThat(this.promptBuilder.strategy()).isEqualTo(PromptStrategy.CUSTOM);

        Map<String, String> variables = MapBuilder.<String, String>get().put("key0", "value0").build();
        UserAdvice userAdvice = new UserAdvice("background", "template {{key0}}", variables);

        assertThat(this.promptBuilder.build(userAdvice, Collections.emptyMap())).isPresent();
        assertThat(this.promptBuilder.build(userAdvice, null)).isPresent();

        Optional<PromptMessage> promptMessage = this.promptBuilder.build(userAdvice, Collections.emptyMap());
        Assertions.assertThat(promptMessage).isPresent();
        assertThat(promptMessage.get()).extracting(PromptMessage::getSystemMessage, PromptMessage::getHumanMessage)
                .containsExactly("# 人设与回复逻辑\n\nbackground", "template value0");
    }

    @Test
    void shouldFailWhenCustomBuilderWithInvalidUserAdvice() {
        assertThatThrownBy(() -> this.promptBuilder.build(new UserAdvice("background", "template {{key0}}", null),
                Collections.emptyMap())).isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> this.promptBuilder.build(null, Collections.emptyMap())).isInstanceOf(
                IllegalArgumentException.class);
    }
}
