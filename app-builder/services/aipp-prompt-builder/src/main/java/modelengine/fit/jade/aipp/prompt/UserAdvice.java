/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.prompt;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

/**
 * 用户提示词建议。
 *
 * @author 刘信宏
 * @since 2024-12-02
 */
@AllArgsConstructor
@Getter
public class UserAdvice {
    /**
     * 人设与背景描述。
     */
    private String background;

    /**
     * 用户提示词模板。
     */
    private String template;

    /**
     * 提示词参数。
     */
    private Map<String, String> variables;
}
