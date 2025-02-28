/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.appbuilder.security.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Xss相关工具类对应测试类。
 *
 * @author 陈镕希
 * @since 2024-10-22
 */
class XssUtilsTest {
    private static final String USER_INPUT = "& < > \" ' : [ ] $ ( ) % + \\ / # ` * , - ; = ^ |";

    @Test
    public void filterHtml() {
        Assertions.assertEquals("&amp; &lt; &gt; \" ' : [ ] $ ( ) % + \\ / # ` * , - ; = ^ |",
                XssUtils.filter(USER_INPUT));
    }

    @Test
    public void filterSafeTag() {
        Assertions.assertEquals("<p>content</p>", XssUtils.filter("<p>content</p>"));
    }

    @Test
    public void filterUnsafeTag() {
        Assertions.assertEquals("", XssUtils.filter("<script>content</script>"));
    }

    @Test
    public void filterEmptyString() {
        Assertions.assertEquals("", XssUtils.filter(""));
    }

    @Test
    public void filterNull() {
        Assertions.assertNull(XssUtils.filter(null));
    }
}