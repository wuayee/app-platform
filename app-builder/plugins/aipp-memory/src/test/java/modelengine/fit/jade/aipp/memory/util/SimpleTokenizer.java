/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.memory.util;

import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fel.core.tokenizer.Tokenizer;

import java.util.List;

/**
 * 表示 {@link Tokenizer} 的简单实现。
 *
 * @author 易文渊
 * @since 2024-08-09
 */
public class SimpleTokenizer implements Tokenizer {
    @Override
    public List<Integer> encode(String text) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String decode(List<Integer> tokens) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int countToken(String text) {
        notNull(text, "Text cannot be null.");
        return text.length();
    }
}
