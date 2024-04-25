/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.store;

import com.huawei.jade.store.support.DefaultApplication;

/**
 * 表示大模型的应用。
 *
 * @author 王攀博
 * @since 2024-04-24
 */
public interface Application extends Item {
    /**
     * 表示大模型中的应用分类。
     */
    String CATEGORY = "Application";

    /**
     * 创建一个大模型应用。
     *
     * @param itemInfo 表示大模型应用的基本信息的 {@link ItemInfo}。
     * @return 表示创建出来的大模型应用的 {@link Application}。
     * @throws IllegalArgumentException 当 {@code itemInfo} 为 {@code null} 时。
     */
    static Application create(ItemInfo itemInfo) {
        return new DefaultApplication(itemInfo);
    }
}
