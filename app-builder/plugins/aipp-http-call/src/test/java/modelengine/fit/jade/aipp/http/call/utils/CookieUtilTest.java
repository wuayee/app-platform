/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.http.call.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import modelengine.fit.http.Cookie;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * {@link CookieUtil}测试集。
 *
 * @author 张越
 * @since 2025-02-24
 */
@DisplayName("测试 cookie 工具类")
public class CookieUtilTest {
    @Test
    @DisplayName("cookie 是空字符串")
    void testCookieIsEmpty() {
        // given
        // when
        List<Cookie> cookies = CookieUtil.parse("");

        // then
        Assertions.assertTrue(cookies.isEmpty());
    }

    @Test
    @DisplayName("有一个 cookie")
    void testOneCookie() {
        // given.
        // when.
        List<Cookie> cookies = CookieUtil.parse("jsessionid=e7e85bf6-223c-44f4-b0a1-6dc8030482cb");

        // then.
        assertEquals(1, cookies.size());
        assertEquals("jsessionid", cookies.get(0).name());
        assertEquals("e7e85bf6-223c-44f4-b0a1-6dc8030482cb", cookies.get(0).value());
    }

    @Test
    @DisplayName("有多个 cookie")
    void testMultiCookie() {
        // given.
        // when.
        List<Cookie> cookies = CookieUtil.parse(
                "jsessionid=e7e85bf6-223c-44f4-b0a1-6dc8030482cb; "
                        + "jsessionid=e7e85bf6-223c-44f4-b0a1-6dc8030482cb; "
                        + "idss_cid=3f62ebb1-3f3f-42c0-b7c8-cb7c36ca482c;");

        // then.
        assertEquals(3, cookies.size());
    }

    @Test
    @DisplayName("cookie 格式异常")
    void testCookieFormatInvalid() {
        // given.
        // when.
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> CookieUtil.parse("jsessionid="));

        // then.
        assertTrue(exception.getMessage().startsWith("Invalid cookie string"));
    }
}
