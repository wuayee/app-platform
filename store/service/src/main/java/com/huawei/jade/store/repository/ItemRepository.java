/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.store.repository;

import com.huawei.jade.store.Item;
import com.huawei.jade.store.ItemInfo;

import java.util.List;
import java.util.Optional;

/**
 * 存入数据库的工具的实体类。
 *
 * @author 王攀博
 * @since 2024-04-27
 */
public interface ItemRepository {
    /**
     * 添加商品标签。
     *
     * @param itemUniqueName 表示商品的唯一标识的 {@link String}。
     * @param tag 表示商品的标签的 {@link String}。
     */
    void addItemTag(String itemUniqueName, String tag);

    /**
     * 删除商品标签。
     *
     * @param itemUniqueName 表示商品的唯一标识的 {@link String}。
     * @param tag 表示商品的标签的 {@link String}。
     */
    void deleteItemTag(String itemUniqueName, String tag);

    /**
     * 添加商品。
     *
     * @param itemInfo 表示待增加的商品信息的 {@link ItemInfo}。
     * @return 表示商品的唯一标识的 {@link String}。
     */
    String addItem(ItemInfo itemInfo);

    /**
     * 删除商品。
     *
     * @param itemUniqueName 表示待删除商品信息的 {@link String}。
     */
    void deleteItem(String itemUniqueName);

    /**
     * 基于商品的唯一标识查询某个商品。
     *
     * @param itemUniqueName 表示商品的唯一索引的 {@link String}。
     * @return 表示商品详细信息的 {@link Optional}{@code <}{@link Item}{@code >}。
     */
    Optional<Item> getItem(String itemUniqueName);

    /**
     * 根据商品分类和标签查询所有的商品信息。
     *
     * @param category 表示商品分类的 {@link String}。
     * @param includeTags 表示要包括的标签的列表的 {@link List}{@code <}{@link String}{@code >}。
     * @param excludeTags 表示不包括的标签的列表的 {@link List}{@code <}{@link String}{@code >}。
     * @param offset 表示分页查询的偏移量的 {@code int}。
     * @param limit 表示分页查询的数量限制的 {@code int}。
     * @return 表示所有商品详细信息的列表的 {@link List}{@code <}{@link Item}{@code >}。
     */
    List<Item> getAllItems(String category, List<String> includeTags, List<String> excludeTags, int offset, int limit);

    /**
     * 根据商品分类、分组、名字和标签查询某个商品。
     *
     * @param category 表示商品分类的 {@link String}。
     * @param group 表示商品分组的 {@link String}。
     * @param name 表示商品名字的 {@link String}。
     * @param includeTags 表示要包括的标签的列表的 {@link List}{@code <}{@link String}{@code >}。
     * @param excludeTags 表示不包括的标签的列表的 {@link List}{@code <}{@link String}{@code >}。
     * @return 表示商品详细信息的 {@link Optional}{@code <}{@link Item}{@code >}。
     */
    Optional<Item> getItem(String category, String group, String name, List<String> includeTags,
            List<String> excludeTags);

    /**
     * 根据商品分类、分组和标签查询所有商品。
     *
     * @param category 表示商品分类的 {@link String}。
     * @param group 表示商品分组的 {@link String}。
     * @param includeTags 表示要包括的标签的列表的 {@link List}{@code <}{@link String}{@code >}。
     * @param excludeTags 表示不包括的标签的列表的 {@link List}{@code <}{@link String}{@code >}。
     * @param offset 表示分页查询的偏移量的 {@code int}。
     * @param limit 表示分页查询的数量限制的 {@code int}。
     * @return 商品的列表的 {@link List}{@code <}{@link Item}{@code >}。
     */
    List<Item> getItems(String category, String group, List<String> includeTags, List<String> excludeTags, int offset,
            int limit);

    /**
     * 查询所有的组。
     *
     * @param category category 表示商品分类的 {@link String}。
     * @param tag 表示 FitTool 的标签的 {@link String}。
     * @param offset 表示分页查询的偏移量的 {@code int}。
     * @param limit 表示分页查询的数量限制的 {@code int}。
     * @return 所有组列表 {@link List}{@code <}{@link String}{@code >}。
     */
    List<String> getAllGroups(String category, String tag, int offset, int limit);

    /**
     * 根据商品分类、标签和分组查询所有商品信息。
     *
     * @param category 表示商品分类的 {@link String}。
     * @param tag 表示商品的标签的 {@link String}。
     * @param group 表示商品的分组的 {@link String}。
     * @param offset 表示分页查询偏移量的 {@code int}。
     * @param limit 表示分页查询数量限制的 {@code int}。
     * @return 所有商品详细信息的列表的 {@link List}{@code <}{@link Item}{@code >}。
     */
    List<Item> getItemsByGroup(String category, String tag, String group, int offset, int limit);
}
