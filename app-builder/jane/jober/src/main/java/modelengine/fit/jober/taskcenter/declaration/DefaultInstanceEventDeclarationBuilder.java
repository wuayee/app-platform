/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.taskcenter.declaration;

import modelengine.fit.jane.task.util.UndefinableValue;

/**
 * 实例构造器
 *
 * @author 陈镕希
 * @since 2023-10-30
 */
class DefaultInstanceEventDeclarationBuilder implements InstanceEventDeclaration.Builder {
    private UndefinableValue<String> type;

    private UndefinableValue<String> fitableId;

    DefaultInstanceEventDeclarationBuilder() {
        this.type = UndefinableValue.undefined();
        this.fitableId = UndefinableValue.undefined();
    }

    @Override
    public InstanceEventDeclaration.Builder type(String type) {
        this.type = UndefinableValue.defined(type);
        return this;
    }

    @Override
    public InstanceEventDeclaration.Builder fitableId(String fitableId) {
        this.fitableId = UndefinableValue.defined(fitableId);
        return this;
    }

    @Override
    public InstanceEventDeclaration build() {
        return new DefaultInstanceEventDeclaration(this.type, this.fitableId);
    }
}
