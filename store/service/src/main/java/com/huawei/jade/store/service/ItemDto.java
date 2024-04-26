/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.store.service;

import java.util.Map;
import java.util.Set;

/**
 * 存入数据库的工具的实体类。
 *
 * @author 鲁为 l00839724
 * @since 2024/4/18
 */
public class ItemDto {
    /**
     * 表示商品的分类。
     * <p>添加商品时需传入。</p>
     * <p>查询商品时会返回。</p>
     */
    private String category;

    /**
     * 表示商品的分组。
     * <p>添加商品时需传入。</p>
     * <p>查询商品时会返回。</p>
     */
    private String group;

    /**
     * 表示商品的名字。
     * <p>添加商品时需传入。</p>
     * <p>查询商品时会返回。</p>
     */
    private String name;

    /**
     * 表示商品的描述。
     * <p>添加商品时不需要传入。</p>
     * <p>查询商品时会返回。</p>
     */
    private String description;

    /**
     * 表示商品的唯一标识。
     * <p>添加商品时为空</p>
     * <p>查询商品时会返回</p>
     */
    private String uniqueName;

    /**
     * 表示商品的结构。
     * <p>添加商品时需传入。</p>
     * <p>查询商品时会返回。</p>
     */
    private Map<String, Object> schema;

    /**
     * 表示商品的来源。
     * <p>添加商品时需传入。</p>
     * <p>查询商品时会返回。</p>
     */
    private String source;

    /**
     * 表示商品的标签。
     * <p>添加商品时需传入。</p>
     * <p>查询商品时会返回。</p>
     */
    private Set<String> tags;

    public String getCategory() {
        return this.category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getGroup() {
        return this.group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUniqueName() {
        return this.uniqueName;
    }

    public void setUniqueName(String uniqueName) {
        this.uniqueName = uniqueName;
    }

    public Map<String, Object> getSchema() {
        return this.schema;
    }

    public void setSchema(Map<String, Object> schema) {
        this.schema = schema;
    }

    public String getSource() {
        return this.source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Set<String> getTags() {
        return this.tags;
    }

    public void setTags(Set<String> tags) {
        this.tags = tags;
    }
}
