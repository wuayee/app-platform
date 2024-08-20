/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 */

package com.huawei.fitframework.util.wildcard.support;

import com.huawei.fitframework.util.StringUtils;
import com.huawei.fitframework.util.wildcard.PathPattern;
import com.huawei.fitframework.util.wildcard.Pattern;
import com.huawei.fitframework.util.wildcard.SymbolClassifier;
import com.huawei.fitframework.util.wildcard.SymbolMatcher;
import com.huawei.fitframework.util.wildcard.SymbolSequence;
import com.huawei.fitframework.util.wildcard.SymbolType;

import java.util.ArrayList;
import java.util.Objects;

/**
 * 表示 {@link PathPattern} 的默认实现。
 *
 * @author 季聿阶
 * @since 2022-12-21
 */
public class DefaultPathPattern extends DefaultPattern<String> implements PathPattern {
    private static final String MULTIPLE_WILDCARD = "**";
    private static final String SINGLE_WILDCARD = "*";

    private static final SymbolClassifier<String> DEFAULT_PATH_CLASSIFIER = symbol -> {
        if (Objects.equals(symbol, MULTIPLE_WILDCARD)) {
            return SymbolType.MULTIPLE_WILDCARD;
        } else if (Objects.equals(symbol, SINGLE_WILDCARD)) {
            return SymbolType.SINGLE_WILDCARD;
        } else {
            return SymbolType.NORMAL;
        }
    };
    private static final SymbolMatcher<String> DEFAULT_PATH_MATCHER =
            (pattern, value) -> Pattern.forCharSequence(pattern).matches(value);

    private final char pathSeparator;

    /**
     * 构造一个新的 {@link DefaultPathPattern} 实例。
     *
     * @param pattern 表示路径模式的 {@link String}。
     * @param pathSeparator 表示路径分隔符的 {@code char}。
     */
    public DefaultPathPattern(String pattern, char pathSeparator) {
        super(SymbolSequence.fromList(StringUtils.split(pattern,
                pathSeparator,
                ArrayList::new,
                StringUtils::isNotBlank)), DEFAULT_PATH_CLASSIFIER, DEFAULT_PATH_MATCHER);
        this.pathSeparator = pathSeparator;
    }

    @Override
    public boolean matches(String path) {
        return this.matches(SymbolSequence.fromList(StringUtils.split(path,
                this.pathSeparator,
                ArrayList::new,
                StringUtils::isNotBlank)));
    }
}
