/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 */

package com.huawei.fitframework.util.wildcard.support;

import static org.assertj.core.api.Assertions.assertThat;

import com.huawei.fitframework.util.wildcard.Pattern;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * 表示 {@link DefaultPathPattern} 的单元测试。
 *
 * @author 季聿阶 j00559309
 * @since 2022-12-21
 */
@DisplayName("测试 DefaultPathPattern")
public class DefaultPathPatternTest {
    @ParameterizedTest
    @DisplayName("当匹配成功时，返回 true")
    @CsvSource({
            "/a,/a", "/a/*,/a/a", "/a/*,/a/ab", "/a/a?,/a/ab", "/a/**,/a", "/a/**,/a/b", "/a/**,/a/b/c",
            "/**/*.*,/a.html", "/**/*.*,/a/b/c.html"
    })
    void shouldReturnTrueWhenMatches(String pattern, String path) {
        boolean matches = Pattern.forPath(pattern, '/').matches(path);
        assertThat(matches).isTrue();
    }

    @ParameterizedTest
    @DisplayName("当匹配失败时，返回 false")
    @CsvSource({"/a,/b", "/a/*,/a/b/c", "/a/a?,/a/bb", "/a/**/b,/a", "/**/*.*,/a", "/**/*.*,/a/b/c"})
    void shouldReturnFalseWhenNotMatches(String pattern, String path) {
        boolean matches = Pattern.forPath(pattern, '/').matches(path);
        assertThat(matches).isFalse();
    }
}
