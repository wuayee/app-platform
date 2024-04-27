/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.store.service;

import com.huawei.jade.store.ItemInfo;

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

    public ItemDto setCategory(String category) {
        this.category = category;
        return this;
    }

    public String getGroup() {
        return this.group;
    }

    public ItemDto setGroup(String group) {
        this.group = group;
        return this;
    }

    public String getName() {
        return this.name;
    }

    public ItemDto setName(String name) {
        this.name = name;
        return this;
    }

    public String getDescription() {
        return this.description;
    }

    public ItemDto setDescription(String description) {
        this.description = description;
        return this;
    }

    public String getUniqueName() {
        return this.uniqueName;
    }

    public ItemDto setUniqueName(String uniqueName) {
        this.uniqueName = uniqueName;
        return this;
    }

    public Map<String, Object> getSchema() {
        return this.schema;
    }

    public ItemDto setSchema(Map<String, Object> schema) {
        this.schema = schema;
        return this;
    }

    public String getSource() {
        return this.source;
    }

    public ItemDto setSource(String source) {
        this.source = source;
        return this;
    }

    public Set<String> getTags() {
        return this.tags;
    }

    public ItemDto setTags(Set<String> tags) {
        this.tags = tags;
        return this;
    }

    /**
     * 将商品信息转换为 {@link ItemDto}。
     *
     * @param itemInfo 表示商品信息的 {@link ItemInfo}。
     * @return 表示商品信息的传输对象的 {@link ItemDto}。
     */
    public static ItemDto from(ItemInfo itemInfo) {
        return new ItemDto().setCategory(itemInfo.category())
                .setGroup(itemInfo.group())
                .setName(itemInfo.name())
                .setUniqueName(itemInfo.uniqueName())
                .setTags(itemInfo.tags())
                .setSchema(itemInfo.schema())
                .setDescription(itemInfo.description());
    }

    /**
     * 将商品信息的传输对象转换为商品信息。
     *
     * @param itemDto 表示商品信息的传输对象的 {@link ItemDto}。
     * @return 表示商品信息的 {@link ItemInfo}。
     */
    public static ItemInfo convertToItemInfo(ItemDto itemDto) {
        return ItemInfo.custom()
                .category(itemDto.getCategory())
                .group(itemDto.getGroup())
                .name(itemDto.getName())
                .uniqueName(itemDto.getUniqueName())
                .tags(itemDto.getTags())
                .description(itemDto.getDescription())
                .schema(itemDto.getSchema())
                .build();
    }
}
