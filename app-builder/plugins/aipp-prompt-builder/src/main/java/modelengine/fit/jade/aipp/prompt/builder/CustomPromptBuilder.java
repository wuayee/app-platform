/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.prompt.builder;

import modelengine.jade.common.globalization.LocaleService;

import modelengine.fel.core.template.StringTemplate;
import modelengine.fel.core.template.support.DefaultStringTemplate;
import modelengine.fit.jade.aipp.prompt.PromptBuilder;
import modelengine.fit.jade.aipp.prompt.PromptMessage;
import modelengine.fit.jade.aipp.prompt.PromptStrategy;
import modelengine.fit.jade.aipp.prompt.UserAdvice;
import modelengine.fit.jade.aipp.prompt.constant.InternalConstant;
import modelengine.fit.jade.aipp.prompt.constant.PromptBuilderOrder;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Order;
import modelengine.fitframework.inspection.Validation;
import modelengine.fitframework.util.StringUtils;

import java.util.Map;
import java.util.Optional;

/**
 * 自定义提示词构造器。
 *
 * @author 刘信宏
 * @since 2024-12-02
 */
@Component
@Order(PromptBuilderOrder.DEFAULT)
public class CustomPromptBuilder implements PromptBuilder {
    private final LocaleService localeService;

    public CustomPromptBuilder(LocaleService localeService) {
        this.localeService = localeService;
    }

    @Override
    public Optional<PromptMessage> build(UserAdvice userAdvice, Map<String, Object> context) {
        Validation.notNull(userAdvice, "The user advice cannot be null.");
        Validation.notNull(userAdvice.getVariables(), "The prompt variables cannot be null.");

        StringTemplate template = new DefaultStringTemplate(userAdvice.getTemplate());
        String sysMessage = this.renderSysMessage(userAdvice.getBackground());
        String humanMessage = template.render(userAdvice.getVariables());
        return Optional.of(new PromptMessage(sysMessage, humanMessage));
    }

    @Override
    public PromptStrategy strategy() {
        return PromptStrategy.CUSTOM;
    }

    private String renderSysMessage(String background) {
        if (StringUtils.isBlank(background)) {
            return StringUtils.EMPTY;
        }
        String systemBackground = this.localeService.localize(InternalConstant.BACKGROUND_KEY);
        return systemBackground + InternalConstant.BLOCK_SEPARATOR + background;
    }
}
