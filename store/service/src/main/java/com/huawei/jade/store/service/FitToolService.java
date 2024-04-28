/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.store.service;

import com.huawei.fitframework.annotation.Genericable;

import java.util.List;

/**
 * 提供 FIT 调用工具的基本服务。
 *
 * @author 鲁为 l00839724
 * @since 2024-04-25
 */
public interface FitToolService {
    /**
     * 添加一个 FIT 调用工具。
     *
     * @param item 表示待添加的商品信息的 {@link ItemData}。
     * @return 表示添加的商品的唯一标识的 {@link String}。
     */
    @Genericable(id = "com.huawei.jade.store.service.addFitTool")
    String addFitTool(ItemData item);

    /**
     * 查询所有的 FIT 调用工具的唯一标识列表。
     *
     * @param offset 表示分页查询偏移量的 {@code int}。
     * @param limit 表示分页查询数量限制的 {@code int}。
     * @return 表示所有满足条件的 FIT 调用工具的唯一标识列表的 {@link List}{@code <}{@link String}{@code >}。
     */
    @Genericable(id = "com.huawei.jade.store.service.getAllGenericableIds")
    List<String> getAllGenericableIds(int offset, int limit);

    /**
     * 查询 FIT 调用工具中指定唯一标识下的所有商品的 {@link ItemData}。
     *
     * @param genericableId 表示指定的 FIT 调用的唯一标识的 {@link String}。
     * @param offset 表示分页查询偏移量的 {@code int}。
     * @param limit 表示分页查询数量限制的 {@code int}。
     * @return 表示所有满足条件的 FIT 调用工具详细信息列表的 {@link List}{@code <}{@link ItemData}{@code >}。
     */
    @Genericable(id = "com.huawei.jade.store.service.getFitTools")
    List<ItemData> getFitTools(String genericableId, int offset, int limit);
}
