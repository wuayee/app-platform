/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.store;

/**
 * 表示商品。
 *
 * @author 王攀博
 * @since 2024-04-24
 */
public interface Item {
    /**
     * 获取商品信息。
     *
     * @return 表示商品信息的 {@link ItemInfo}。
     */
    ItemInfo itemInfo();
}
