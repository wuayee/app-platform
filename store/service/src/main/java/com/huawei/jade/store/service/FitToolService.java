/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.store.service;

import com.huawei.fitframework.annotation.Genericable;

import java.util.List;

/**
 * 提供 FitTool 的增删查改服务。
 *
 * @author 鲁为 l00839724
 * @since 2024/4/25
 */
public interface FitToolService {
    /**
     * 增加 FitTool。
     *
     * @param item 表示待增加的商品信息的 {@link ItemDto}。
     * @return 增加的商品的唯一标识的 {@link String}。
     */
    @Genericable(id = "com.huawei.jade.store.service.addFitTool")
    String addFitTool(ItemDto item);

    /**
     * 查询所有的 genericableIds。
     *
     * @param offset 表示偏移量的 {@code int}。
     * @param limit 表示限制数量的 {@code int}。
     * @return 表示工具信息的 {@link List}{@code <}{@link String}{@code >}。
     */
    @Genericable(id = "com.huawei.jade.store.service.getAllGenericableIds")
    List<String> getAllGenericableIds(int offset, int limit);

    /**
     * 查询 genericableId 下的所有商品的 {@link ItemDto}。
     *
     * @param genericableId 表示泛服务的 Id 的 {@link String}。
     * @param offset 表示分页查询偏移量的 {@code int}。
     * @param limit 表示分页查询数量限制的 {@code int}。
     * @return 表示工具详细信息的 {@link List}{@code <}{@link ItemDto}{@code >}。
     */
    @Genericable(id = "com.huawei.jade.store.service.getFitTools")
    List<ItemDto> getFitTools(String genericableId, int offset, int limit);
}
