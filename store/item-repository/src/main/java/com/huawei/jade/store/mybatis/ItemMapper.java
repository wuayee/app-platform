/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.store.mybatis;

import java.util.List;
import java.util.Map;

/**
 * 表示用于 MyBatis 持久层引用的接口。
 *
 * @author 鲁为 l00839724
 * @since 2024-04-17
 */
public interface ItemMapper {
    /**
     * 增加工具。
     *
     * @param item 表示待增加的工具信息的 {@link ItemDo}。
     */
    void addItem(ItemDo item);

    /**
     * 给工具增加标签。
     *
     * @param tag 表示标签名的 {@link String}。
     */
    void addTag(Tag tag);

    /**
     * 删除表单中工具。
     *
     * @param uniqueName 表示工具名的 {@link String}。
     */
    void deleteItem(String uniqueName);

    /**
     * 删除工具的标签。
     *
     * @param itemId 表示工具的数据库自增主键的 {@code long}。
     * @param tag 表示标签名的 {@link String}。
     */
    void deleteTag(long itemId, String tag);

    /**
     * 查询所有的 genericableIds。
     *
     * @param category category 表示商品分类的 {@link String}。
     * @param tag 表示 FitTool 的标签的 {@link String}。
     * @param offset 表示分页查询的偏移量的 {@code int}。
     * @param limit 表示分页查询的数量限制的 {@code int}。
     * @return 所有 FitTool 详细信息的列表的 {@link List}{@code <}{@link String}{@code >}。
     */
    List<String> getAllGenericableIds(String category, String tag, int offset, int limit);

    /**
     * 根据商品分类和标签查询所有的商品信息。
     *
     * @param mp 表示商品分类和标签的封装类的 {@link Map}{@code <}{@link String}{@code ,}{@link Object}{@code >}。
     * @return 所有商品详细信息的列表的 {@link List}{@code <}{@link ItemDo}{@code >}。
     */
    List<ItemDo> getAllItems(Map<String, Object> mp);

    /**
     * 根据商品分类、标签和分组查询所有商品信息。
     *
     * @param category 表示商品分类的 {@link String}。
     * @param tag 表示 FitTool 的标签的 {@link String}。
     * @param genericableId 表示 FitTool 的分组的 {@link String}。
     * @param offset 表示分页查询偏移量的 {@code int}。
     * @param limit 表示分页查询数量限制的 {@code int}。
     * @return 所有商品详细信息的列表的 {@link List}{@code <}{@link ItemDo}{@code >}。
     */
    List<ItemDo> getFitTools(String category, String tag, String genericableId, int offset, int limit);

    /**
     * 根据工具主键获取工具信息。
     *
     * @param uniqueName 表示工具主键的 {@link String}。
     * @return 表示工具信息的 {@link ItemDo}。
     */
    ItemDo getItem(String uniqueName);

    /**
     * 根据商品分类、分组、名字和标签查询某个商品。
     *
     * @param mp 表示商品分类、分组、名字和标签的封装类的 {@link Map}{@code <}{@link String}{@code ,}{@link Object}{@code >}。
     * @return 表示工具信息的 {@link ItemDo}。
     */
    ItemDo getItemByCategoryGroupNameTags(Map<String, Object> mp);

    /**
     * 根据分组查询工具。
     *
     * @param mp 表示商品分组的封装类的 {@link Map}{@code <}{@link String}{@code ,}{@link Object}{@code >}。
     * @return 所有商品详细信息的列表的 {@link List}{@code <}{@link ItemDo}{@code >}。
     */
    List<ItemDo> getItemsByGroup(Map<String, Object> mp);

    /**
     * 基于商品的唯一标识查询某个商品。
     *
     * @param uniqueName 表示商品的唯一标识的 {@link String}。
     * @return 表示工具信息的 {@link ItemDo}。
     */
    ItemDo getItemByUniqueName(String uniqueName);

    /**
     * 获取工具的所有标签。
     *
     * @param uniqueName 表示工具的数据库自增主键的 {@link String}。
     * @return 标签的列表的 {@link List}{@code <}{@link Tag}{@code >}。
     */
    List<Tag> getTags(String uniqueName);
}
