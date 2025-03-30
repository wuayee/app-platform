/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.memory.support;

import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fel.core.template.BulkStringTemplate;
import modelengine.fit.jade.aipp.memory.AippChatRound;

import java.util.Collections;
import java.util.List;

/**
 * 用对话轮数设置滑动窗口大小的历史记录。
 *
 * @author 易文渊
 * @since 2024-09-19
 */
public class AippBufferWindowMemory extends AbstractAippChatMemory {
    private final List<AippChatRound> histories;

    /**
     * 构造历史记录。
     *
     * @param histories 表示历史记录列表的 {@link List}{@code <}{@link AippChatRound}{@code >}。
     * @param maxRound 表示可记录的最大对话轮数的 {@code int}。
     * @param template 表示模板的 {@link BulkStringTemplate}。
     */
    public AippBufferWindowMemory(List<AippChatRound> histories, int maxRound, BulkStringTemplate template) {
        super(template);
        this.histories = filterWithMaxRounds(histories, maxRound);
    }

    private static List<AippChatRound> filterWithMaxRounds(List<AippChatRound> histories, int maxRound) {
        notNull(histories, "Histories cannot be null.");
        int round = histories.size();
        return round <= maxRound ? histories : histories.subList(round - maxRound, round);
    }

    @Override
    protected List<AippChatRound> getAvailableHistories() {
        return Collections.unmodifiableList(this.histories);
    }
}