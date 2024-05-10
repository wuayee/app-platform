/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.domain.postgresql;

import static com.huawei.fitframework.util.ObjectUtils.cast;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * 为领域对象存储的行提供基类。
 *
 * @author 梁济时 l00815032
 * @since 2023-11-30
 */
abstract class AbstractDomainRow {
    static final String COLUMN_ID = "id";

    static final String COLUMN_CREATOR = "created_by";

    static final String COLUMN_CREATION_TIME = "created_at";

    static final String COLUMN_LAST_MODIFIER = "updated_by";

    static final String COLUMN_LAST_MODIFICATION_TIME = "updated_at";

    private final Map<String, Object> values;

    AbstractDomainRow(Map<String, Object> values) {
        this.values = values;
    }

    /**
     * 设置指定列的值。
     *
     * @param column 表示待设置值的列的名称的 {@link String}。
     * @param value 表示列的值的 {@link Object}。
     */
    protected final void set(String column, Object value) {
        this.values.put(column, value);
    }

    /**
     * 设置指定列的值。
     *
     * @param column 表示待获取值的列的名称的 {@link String}。
     * @return 表示列的值的 {@link Object}。
     */
    protected final Object get(String column) {
        return this.values.get(column);
    }

    String id() {
        return cast(this.get(COLUMN_ID));
    }

    void id(String id) {
        this.set(COLUMN_ID, id);
    }

    String creator() {
        return cast(this.get(COLUMN_CREATOR));
    }

    void creator(String creator) {
        this.set(COLUMN_CREATOR, creator);
    }

    LocalDateTime creationTime() {
        Object value = this.get(COLUMN_CREATION_TIME);
        if (value instanceof Timestamp) {
            value = ((Timestamp) value).toLocalDateTime();
            this.set(COLUMN_CREATION_TIME, value);
        }
        return cast(value);
    }

    void creationTime(LocalDateTime creationTime) {
        this.set(COLUMN_CREATION_TIME, creationTime);
    }

    String lastModifier() {
        return cast(this.get(COLUMN_LAST_MODIFIER));
    }

    void lastModifier(String lastModifier) {
        this.set(COLUMN_LAST_MODIFIER, lastModifier);
    }

    LocalDateTime lastModificationTime() {
        Object value = this.get(COLUMN_LAST_MODIFICATION_TIME);
        if (value instanceof Timestamp) {
            value = ((Timestamp) value).toLocalDateTime();
            this.set(COLUMN_LAST_MODIFICATION_TIME, value);
        }
        return cast(value);
    }

    void lastModificationTime(LocalDateTime lastModificationTime) {
        this.set(COLUMN_LAST_MODIFICATION_TIME, lastModificationTime);
    }
}
