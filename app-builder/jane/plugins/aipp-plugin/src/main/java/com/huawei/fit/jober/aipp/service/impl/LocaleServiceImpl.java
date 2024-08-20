/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.service.impl;

import com.huawei.fit.jober.aipp.repository.I18nRepository;
import com.huawei.fit.jober.aipp.service.LocaleService;
import com.huawei.fitframework.annotation.Component;

import java.util.Locale;
import java.util.Map;

/**
 * 国际化字段值处理实现。
 * 用于获取国际化信息，支持数据库内置参数的动态替换
 *
 * @author 陈潇文
 * @since 2024-08-20
 */
@Component
public class LocaleServiceImpl implements LocaleService {
    private static Map<String, Map<String, String>> resourceMap = null;

    private final I18nRepository i18nRepository;

    public LocaleServiceImpl(I18nRepository i18nRepository) {
        this.i18nRepository = i18nRepository;
    }

    @Override
    public String getLocaleMessage(String key, Locale locale) {
        if (resourceMap == null) {
            resourceMap = this.i18nRepository.selectResource();
        }
        return resourceMap.get(locale.getLanguage())
                .getOrDefault(key, resourceMap.get(Locale.ENGLISH.getLanguage()).get(key));
    }
}
