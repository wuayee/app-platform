/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.store.tool.parser.entity;

import static com.huawei.fitframework.inspection.Validation.notNull;

/**
 * 表示参数的定义。
 *
 * @author 杭潇 h00675922
 * @since 2024-06-15
 */
public class ParameterEntity {
    private final String name;
    private final String type;
    private final String description;

    /**
     * 参数实体的构造方法。
     *
     * @param name 表示参数名的 {@link String}。
     * @param type 表示参数的类型的 {@link String}。
     * @param description 表示参数的描述的 {@link String}。
     */
    public ParameterEntity(String name, String type, String description) {
        this.name = notNull(name, "The name can not be null.");
        this.type = notNull(type, "The type can not be null.");
        this.description = notNull(description, "The description can not be null.");
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
     * @return 参数的类型的 {@link String}。
     */
    public String getType() {
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
}
