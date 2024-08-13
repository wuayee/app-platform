/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.domain.util.filter;

import static com.huawei.fitframework.util.ObjectUtils.cast;

import com.huawei.fit.jane.task.domain.PropertyDataType;
import com.huawei.fit.jober.taskcenter.domain.util.Filter;
import com.huawei.fit.jober.taskcenter.domain.util.FilterParser;
import com.huawei.fitframework.jvm.scan.PackageScanner;
import com.huawei.fitframework.util.ParsingResult;
import com.huawei.fitframework.util.StringUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 为 {@link FilterParser} 提供默认实现。
 *
 * @author 梁济时
 * @since 2024-01-12
 */
public class DefaultFilterParser implements FilterParser {
    /**
     * 获取当前类型的唯一实例。
     */
    public static final DefaultFilterParser INSTANCE = new DefaultFilterParser();

    private final Map<String, FilterParser> parsers;

    private DefaultFilterParser() {
        this.parsers = new HashMap<>();
        ClassLoader loader = DefaultFilterParser.class.getClassLoader();
        PackageScanner scanner = PackageScanner.forClassLoader(loader, this::onClassDetected);
        String packageName = DefaultFilterParser.class.getPackage().getName();
        scanner.scan(Collections.singleton(packageName));
    }

    private void onClassDetected(PackageScanner scanner, Class<?> clazz) {
        if (clazz.isInterface() || Modifier.isAbstract(clazz.getModifiers())
                || !FilterParser.class.isAssignableFrom(clazz)) {
            return;
        }
        FilterParser.Declare declaration = clazz.getAnnotation(FilterParser.Declare.class);
        if (declaration == null) {
            return;
        }
        Constructor<?> constructor;
        try {
            constructor = clazz.getDeclaredConstructor();
        } catch (NoSuchMethodException e) {
            return;
        }
        String key = StringUtils.trim(declaration.value());
        if (StringUtils.isEmpty(key)) {
            throw new IllegalStateException(StringUtils.format(
                    "The key of a filter parser cannot be blank. [class={0}]", clazz.getName()));
        }
        key = StringUtils.toLowerCase(key);
        if (this.parsers.containsKey(key)) {
            throw new IllegalStateException(StringUtils.format(
                    "Duplicate key of filter parsers. [class1={0}, class2={1}]",
                    clazz.getName(), this.parsers.get(key).getClass().getName()));
        }
        FilterParser parser;
        try {
            parser = cast(constructor.newInstance());
        } catch (InvocationTargetException ex) {
            Throwable cause = ex.getTargetException();
            throw new IllegalStateException(StringUtils.format(
                    "Failed to instantiate filter parser. [class={0}]", clazz.getName()), cause);
        } catch (Exception ex) {
            throw new IllegalStateException(StringUtils.format(
                    "Failed to instantiate filter parser. [class={0}]", clazz.getName()), ex);
        }
        this.parsers.put(key, parser);
    }

    @Override
    public Filter parse(PropertyDataType dataType, String text) {
        String actual = StringUtils.trim(text);
        if (StringUtils.isEmpty(actual) || actual.charAt(actual.length() - 1) != ')') {
            return this.createDefaultFilter(dataType, text);
        }
        int index = actual.indexOf('(');
        if (index < 1) {
            return this.createDefaultFilter(dataType, text);
        }
        String key = StringUtils.trim(actual.substring(0, index));
        if (StringUtils.isEmpty(key)) {
            return this.createDefaultFilter(dataType, text);
        }
        FilterParser parser = this.parsers.get(StringUtils.toLowerCase(key));
        if (parser == null) {
            return this.createDefaultFilter(dataType, text);
        }
        String value = actual.substring(index + 1, actual.length() - 1);
        Filter filter = parser.parse(dataType, value);
        if (filter == null) {
            filter = new EqualsFilter(text);
        }
        return filter;
    }

    private Filter createDefaultFilter(PropertyDataType dataType, String text) {
        if (dataType == PropertyDataType.TEXT) {
            return new ContainsFilter(text);
        }
        ParsingResult<Object> value = dataType.parse(text);
        if (!value.isParsed()) {
            return Filter.alwaysFalse();
        }
        return new EqualsFilter(value.getResult());
    }
}
