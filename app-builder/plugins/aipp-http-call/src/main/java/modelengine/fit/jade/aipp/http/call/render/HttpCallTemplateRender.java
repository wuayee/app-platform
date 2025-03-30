/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.http.call.render;

import modelengine.fitframework.parameterization.ParameterizedString;
import modelengine.fitframework.parameterization.ParameterizedStringResolver;
import modelengine.fitframework.parameterization.ResolvedParameter;
import modelengine.fitframework.util.StringUtils;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 模板翻译器。
 *
 * @author 张越
 * @since 2024-12-17
 */
public class HttpCallTemplateRender {
    private static final ParameterizedStringResolver FORMATTER = ParameterizedStringResolver.create("{{", "}}", '\0',
            false);

    private final Map<String, String> args;

    public HttpCallTemplateRender(Map<String, String> args) {
        this.args = args;
    }

    /**
     * 渲染模板.
     *
     * @param template 模板字符串.
     * @return {@link String} 渲染后的模板字符串.
     */
    public String render(String template) {
        ParameterizedString parameterizedString = FORMATTER.resolve(template);
        Set<String> params = parameterizedString.getParameters()
                .stream()
                .map(ResolvedParameter::getName)
                .collect(Collectors.toSet());

        if (params.isEmpty()) {
            return template;
        }

        // 校验参数是否存在.
        params.forEach(p -> {
            if (this.args.get(p) == null) {
                throw new IllegalArgumentException(StringUtils.format("Template arg[{0}] not exists in args.", p));
            }
        });

        return parameterizedString.format(this.args);
    }
}