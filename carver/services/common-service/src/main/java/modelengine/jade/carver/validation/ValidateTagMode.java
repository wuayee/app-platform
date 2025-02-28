/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.carver.validation;

import modelengine.fitframework.util.StringUtils;

/**
 * 校验工具标签拼接方式的工具类。
 *
 * @author 鲁为
 * @since 2024-07-19
 */
public class ValidateTagMode {
    /**
     * 校验校验工具标签拼接方式。
     *
     * @param mode 表示标签拼接方式的 {@link String}。
     * @return 校验之后的标签拼接方式的 {@link String}。
     */
    public static String validateTagMode(String mode) {
        if (StringUtils.isEmpty(mode) || mode.equalsIgnoreCase("AND")) {
            return "AND";
        }
        return "OR";
    }
}
