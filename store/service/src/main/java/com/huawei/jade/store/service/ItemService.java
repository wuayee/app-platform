/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.store.service;

import com.huawei.fitframework.annotation.Genericable;

import java.util.List;

/**
 * 提供商品的通用服务。
 *
 * @author 鲁为 l00839724
 * @since 2024-04-16
 */
public interface ItemService {
    /**
     * 添加商品标签。
     *
     * @param itemUniqueName 表示商品的唯一标识的 {@link String}。
     * @param tag 表示待添加的商品标签的 {@link String}。
     */
    @Genericable(id = "com.huawei.jade.store.service.addTag")
    void addItemTag(String itemUniqueName, String tag);

    /**
     * 删除商品标签。
     *
     * @param itemUniqueName 表示商品的唯一标识的 {@link String}。
     * @param tag 表示待删除的商品标签的 {@link String}。
     */
    @Genericable(id = "com.huawei.jade.store.service.deleteTag")
    void deleteItemTag(String itemUniqueName, String tag);

    /**
     * 添加商品。
     *
     * @param item 表示待增加的商品信息的 {@link ItemData}。
     * @return 表示添加后的商品的唯一标识的 {@link String}。
     */
    @Genericable(id = "com.huawei.jade.store.service.addItem")
    String addItem(ItemData item);

    /**
     * 删除商品。
     *
     * @param itemUniqueName 表示待删除商品唯一标识的 {@link String}。
     */
    @Genericable(id = "com.huawei.jade.store.service.deleteItem")
    void deleteItem(String itemUniqueName);

    /**
     * 基于商品的唯一标识查询某个商品。
     *
     * @param itemUniqueName 表示商品的唯一标识的 {@link String}。
     * @return 表示商品详细信息的 {@link ItemData}。
     */
    @Genericable(id = "com.huawei.jade.store.service.getItem.byItemUniqueName")
    ItemData getItem(String itemUniqueName);

    /**
     * 根据商品分类和标签查询所有的商品信息。
     *
     * @param category 表示商品分类的 {@link String}。
     * @param includeTags 表示要包括的标签的列表的 {@link List}{@code <}{@link String}{@code >}。
     * @param excludeTags 表示不包括的标签的列表的 {@link List}{@code <}{@link String}{@code >}。
     * @param offset 表示分页查询的偏移量的 {@code int}。
     * @param limit 表示分页查询的数量限制的 {@code int}。
     * @return 表示所有商品详细信息的列表的 {@link List}{@code <}{@link ItemData}{@code >}。
     */
    @Genericable(id = "com.huawei.jade.store.service.getAllItems")
    List<ItemData> getAllItems(String category, List<String> includeTags, List<String> excludeTags, int offset,
            int limit);

    /**
     * 根据商品分类、分组、名字和标签查询某个商品。
     *
     * @param category 表示商品分类的 {@link String}。
     * @param group 表示商品分组的 {@link String}。
     * @param name 表示商品名字的 {@link String}。
     * @param includeTags 表示要包括的标签的列表的 {@link List}{@code <}{@link String}{@code >}。
     * @param excludeTags 表示不包括的标签的列表的 {@link List}{@code <}{@link String}{@code >}。
     * @return 表示商品详细信息的 {@link ItemData}。
     */
    @Genericable(id = "com.huawei.jade.store.service.getItem.byName")
    ItemData getItem(String category, String group, String name, List<String> includeTags, List<String> excludeTags);

    /**
     * 根据商品分类、分组和标签查询所有商品。
     *
     * @param category 表示商品分类的 {@link String}。
     * @param group 表示商品分组的 {@link String}。
     * @param includeTags 表示要包括的标签的列表的 {@link List}{@code <}{@link String}{@code >}。
     * @param excludeTags 表示不包括的标签的列表的 {@link List}{@code <}{@link String}{@code >}。
     * @param offset 表示分页查询的偏移量的 {@code int}。
     * @param limit 表示分页查询的数量限制的 {@code int}。
     * @return 表示所有商品详细信息的列表的 {@link List}{@code <}{@link ItemData}{@code >}。
     */
    @Genericable(id = "com.huawei.jade.store.service.getItems")
    List<ItemData> getItems(String category, String group, List<String> includeTags, List<String> excludeTags,
            int offset, int limit);
}
