/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fel.core.template.support;

import modelengine.fel.core.template.StringTemplate;
import modelengine.fitframework.inspection.Validation;
import modelengine.fitframework.merge.ConflictResolutionPolicy;
import modelengine.fitframework.parameterization.ParameterizedString;
import modelengine.fitframework.parameterization.ParameterizedStringResolver;
import modelengine.fitframework.parameterization.ResolvedParameter;
import modelengine.fitframework.util.MapUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * 表示生成字符串的默认模版实现。
 *
 * @author 易文渊
 * @since 2024-04-25
 */
public class DefaultStringTemplate implements StringTemplate {
    private static final ParameterizedStringResolver FORMATTER = ParameterizedStringResolver.create("{{", "}}", '/');

    private final ParameterizedString parameterizedString;

    private final Set<String> placeholder;

    private final Map<String, Supplier<String>> builtin;

    /**
     * 使用 mustache 模板语法创建 {@link DefaultStringTemplate}。
     *
     * @param template 表示使用 mustache 模板语法的 {@link String}。
     * @see <a href="https://mustache.github.io/">mustache</a>。
     */
    public DefaultStringTemplate(String template) {
        this.parameterizedString = FORMATTER.resolve(template);
        this.placeholder = this.parameterizedString.getParameters()
                .stream()
                .map(ResolvedParameter::getName)
                .collect(Collectors.toSet());
        this.builtin = new HashMap<>();
    }

    /**
     * 使用静态值默认替换 mustache 标签。
     *
     * @param key 表示占位符的 {@link String}。
     * @param value 表示静态值的 {@link String}。
     * @return 返回表示当前实例的 {@link StringTemplate}。
     */
    public StringTemplate partial(String key, String value) {
        Validation.notBlank(value, "The input value cannot be blank");
        return this.partial(key, () -> value);
    }

    /**
     * 使用函数返回值值替换 mustache 标签。
     *
     * @param key 表示占位符的 {@link String}。
     * @param supplier 表示提供参数的 {@link Supplier}{@code <}{@link String}{@code >}。
     * @return 返回表示当前实例的 {@link StringTemplate}。
     */
    public StringTemplate partial(String key, Supplier<String> supplier) {
        Validation.isTrue(this.placeholder.contains(key), "The template not contained '{0}'", key);
        this.placeholder.remove(key);
        this.builtin.put(key, supplier);
        return this;
    }

    @Override
    public String render(Map<String, String> values) {
        Map<String, String> builtinValues =
                this.builtin.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().get()));
        return parameterizedString.format(MapUtils.merge(values, builtinValues, ConflictResolutionPolicy.OVERRIDE),
                false);
    }

    @Override
    public Set<String> placeholder() {
        return Collections.unmodifiableSet(this.placeholder);
    }
}