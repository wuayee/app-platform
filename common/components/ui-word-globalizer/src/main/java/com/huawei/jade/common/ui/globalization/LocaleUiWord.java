/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.common.ui.globalization;

import com.huawei.jade.authentication.context.UserContext;
import com.huawei.jade.authentication.context.UserContextHolder;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.exception.FitException;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.plugin.Plugin;
import modelengine.fitframework.util.CollectionUtils;
import modelengine.fitframework.util.StringUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * 界面词国际化处理。
 *
 * @author 鲁为
 * @since 2024-08-17
 */
@Component
public class LocaleUiWord {
    private static final Logger log = Logger.get(LocaleUiWord.class);

    /**
     * 默认支持语言。
     */
    private static final List<Locale> LOCALES = Collections.unmodifiableList(
            Arrays.asList(new Locale("en"), new Locale("zh"), new Locale("en", "US"), new Locale("zh", "CN")));


    private final Plugin plugin;

    /**
     * 用插件实例构建 {@link LocaleUiWord} 的实例。
     *
     * @param plugin 表示插件的 {@link Plugin}。
     */
    public LocaleUiWord(Plugin plugin) {
        this.plugin = plugin;
    }

    /**
     * 根据标识获取国际化消息。
     *
     * @param code 表示国际化消息的标识的 {@link String}。
     * @return 表示国际化消息的 {@link String}。
     */
    public String getLocaleMessage(String code) {
        Locale locale = LocaleUiWord.getLocale();
        try {
            return this.plugin.sr().getMessage(locale, code);
        } catch (FitException e) {
            log.warn("Failed to get locale message. [code={}]", code);
            return "";
        }
    }

    /**
     * 获取国际化的类。
     *
     * @return 国际化的类的 {@link Locale}。
     */
    public static Locale getLocale() {
        UserContext userContext = UserContextHolder.get();
        Locale locale;
        if (userContext == null || StringUtils.isEmpty(userContext.getLanguage())) {
            locale = Locale.getDefault();
        } else {
            List<Locale.LanguageRange> list = Locale.LanguageRange.parse(userContext.getLanguage());
            locale = CollectionUtils.isEmpty(list) ? Locale.getDefault() : Locale.lookup(list, LOCALES);
        }
        return locale;
    }
}
