/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.store.support;

import static com.huawei.fitframework.inspection.Validation.notNull;

import com.huawei.jade.store.Item;
import com.huawei.jade.store.ItemInfo;

/**
 * 表示 {@link Item} 的抽象实现类。
 *
 * @author 季聿阶
 * @since 2024-04-25
 */
public abstract class AbstractItem implements Item {
    private final ItemInfo itemInfo;

    /**
     * 通过商品的基本信息来初始化 {@link AbstractItem} 的新实例。
     *
     * @param itemInfo 表示商品的基本信息的 {@link ItemInfo}。
     */
    protected AbstractItem(ItemInfo itemInfo) {
        this.itemInfo = notNull(itemInfo, "The item info cannot be null.");
    }

    @Override
    public ItemInfo itemInfo() {
        return this.itemInfo;
    }
}
