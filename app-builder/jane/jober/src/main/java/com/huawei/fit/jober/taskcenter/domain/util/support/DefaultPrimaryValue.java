/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.domain.util.support;

import static com.huawei.fitframework.util.ObjectUtils.nullIf;

import com.huawei.fit.jober.taskcenter.domain.util.PrimaryValue;
import com.huawei.fit.jober.taskcenter.domain.util.TaskInstanceRow;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

/**
 * 为 {@link PrimaryValue} 提供默认实现。
 *
 * @author 梁济时 l00815032
 * @since 2023-10-28
 */
public class DefaultPrimaryValue implements PrimaryValue {
    public static final PrimaryValue EMPTY = new DefaultPrimaryValue(Collections.emptyMap());

    private final Map<String, Object> values;

    public DefaultPrimaryValue(Map<String, Object> values) {
        this.values = nullIf(values, Collections.emptyMap());
    }

    @Override
    public Map<String, Object> values() {
        return this.values;
    }

    @Override
    public boolean isEmpty() {
        return this.values.isEmpty();
    }

    @Override
    public boolean match(TaskInstanceRow object) {
        for (Map.Entry<String, Object> primaryValue : this.values.entrySet()) {
            Object expectedValue = primaryValue.getValue();
            Object actualValue = object.info().get(primaryValue.getKey());
            if (!Objects.equals(expectedValue, actualValue)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj instanceof DefaultPrimaryValue) {
            DefaultPrimaryValue another = (DefaultPrimaryValue) obj;
            return this.values.equals(another.values);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return this.values.hashCode();
    }

    @Override
    public String toString() {
        return this.values.toString();
    }
}
