/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.taskcenter.domain.util.support;

import static modelengine.fitframework.util.ObjectUtils.nullIf;

import modelengine.fit.jober.taskcenter.domain.util.PrimaryValue;
import modelengine.fit.jober.taskcenter.domain.util.TaskInstanceRow;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

/**
 * 为 {@link PrimaryValue} 提供默认实现。
 *
 * @author 梁济时
 * @since 2023-10-28
 */
public class DefaultPrimaryValue implements PrimaryValue {
    /**
     * 空值
     */
    public static final PrimaryValue EMPTY = new DefaultPrimaryValue(Collections.emptyMap());

    private final Map<String, Object> values;

    /**
     * 构造函数
     *
     * @param values 值
     */
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
