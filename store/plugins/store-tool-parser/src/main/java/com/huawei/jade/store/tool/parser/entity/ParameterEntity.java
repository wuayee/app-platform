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
    private String name;
    private String type;
    private String description;

    /**
     * 无参构造方法构建 {@link ParameterEntity} 实例。
     */
    public ParameterEntity() {}

    /**
     * 表示设置参数的名字。
     *
     * @param name 待设置参数的名字的 {@link String}。
     */
    public void setName(String name) {
        this.name = notNull(name, "The name can not be null.");
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
     * 表示设置参数的类型。
     *
     * @param type 待设置参数的类型的 {@link String}。
     */
    public void setType(String type) {
        this.type = notNull(type, "The type can not be null.");
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
     * 表示设置参数的描述信息。
     *
     * @param description 待设置参数的描述信息的 {@link String}。
     */
    public void setDescription(String description) {
        this.description = notNull(description, "The description can not be null.");
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
