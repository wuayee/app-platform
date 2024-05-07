/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.store.mybatis;

import com.huawei.fitframework.exception.FitException;
import com.huawei.jade.store.ItemInfo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.sql.Timestamp;
import java.util.Map;

/**
 * 存入数据库的商品的实体类。
 *
 * @author 鲁为 l00839724
 * @since 2024-04-18
 */
public class ItemDo {
    /**
     * 表示商品的自增主键。
     * <p>添加商品时不需传入。</p>
     * <p>查询商品时不会返回。</p>
     */
    private long id;

    /**
     * 表示商品的创建时间。
     * <p>添加商品时不需传入。</p>
     * <p>查询商品时不会返回。</p>
     */
    private Timestamp createdTime;

    /**
     * 表示商品的更新时间。
     * <p>添加商品时不需传入。</p>
     * <p>查询商品时不会返回。</p>
     */
    private Timestamp updatedTime;

    /**
     * 表示商品的创建者。
     * <p>添加商品时不需传入。</p>
     * <p>查询商品时不会返回。</p>
     */
    private String creator;

    /**
     * 表示商品的修改者。
     * <p>添加商品时不需传入。</p>
     * <p>查询商品时不会返回。</p>
     */
    private String modifier;

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
    private String schema;

    /**
     * 表示商品的来源。
     * <p>添加商品时需传入。</p>
     * <p>查询商品时会返回。</p>
     */
    private String source;

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Timestamp getCreatedTime() {
        return this.createdTime;
    }

    public void setCreatedTime(Timestamp createdTime) {
        this.createdTime = createdTime;
    }

    public Timestamp getUpdatedTime() {
        return this.updatedTime;
    }

    public void setUpdatedTime(Timestamp updatedTime) {
        this.updatedTime = updatedTime;
    }

    public String getCreator() {
        return this.creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getModifier() {
        return this.modifier;
    }

    public void setModifier(String modifier) {
        this.modifier = modifier;
    }

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

    public String getUniqueName() {
        return this.uniqueName;
    }

    public void setUniqueName(String uniqueName) {
        this.uniqueName = uniqueName;
    }

    public String getSchema() {
        return this.schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public String getSource() {
        return this.source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    /**
     * 将领域类转换为数据对象实体类。
     *
     * @param itemInfo 表示领域类的 {@link ItemInfo}。
     * @return 数据对象实体类的 {@link ItemDo}。
     */
    public static ItemDo from(ItemInfo itemInfo) {
        ItemDo itemDo = new ItemDo();
        itemDo.setCategory(itemInfo.category());
        itemDo.setGroup(itemInfo.group());
        itemDo.setName(itemInfo.name());
        itemDo.setUniqueName(itemInfo.uniqueName());
        itemDo.setSchema(obj2Json(itemInfo.schema()));
        return itemDo;
    }

    /**
     * 序列化。
     *
     * @param item 表示待序列化对象的 {@link Map}{@code <}{@link String}{@code ,}{@link Object}{@code >}。
     * @return 序列化后的字符串的 {@link String}。
     */
    public static String obj2Json(Map<String, Object> item) {
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = null;
        try {
            jsonString = objectMapper.writeValueAsString(item);
        } catch (JsonProcessingException e) {
            throw new FitException(e);
        }
        return jsonString;
    }
}
