/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.declaration;

import com.huawei.fit.jane.task.util.UndefinableValue;

/**
 * 实例构造器
 *
 * @author 陈镕希 c00572808
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
