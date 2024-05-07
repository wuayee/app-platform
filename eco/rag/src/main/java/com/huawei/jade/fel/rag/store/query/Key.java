/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.rag.store.query;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 表达式中的键。
 *
 * @since 2024-05-07
 */
@Getter
@AllArgsConstructor
public class Key implements Element {
    private String key;
}
