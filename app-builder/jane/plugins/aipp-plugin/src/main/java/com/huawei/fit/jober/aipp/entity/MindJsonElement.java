/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MindJsonElement {
    String name;
    String children;

    static public String packToElementJson(String name, String children) {
        return String.format("{\"name\":\"%s\",\"children\":[%s]}", name, children);
    }
}
