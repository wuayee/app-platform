/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.common.locale;

import modelengine.fitframework.util.CollectionUtils;
import modelengine.fitframework.util.StringUtils;
import modelengine.jade.authentication.context.UserContext;
import modelengine.jade.authentication.context.UserContextHolder;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * 提供获取系统语言的静态方法。
 *
 * @author 鲁为
 * @since 2024-09-04
 */
public class LocaleUtil {
    /**
     * 默认支持语言。
     */
    private static final List<Locale> LOCALES = Collections.unmodifiableList(
            Arrays.asList(new Locale("en"), new Locale("zh"), new Locale("en", "US"),
                    new Locale("zh", "CN")));

    /**
     * 获取系统语言的静态方法。
     *
     * @return 系统语言的类的 {@link Locale}。
     */
    public static Locale getLocale() {
        UserContext userContext = UserContextHolder.get();
        Locale locale;
        if (userContext == null || StringUtils.isEmpty(userContext.getLanguage())) {
            locale = java.util.Locale.getDefault();
        } else {
            List<java.util.Locale.LanguageRange> list = java.util.Locale.LanguageRange.parse(userContext.getLanguage());
            locale = CollectionUtils.isEmpty(list) ? java.util.Locale.getDefault() : java.util.Locale.lookup(list,
                    LOCALES);
        }
        return locale;
    }
}
