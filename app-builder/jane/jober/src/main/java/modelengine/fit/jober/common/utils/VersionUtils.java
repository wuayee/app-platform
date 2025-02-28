/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.common.utils;

import modelengine.fitframework.util.StringUtils;

/**
 * 跟版本相关的工具类方法
 *
 * @author 邬涨财
 * @since 2024-06-14
 */
public class VersionUtils {
    /**
     * 获取去除版本号的实际名称。
     *
     * @param name 表示带有版本号的名称的 {@link String}。
     * @return 表示去除版本号的实际名称的 {@link String}。
     */
    public static String getRealName(String name) {
        if (StringUtils.isEmpty(name) || !name.contains("|")) {
            return name;
        }
        int index = name.lastIndexOf("|");
        return name.substring(0, index);
    }
}
