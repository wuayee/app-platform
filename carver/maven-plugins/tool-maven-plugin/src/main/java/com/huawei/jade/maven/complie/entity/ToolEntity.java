/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.maven.complie.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * 表示工具实体的定义。
 *
 * @author 杭潇
 * @since 2024-06-06
 */
public class ToolEntity {
    private final List<MethodEntity> methods;

    /**
     * 工具实体的无参构造方法。
     */
    public ToolEntity() {
        this.methods = new ArrayList<>();
    }

    /**
     * 添加给定方法实体到工具实体中。
     *
     * @param method 待添加的工具实体的 {@link MethodEntity}。
     */
    public void addMethod(MethodEntity method) {
        this.methods.add(method);
    }

    /**
     * 表示获取工具的所有方法实体列表。
     *
     * @return 工具所有方法的实体列表的 {@link List}{@code <}{@link MethodEntity}{@code >}。
     */
    public List<MethodEntity> getMethods() {
        return this.methods;
    }
}
