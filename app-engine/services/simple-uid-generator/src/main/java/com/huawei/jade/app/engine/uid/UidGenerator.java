/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.uid;

import com.huawei.fitframework.annotation.Genericable;

/**
 * 全局唯一 ID 生成器接口定义。
 *
 * @author 易文渊
 * @since 2024-07-29
 */
public interface UidGenerator {
    /**
     * 获取全局唯一顺序递增 ID。
     *
     * @return 表示全局唯一 ID 的 {@code long}。
     */
    @Genericable("com.huawei.jade.app.engine.uid.get")
    long getUid();
}