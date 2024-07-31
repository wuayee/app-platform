/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jane.meta.multiversion.definition;

import static com.huawei.fitframework.util.ObjectUtils.nullIf;

import com.huawei.fit.jane.Undefinable;
import com.huawei.fit.jane.meta.property.MetaPropertyDeclarationInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 表示meta声明信息结构体。
 *
 * @author 陈镕希 c00572808
 * @since 2023-12-08
 */
public class MetaDeclarationInfo {
    private Undefinable<String> name;

    private Undefinable<String> category;

    private Undefinable<Map<String, Object>> attributes;

    /**
     * 基础模板Id，用于继承，创建Meta时使用，若无需继承模板，则可以不传入
     */
    private Undefinable<String> basicMetaTemplateId;

    private Undefinable<List<MetaPropertyDeclarationInfo>> properties;

    private Undefinable<String> version;

    public MetaDeclarationInfo() {
        this(null, null, null, null, null, null);
    }

    public MetaDeclarationInfo(Undefinable<String> name, Undefinable<String> category,
            Undefinable<Map<String, Object>> attributes, Undefinable<String> basicMetaTemplateId,
            Undefinable<List<MetaPropertyDeclarationInfo>> properties, Undefinable<String> version) {
        this.name = nullIf(name, Undefinable.undefined());
        this.category = nullIf(category, Undefinable.undefined());
        this.attributes = nullIf(attributes, Undefinable.undefined());
        this.basicMetaTemplateId = nullIf(basicMetaTemplateId, Undefinable.undefined());
        this.properties = nullIf(properties, Undefinable.undefined());
        this.version = nullIf(version, Undefinable.undefined());
    }

    public Undefinable<String> getName() {
        return name;
    }

    public void setName(Undefinable<String> name) {
        this.name = nullIf(name, Undefinable.undefined());
    }

    public Undefinable<String> getCategory() {
        return category;
    }

    public void setCategory(Undefinable<String> category) {
        this.category = nullIf(category, Undefinable.undefined());
    }

    public Undefinable<Map<String, Object>> getAttributes() {
        return attributes;
    }

    public void setAttributes(Undefinable<Map<String, Object>> attributes) {
        this.attributes = nullIf(attributes, Undefinable.undefined());
    }

    /**
     * 设置属性
     *
     * @param key key
     * @param value value
     */
    public void putAttribute(String key, Object value) {
        if (this.attributes.getDefined()) {
            this.attributes.getValue().put(key, value);
            return;
        }
        this.attributes = Undefinable.defined(new HashMap<String, Object>() {
            {
            put(key, value);
        }});
    }

    public Undefinable<String> getBasicMetaTemplateId() {
        return basicMetaTemplateId;
    }

    public void setBasicMetaTemplateId(Undefinable<String> basicMetaTemplateId) {
        this.basicMetaTemplateId = nullIf(basicMetaTemplateId, Undefinable.undefined());
    }

    public Undefinable<List<MetaPropertyDeclarationInfo>> getProperties() {
        return properties;
    }

    public void setProperties(Undefinable<List<MetaPropertyDeclarationInfo>> properties) {
        this.properties = nullIf(properties, Undefinable.undefined());
    }

    public Undefinable<String> getVersion() {
        return version;
    }

    public void setVersion(Undefinable<String> version) {
        this.version = nullIf(version, Undefinable.undefined());
    }
}
