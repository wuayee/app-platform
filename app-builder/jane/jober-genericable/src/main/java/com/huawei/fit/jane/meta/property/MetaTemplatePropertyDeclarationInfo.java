/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jane.meta.property;

import static modelengine.fitframework.util.ObjectUtils.nullIf;

import com.huawei.fit.jane.Undefinable;

/**
 * MetaTemplate属性声明信息结构体。
 *
 * @author 陈镕希
 * @since 2024-02-04
 */
public class MetaTemplatePropertyDeclarationInfo {
    private Undefinable<String> id;
    private Undefinable<String> name;
    private Undefinable<String> dataType;

    public MetaTemplatePropertyDeclarationInfo() {
        this(null, null, null);
    }

    public MetaTemplatePropertyDeclarationInfo(Undefinable<String> id, Undefinable<String> name,
            Undefinable<String> dataType) {
        this.id = nullIf(id, Undefinable.undefined());
        this.name = nullIf(name, Undefinable.undefined());
        this.dataType = nullIf(dataType, Undefinable.undefined());
    }

    public Undefinable<String> getId() {
        return id;
    }

    public void setId(Undefinable<String> id) {
        this.id = nullIf(id, Undefinable.undefined());
    }

    public Undefinable<String> getName() {
        return name;
    }

    public void setName(Undefinable<String> name) {
        this.name = nullIf(name, Undefinable.undefined());
    }

    public Undefinable<String> getDataType() {
        return dataType;
    }

    public void setDataType(Undefinable<String> dataType) {
        this.dataType = nullIf(dataType, Undefinable.undefined());
    }
}
