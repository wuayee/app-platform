/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jane.meta.definition;

import static com.huawei.fitframework.util.ObjectUtils.nullIf;

import com.huawei.fit.jane.Undefinable;
import com.huawei.fit.jane.meta.property.MetaTemplatePropertyDeclarationInfo;

import java.util.List;

/**
 * 表示metaTemplate声明信息结构体。
 *
 * @author 陈镕希
 * @since 2024-02-04
 */
public class MetaTemplateDeclarationInfo {
    private Undefinable<String> name;
    private Undefinable<String> description;
    private Undefinable<List<MetaTemplatePropertyDeclarationInfo>> properties;

    public MetaTemplateDeclarationInfo() {
        this(null, null, null);
    }

    public MetaTemplateDeclarationInfo(Undefinable<String> name, Undefinable<String> description,
            Undefinable<List<MetaTemplatePropertyDeclarationInfo>> properties) {
        this.name = nullIf(name, Undefinable.undefined());
        this.description = nullIf(description, Undefinable.undefined());
        this.properties = nullIf(properties, Undefinable.undefined());
    }

    public Undefinable<String> getName() {
        return name;
    }

    public void setName(Undefinable<String> name) {
        this.name = nullIf(name, Undefinable.undefined());
    }

    public Undefinable<String> getDescription() {
        return description;
    }

    public void setDescription(Undefinable<String> description) {
        this.description = nullIf(description, Undefinable.undefined());
    }

    public Undefinable<List<MetaTemplatePropertyDeclarationInfo>> getProperties() {
        return properties;
    }

    public void setProperties(Undefinable<List<MetaTemplatePropertyDeclarationInfo>> properties) {
        this.properties = nullIf(properties, Undefinable.undefined());
    }
}
