/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.store.service;

import com.huawei.jade.store.ItemInfo;

import java.util.Map;
import java.util.Set;

/**
 * 表示商品的数据内容。
 *
 * @author 鲁为 l00839724
 * @since 2024-04-18
 */
public class ItemData {
    /**
     * 表示商品的分类。
     * <p>添加商品时需传入，查询商品时会返回。</p>
     */
    private String category;

    /**
     * 表示商品的分组。
     * <p>添加商品时需传入，查询商品时会返回。</p>
     */
    private String group;

    /**
     * 表示商品的名字。
     * <p>添加商品时需传入，查询商品时会返回。</p>
     */
    private String name;

    /**
     * 表示商品的描述。
     * <p>添加商品时可不传入，查询商品时会返回。</p>
     */
    private String description;

    /**
     * 表示商品的唯一标识。
     * <p>添加商品时不需要设置，查询商品时会返回。</p>
     */
    private String uniqueName;

    /**
     * 表示商品的格式规范。
     * <p>添加商品时需传入，查询商品时会返回。</p>
     */
    private Map<String, Object> schema;

    /**
     * 表示商品的来源。
     * <p>添加商品时可不传入，查询商品时会返回。</p>
     */
    private String source;

    /**
     * 表示商品的标签集合。
     * <p>添加商品时可不传入，查询商品时会返回。</p>
     */
    private Set<String> tags;

    /**
     * 获取商品的分类。
     *
     * @return 表示商品的分类的 {@link String}。
     */
    public String getCategory() {
        return this.category;
    }

    /**
     * 设置商品的分类。
     *
     * @param category 表示待设置的商品分类的 {@link String}。
     */
    public void setCategory(String category) {
        this.category = category;
    }

    /**
     * 获取商品的分组。
     *
     * @return 表示商品的分组的 {@link String}。
     */
    public String getGroup() {
        return this.group;
    }

    /**
     * 设置商品的分组。
     *
     * @param group 表示待设置的商品分组的 {@link String}。
     */
    public void setGroup(String group) {
        this.group = group;
    }

    /**
     * 获取商品的名字。
     *
     * @return 表示商品名字的 {@link String}。
     */
    public String getName() {
        return this.name;
    }

    /**
     * 设置商品的名字。
     *
     * @param name 表示商品名字的 {@link String}。
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取商品的描述。
     *
     * @return 表示商品描述的 {@link String}。
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * 设置商品的描述。
     *
     * @param description 表示待设置的商品描述的 {@link String}。
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * 获取商品的唯一标识。
     *
     * @return 表示商品的唯一标识的 {@link String}。
     */
    public String getUniqueName() {
        return this.uniqueName;
    }

    /**
     * 设置商品的唯一标识。
     *
     * @param uniqueName 表示待设置的商品唯一标识的 {@link String}。
     */
    public void setUniqueName(String uniqueName) {
        this.uniqueName = uniqueName;
    }

    /**
     * 获取商品的格式规范。
     *
     * @return 表示商品格式规范的 {@link Map}{@code <}{@link String}{@code , }{@link Object}{@code >}。
     */
    public Map<String, Object> getSchema() {
        return this.schema;
    }

    /**
     * 设置商品的格式规范。
     *
     * @param schema 表示待设置的商品格式规范的 {@link Map}{@code <}{@link String}{@code , }{@link Object}{@code >}。
     */
    public void setSchema(Map<String, Object> schema) {
        this.schema = schema;
    }

    /**
     * 获取商品的来源。
     *
     * @return 表示商品来源的 {@link String}。
     */
    public String getSource() {
        return this.source;
    }

    /**
     * 设置商品的来源。
     *
     * @param source 表示待设置的商品来源的 {@link String}。
     */
    public void setSource(String source) {
        this.source = source;
    }

    /**
     * 获取商品的标签集合。
     *
     * @return 表示商品的标签集合的 {@link Set}{@code <}{@link String}{@code >}。
     */
    public Set<String> getTags() {
        return this.tags;
    }

    /**
     * 设置商品的标签集合。
     *
     * @param tags 表示待设置的商品标签集合的 {@link Set}{@code <}{@link String}{@code >}。
     */
    public void setTags(Set<String> tags) {
        this.tags = tags;
    }

    /**
     * 将商品信息转换为 {@link ItemData}。
     *
     * @param itemInfo 表示商品信息的 {@link ItemInfo}。
     * @return 表示商品信息的数据内容的 {@link ItemData}。
     */
    public static ItemData from(ItemInfo itemInfo) {
        ItemData itemData = new ItemData();
        itemData.setCategory(itemInfo.category());
        itemData.setGroup(itemInfo.group());
        itemData.setName(itemInfo.name());
        itemData.setUniqueName(itemInfo.uniqueName());
        itemData.setTags(itemInfo.tags());
        itemData.setSchema(itemInfo.schema());
        itemData.setDescription(itemInfo.description());
        return itemData;
    }

    /**
     * 将商品信息的数据内容转换为商品信息。
     *
     * @param itemData 表示商品信息的传输对象的 {@link ItemData}。
     * @return 表示商品信息的 {@link ItemInfo}。
     */
    public static ItemInfo convertToItemInfo(ItemData itemData) {
        return ItemInfo.custom()
                .category(itemData.getCategory())
                .group(itemData.getGroup())
                .name(itemData.getName())
                .uniqueName(itemData.getUniqueName())
                .tags(itemData.getTags())
                .description(itemData.getDescription())
                .schema(itemData.getSchema())
                .build();
    }
}
