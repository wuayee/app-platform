/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.store.entity.parser;

/**
 * 表示参数的定义。
 *
 * @author 杭潇 h00675922
 * @since 2024-06-15
 */
public class ParameterEntity {
    private final String name;
    private final Object type;
    private final String description;

    /**
     * 参数实体的构造方法。
     *
     * @param name 表示参数名的 {@link String}。
     * @param type 表示参数的类型的 {@link Object}。
     * @param description 表示参数的描述的 {@link String}。
     */
    public ParameterEntity(String name, Object type, String description) {
        this.name = name;
        this.type = type;
        this.description = description;
    }

    /**
     * 表示获取参数的名字。
     *
     * @return 参数的名字的 {@link String}。
     */
    public String getName() {
        return this.name;
    }

    /**
     * 表示获取参数的类型。
     *
     * @return 参数的类型的 {@link Object}。
     */
    public Object getType() {
        return this.type;
    }

    /**
     * 表示获取参数的描述信息。
     *
     * @return 参数的描述信息的 {@link String}。
     */
    public String getDescription() {
        return this.description;
    }

    @Override
    public String toString() {
        return "ParameterEntity{" + "name='" + name + '\'' + ", type=" + type + ", description='" + description + '\''
                + '}';
    }
}
