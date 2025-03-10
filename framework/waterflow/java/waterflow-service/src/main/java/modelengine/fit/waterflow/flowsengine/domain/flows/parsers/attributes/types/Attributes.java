/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.flowsengine.domain.flows.parsers.attributes.types;

import modelengine.fitframework.util.StringUtils;

import java.util.Locale;
import java.util.Map;

/**
 * 图形接口.
 *
 * @author 张越
 * @since 2024-08-05
 */
public interface Attributes {
    /**
     * 获取数据.
     *
     * @return {@link Map}{@code <}{@link String}{@code ,}{@link Object}{@code >} 键值对.
     */
    Map<String, Object> getData();

    /**
     * 是否可在流程中运行.
     *
     * @return true/false.
     */
    boolean isRunnable();

    /**
     * 判断是否是 state 类型.
     *
     * @param type 类型字符串.
     * @return true/false.
     */
    static boolean isState(String type) {
        if (StringUtils.isBlank(type)) {
            return false;
        }
        return "state".equals(type.toLowerCase(Locale.ROOT)) || type.toLowerCase(Locale.ROOT).endsWith("state");
    }
}
