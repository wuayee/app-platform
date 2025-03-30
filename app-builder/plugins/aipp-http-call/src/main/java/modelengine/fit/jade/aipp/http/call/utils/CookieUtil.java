/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.http.call.utils;

import modelengine.fit.http.Cookie;
import modelengine.fitframework.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

/**
 * Cookie工具类。
 *
 * @author 张越
 * @since 2025-02-22
 */
public class CookieUtil {
    private static final String COOKIE_SEPARATOR = ";";
    private static final String NAME_VALUE_SEPARATOR = "=";

    /**
     * 解析.
     *
     * @param cookieString cookie字符串.
     * @return {@link List}{@code <}{@link Cookie}{@code >} 列表.
     */
    public static List<Cookie> parse(String cookieString) {
        if (StringUtils.isBlank(cookieString)) {
            return Collections.emptyList();
        }
        String[] cookies = cookieString.split(COOKIE_SEPARATOR);
        return Stream.of(cookies).map(c -> {
            String[] nameValues = c.split(NAME_VALUE_SEPARATOR);
            if (nameValues.length != 2) {
                throw new IllegalArgumentException(StringUtils.format("Invalid cookie string: {0}", cookieString));
            }
            return Cookie.builder().name(nameValues[0]).value(nameValues[1]).build();
        }).toList();
    }
}
