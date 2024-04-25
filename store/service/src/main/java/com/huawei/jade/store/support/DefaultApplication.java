/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.store.support;

import com.huawei.jade.store.Application;
import com.huawei.jade.store.ItemInfo;

/**
 * 表示应用的默认实现。
 *
 * @author 王攀博
 * @since 2024-04-24
 */
public class DefaultApplication extends AbstractItem implements Application {
    public DefaultApplication(ItemInfo itemInfo) {
        super(itemInfo);
    }
}