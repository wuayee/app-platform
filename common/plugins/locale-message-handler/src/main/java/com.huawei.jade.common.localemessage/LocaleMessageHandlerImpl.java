/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.common.localemessage;

import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.plugin.Plugin;
import modelengine.fitframework.util.CollectionUtils;
import modelengine.fitframework.util.StringUtils;

import com.huawei.jade.authentication.context.UserContext;
import com.huawei.jade.authentication.context.UserContextHolder;
import com.huawei.jade.common.code.CommonRetCode;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * 国际化消息处理。
 * 用于获取国际化信息，支持错误码、默认错误信息和参数的动态替换。
 *
 * @author 张雪彬
 * @since 2024-8-16
 */
@Component
public class LocaleMessageHandlerImpl implements LocaleMessageHandler {
    private static final Logger log = Logger.get(LocaleMessageHandlerImpl.class);

    /**
     * 默认支持语言。
     */
    private static final List<Locale> LOCALES = Collections.unmodifiableList(
        Arrays.asList(new Locale("en"), new Locale("zh"), new Locale("en", "US"), new Locale("zh", "CN")));

    private final Plugin plugin;

    /**
     * 构造函数，用于初始化LocaleMessageHandlerImpl实例。
     *
     * @param plugin 插件实例，用于获取国际化信息
     */
    public LocaleMessageHandlerImpl(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getLocaleMessage(String code, String defaultMsg, Object... params) {
        Locale locale = getLocale();
        try {
            return this.plugin.sr().getMessage(locale, code, defaultMsg, params);
        } catch (Exception e) {
            log.warn("Failed to get locale message. [code={}]", code, e);
            return getDefaultMessage();
        }
    }

    @Override
    public String getDefaultMessage() {
        Locale locale = getLocale();
        return plugin.sr().getMessage(locale, String.valueOf(CommonRetCode.INTERNAL_ERROR.getCode()));
    }

    private Locale getLocale() {
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
