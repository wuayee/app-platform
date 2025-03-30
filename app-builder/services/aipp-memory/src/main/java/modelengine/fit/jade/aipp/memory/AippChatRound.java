/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.memory;

import lombok.Data;
import modelengine.fitframework.util.MapBuilder;

import java.util.Map;

/**
 * 表示历史记录的实体。
 *
 * @author 易文渊
 * @since 2024-09-19
 */
@Data
public class AippChatRound {
    /**
     * 用户问题。
     */
    private String question;

    /**
     * 用户答案。
     */
    private String answer;

    /**
     * 将历史记录实体转换为字典。
     *
     * @return 表示历史记录的 {@link Map}{@code <}{@link String}{@code , }{@link String}{@code >}。
     */
    public Map<String, String> toMap() {
        return MapBuilder.<String, String>get().put("question", this.question).put("answer", this.answer).build();
    }
}