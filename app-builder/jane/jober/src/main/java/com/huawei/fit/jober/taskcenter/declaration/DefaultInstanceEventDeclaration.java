/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.declaration;

import com.huawei.fit.jane.task.util.UndefinableValue;
import modelengine.fitframework.util.StringUtils;

import java.util.Arrays;
import java.util.Objects;

/**
 * 默认实例事件声明
 *
 * @author 陈镕希
 * @since 2023-10-30
 */
class DefaultInstanceEventDeclaration implements InstanceEventDeclaration {
    private final UndefinableValue<String> type;

    private final UndefinableValue<String> fitableId;

    DefaultInstanceEventDeclaration(UndefinableValue<String> type, UndefinableValue<String> fitableId) {
        this.type = type;
        this.fitableId = fitableId;
    }

    @Override
    public UndefinableValue<String> type() {
        return this.type;
    }

    @Override
    public UndefinableValue<String> fitableId() {
        return this.fitableId;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj instanceof DefaultInstanceEventDeclaration) {
            DefaultInstanceEventDeclaration another = (DefaultInstanceEventDeclaration) obj;
            return Objects.equals(this.type, another.type) && Objects.equals(this.fitableId, another.fitableId);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(new Object[] {this.getClass(), this.type, this.fitableId});
    }

    @Override
    public String toString() {
        return StringUtils.format("[type={0}, fitable={1}]", this.type, this.fitableId);
    }
}
