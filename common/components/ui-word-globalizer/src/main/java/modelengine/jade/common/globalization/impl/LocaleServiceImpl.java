/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.common.globalization.impl;

import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.plugin.Plugin;
import modelengine.jade.common.globalization.LocaleService;
import modelengine.jade.common.locale.LocaleUtil;

import java.util.Locale;
import java.util.MissingResourceException;

/**
 * 界面词国际化处理。
 *
 * @author 鲁为
 * @since 2024-08-17
 */
@Component("common")
public class LocaleServiceImpl implements LocaleService {
    private static final Logger logger = Logger.get(LocaleServiceImpl.class);

    private final Plugin plugin;

    /**
     * 用插件实例构建 {@link LocaleServiceImpl} 的实例。
     *
     * @param plugin 表示插件的 {@link Plugin}。
     */
    public LocaleServiceImpl(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public String localize(String code, Object... params) {
        try {
            return this.plugin.sr().getMessage(LocaleUtil.getLocale(), code, params);
        } catch (MissingResourceException e) {
            logger.warn("The bundle resource is missing. [code={}]", code);
            return "";
        }
    }

    @Override
    public String localize(Locale locale, String code, Object... params) {
        try {
            return this.plugin.sr().getMessage(locale, code, params);
        } catch (MissingResourceException e) {
            logger.warn("The bundle resource is missing. [code={}]", code);
            return "";
        }
    }

    @Override
    public String localizeOrDefault(String code, String defaultCode, Object... params) {
        try {
            return this.plugin.sr().getMessage(LocaleUtil.getLocale(), code, params);
        } catch (MissingResourceException e) {
            logger.warn("The bundle resource is missing. [code={}]", code);
            return this.plugin.sr().getMessage(LocaleUtil.getLocale(), defaultCode, params);
        }
    }

    @Override
    public String localizeOrDefault(Locale locale, String code, String defaultCode, Object... params) {
        try {
            return this.plugin.sr().getMessage(locale, code, params);
        } catch (MissingResourceException e) {
            logger.warn("The bundle resource is missing. [code={}]", code);
            return this.plugin.sr().getMessage(locale, defaultCode, params);
        }
    }
}
