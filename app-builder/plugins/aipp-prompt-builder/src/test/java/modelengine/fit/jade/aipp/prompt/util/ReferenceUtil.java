/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.prompt.util;

import static modelengine.fit.jade.aipp.prompt.constant.Constant.PROMPT_METADATA_KEY;

import modelengine.fit.jade.aipp.prompt.PromptMessage;
import modelengine.fitframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 溯源测试工具类
 *
 * @author 刘信宏
 * @since 2024-12-28
 */
public class ReferenceUtil {
    /**
     * 获取引用文本的标识。
     *
     * @param promptMessage 表示提示词消息的 {@link PromptMessage}。
     * @return 表示引用文本的标识列表的 {@link List}{@code <}{@link String}{@code >}。
     */
    public static List<String> getReferenceIds(PromptMessage promptMessage) {
        Map<String, Object> reference = ObjectUtils.cast(promptMessage.getMetadata().get(PROMPT_METADATA_KEY));
        return new ArrayList<>(reference.keySet());
    }
}
