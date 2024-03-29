/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fitframework.type.support;

class TypeNode {
    private final Class<?> type;
    private final TypeNode parent;

    private TypeNode(Class<?> type, TypeNode parent) {
        this.parent = parent;
        this.type = type;
    }
}
