/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.memory.util;

import modelengine.fit.jade.aipp.memory.AippChatRound;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * 测试工具类。
 *
 * @author 易文渊
 * @since 2024-09-19
 */
public class TestUtils {
    /**
     * 产生历史记录。
     *
     * @param count 表示历史记录数量的 {@code int}。
     * @return 表示历史记录列表的 {@link List}{@code <}{@link AippChatRound}{@code >}。
     */
    public static List<AippChatRound> genHistories(int count) {
        return IntStream.range(0, count).mapToObj(num -> {
            String str = String.valueOf(num);
            AippChatRound chatRound = new AippChatRound();
            chatRound.setQuestion(str);
            chatRound.setAnswer(str);
            return chatRound;
        }).collect(Collectors.toList());
    }
}