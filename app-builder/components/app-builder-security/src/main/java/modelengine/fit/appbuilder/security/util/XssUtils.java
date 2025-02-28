/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.appbuilder.security.util;

import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;

/**
 * 为Xss防御提供工具方法。
 *
 * @author 陈镕希
 * @since 2024-10-22
 */
public class XssUtils {
    /**
     * 将用户数据输出到html body某处时，必须经过xss过滤转义，比如：
     * <body>...【用户数据】...</body>
     * <div>...【用户数据】...</div>
     * 以及其它普通的html标签，比如p, b, td等等。
     *
     * @param input 原始输入的{@link String}。
     * @return 过滤后的 {@link String}。
     */
    public static String filter(String input) {
        return input == null ? input : Jsoup.clean(input, xssWhitelist());
    }

    /**
     * XSS过滤白名单
     *
     * @return xss白名单 {@link Safelist}
     */
    private static Safelist xssWhitelist() {
        final String[] tags = {
                "a", "b", "blockquote", "br", "caption", "cite", "code", "col", "colgroup", "dd", "div", "dl", "dt",
                "em", "h1", "h2", "h3", "h4", "h5", "h6", "i", "img", "li", "ol", "p", "pre", "q", "small", "strike",
                "strong", "sub", "sup", "table", "tbody", "td", "tfoot", "th", "thead", "tr", "u", "ul", "embed",
                "object", "param", "span"
        };
        final String[] aAttr = {"href", "class", "style", "target", "rel", "nofollow"};
        final String[] imgAttr = {"align", "alt", "height", "src", "title", "width", "class", "style"};
        final String[] embedAttr = {
                "src", "wmode", "flashvars", "pluginspage", "allowFullScreen", "allowfullscreen", "quality", "width",
                "height", "align", "allowScriptAccess", "allowscriptaccess", "allownetworking", "type"
        };
        final String[] objectAttr = {
                "type", "id", "name", "data", "width", "height", "style", "classid", "codebase"
        };
        return new Safelist()

                // 支持的标签
                .addTags(tags)

                // 支持的标签属性
                .addAttributes("a", aAttr)
                .addAttributes("blockquote", "cite")
                .addAttributes("code", "class", "style")
                .addAttributes("col", "span", "width")
                .addAttributes("colgroup", "span", "width")
                .addAttributes("div", "class", "id", "style")
                .addAttributes("embed", embedAttr)
                .addAttributes("img", imgAttr)
                .addAttributes("li", "class", "style")
                .addAttributes("object", objectAttr)
                .addAttributes("ol", "start", "type")
                .addAttributes("p", "class", "style")
                .addAttributes("param", "name", "value")
                .addAttributes("pre", "class", "style")
                .addAttributes("q", "cite")
                .addAttributes("span", "class", "style")
                .addAttributes("table", "summary", "width", "class", "style")
                .addAttributes("tr", "abbr", "axis", "colspan", "rowspan", "width", "style")
                .addAttributes("td", "abbr", "axis", "colspan", "rowspan", "width", "style")
                .addAttributes("th", "abbr", "axis", "colspan", "rowspan", "width", "style", "scope")
                .addAttributes("ul", "type", "style")

                // 标签属性对应的协议
                .addProtocols("a", "href", "ftp", "http", "https", "mailto", "#")
                .addProtocols("blockquote", "cite", "http", "https")
                .addProtocols("cite", "cite", "http", "https")
                .addProtocols("q", "cite", "http", "https")
                .addProtocols("embed", "src", "http", "https");
    }
}