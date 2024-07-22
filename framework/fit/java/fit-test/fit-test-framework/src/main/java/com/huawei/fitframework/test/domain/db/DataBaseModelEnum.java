/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fitframework.test.domain.db;

import com.huawei.fitframework.util.StringUtils;

/**
 * 表示数据库兼容模式的枚举。
 *
 * @author 易文渊
 * @since 2024-07-21
 */
public enum DataBaseModelEnum {
    NONE(""),
    MYSQL("MySQL"),
    POSTGRESQL("PostgreSQL");

    private final String model;

    DataBaseModelEnum(String model) {
        this.model = model;
    }

    /**
     * 获取数据库连接的 URL。
     *
     * @return 表示数据库连接 URL 的 {@link String}。
     */
    public String getUrl() {
        String baseUrl = "jdbc:h2:mem:test;DATABASE_TO_LOWER=TRUE";
        return StringUtils.isBlank(this.model) ? baseUrl : baseUrl + ";MODE=" + this.model;
    }
}
