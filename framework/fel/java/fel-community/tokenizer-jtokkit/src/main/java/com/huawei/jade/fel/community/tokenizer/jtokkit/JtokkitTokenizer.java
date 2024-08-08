/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.community.tokenizer.jtokkit;

import static com.huawei.fitframework.util.ObjectUtils.nullIf;

import com.huawei.fitframework.inspection.Validation;
import com.huawei.jade.fel.core.tokenizer.Tokenizer;

import com.knuddels.jtokkit.Encodings;
import com.knuddels.jtokkit.api.Encoding;
import com.knuddels.jtokkit.api.EncodingType;
import com.knuddels.jtokkit.api.IntArrayList;

import java.util.List;

/**
 * 表示 {@link Tokenizer} 的 jtokkit 实现。
 *
 * @author 易文渊
 * @see <a href="https://github.com/knuddelsgmbh/jtokkit">jtokkit</a>
 * @since 2024-08-10
 */
public class JtokkitTokenizer implements Tokenizer {
    private final Encoding estimator;

    /**
     * 构造一个新的 {@link JtokkitTokenizer} 实例。
     *
     * @param encodingType 表示编码器类型的 {@link EncodingType}，如果为 {@code null}，则使用 {@link EncodingType#O200K_BASE}。
     */
    public JtokkitTokenizer(EncodingType encodingType) {
        this.estimator = Encodings.newLazyEncodingRegistry().getEncoding(nullIf(encodingType, EncodingType.O200K_BASE));
    }

    @Override
    public List<Integer> encode(String text) {
        Validation.notNull(text, "The text cannot be null.");
        return this.estimator.encode(text).boxed();
    }

    @Override
    public String decode(List<Integer> tokens) {
        Validation.notNull(tokens, "The tokens cannot be null.");
        IntArrayList tokenArray = new IntArrayList(tokens.size());
        tokens.forEach(tokenArray::add);
        return this.estimator.decode(tokenArray);
    }
}