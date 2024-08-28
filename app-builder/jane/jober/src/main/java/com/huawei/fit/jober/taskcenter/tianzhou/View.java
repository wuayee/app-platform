/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.tianzhou;

import com.huawei.fit.jober.common.Constant;
import com.huawei.fit.jober.common.exceptions.JobberException;

import modelengine.fit.http.server.HttpClassicServerRequest;
import modelengine.fitframework.plugin.Plugin;
import modelengine.fitframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Supplier;

/**
 * tianzhou专属返回体包装。
 *
 * @author 陈镕希
 * @since 2023-08-24
 */
public class View {
    /**
     * viewOf
     *
     * @param supplier supplier
     * @param plugin plugin
     * @param httpRequest 请求体
     * @return Map<String, Object>
     */
    public static Map<String, Object> viewOf(Supplier<Object> supplier, Plugin plugin,
            HttpClassicServerRequest httpRequest) {
        String context = "";
        if (httpRequest != null) {
            context = httpRequest.headers()
                    .first("Accept-Language")
                    .orElse(httpRequest.headers().first("accept-language").orElse(StringUtils.EMPTY));
        }
        try {
            return new HashMap<String, Object>() {
                {
                    put("code", 0);
                    put("data", supplier.get());
                    put("message", "success");
                }
            };
        } catch (JobberException exception) {
            String language = context;
            return new HashMap<String, Object>() {
                {
                    put("code", exception.getCode());
                    put("message", getLocaleMessage(String.valueOf(exception.getCode()), exception.getMessage(),
                            exception.getArgs(), language, plugin));
                }
            };
        } catch (Exception exception) {
            return new HashMap<String, Object>() {
                {
                    put("code", -1);
                    put("message", exception.getMessage());
                }
            };
        }
    }

    /**
     * viewOf
     *
     * @param runnable runnable
     * @param plugin plugin
     * @param httpRequest httpRequest
     * @return Map<String, Object>
     */
    public static Map<String, Object> viewOf(Runnable runnable, Plugin plugin, HttpClassicServerRequest httpRequest) {
        return viewOf(() -> {
            runnable.run();
            return null;
        }, plugin, httpRequest);
    }

    /**
     * 获取国际化异常信息
     *
     * @param plugin plugin
     * @param code 错误编码
     * @param defaultMsg 默认返回信息
     * @param params plugin
     * @param language 语言
     * @return 返回响应信息
     */
    public static String getLocaleMessage(String code, String defaultMsg, Object[] params, String language,
            Plugin plugin) {
        if (StringUtils.isEmpty(language)) {
            return defaultMsg;
        }
        List<Locale.LanguageRange> list = Locale.LanguageRange.parse(language);
        Locale locale = StringUtils.isNotEmpty(language) ? Locale.lookup(list, Constant.LOCALES) : Locale.getDefault();
        try {
            String message = plugin.sr().getMessage(locale, code, params);
            if (StringUtils.isEmpty(message)) {
                return defaultMsg;
            }
            return message;
        } catch (NullPointerException | UnsupportedOperationException | ClassCastException e) {
            return defaultMsg;
        }
    }
}