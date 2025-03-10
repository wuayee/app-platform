/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.taskcenter.declaration;

import modelengine.fit.jane.task.util.UndefinableValue;
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
