/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.util;

import static com.huawei.fitframework.util.ObjectUtils.nullIf;

import com.huawei.fitframework.parameterization.ParameterizedString;
import com.huawei.fitframework.parameterization.ParameterizedStringResolver;
import com.huawei.fitframework.parameterization.ResolvedParameter;
import com.huawei.fitframework.util.CollectionUtils;
import com.huawei.fitframework.util.StringUtils;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 为{@link ExecutableSql} 提供默认实现
 *
 * @author 陈镕希 c00572808
 * @since 2023-10-30
 */
class DefaultExecutableSql implements ExecutableSql {
    private final String sql;

    private final List<Object> args;

    DefaultExecutableSql(String sql, List<Object> args) {
        this.sql = nullIf(sql, StringUtils.EMPTY);
        this.args = nullIf(args, Collections.emptyList());
    }

    @Override
    public String sql() {
        return this.sql;
    }

    @Override
    public List<Object> args() {
        return this.args;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj instanceof DefaultExecutableSql) {
            DefaultExecutableSql another = (DefaultExecutableSql) obj;
            return this.sql.equals(another.sql) && CollectionUtils.equals(this.args, another.args);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        Object[] array = new Object[this.args.size() + 2];
        int index = 0;
        array[index++] = this.getClass();
        array[index++] = this.sql;
        for (Object arg : args) {
            array[index++] = arg;
        }
        return Arrays.hashCode(array);
    }

    @Override
    public String toString() {
        return StringUtils.format("[sql={0}, args={1}]", this.sql, this.args);
    }

    static ExecutableSql resolve(String parameterizedSql, Map<String, Object> args) {
        Map<String, Object> formatArgs = new HashMap<>(args.size());
        Map<String, List<Object>> argumentValues = new HashMap<>(args.size());
        for (Map.Entry<String, Object> entry : args.entrySet()) {
            List<Object> values = new LinkedList<>();
            fillValues(values, entry.getValue());
            StringBuilder builder = new StringBuilder();
            builder.append('?');
            for (int i = 1; i < values.size(); i++) {
                builder.append(", ?");
            }
            formatArgs.put(entry.getKey(), builder.toString());
            argumentValues.put(entry.getKey(), values);
        }
        ParameterizedStringResolver resolver = ParameterizedStringResolver.create("${", "}", '\0');
        ParameterizedString resolvedSql = resolver.resolve(parameterizedSql);
        String jdbcSql = resolvedSql.format(formatArgs);
        List<Object> jdbcArgs = resolvedSql.getParameters()
                .stream()
                .map(ResolvedParameter::getName)
                .map(argumentValues::get)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
        return new DefaultExecutableSql(jdbcSql, jdbcArgs);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static void fillValues(List<Object> values, Object value) {
        if (value == null) {
            values.add(null);
        } else if (value instanceof Iterable) {
            Iterable iterable = (Iterable) value;
            iterable.forEach(item -> fillValues(values, item));
        } else if (value instanceof Iterator) {
            Iterator iterator = (Iterator) value;
            while (iterator.hasNext()) {
                fillValues(values, iterator.next());
            }
        } else if (value.getClass().isArray()) {
            int length = Array.getLength(value);
            for (int i = 0; i < length; i++) {
                Object item = Array.get(value, i);
                fillValues(values, item);
            }
        } else {
            values.add(value);
        }
    }
}
