/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.taskcenter.util.sql;

/**
 * 为 {@link SqlBuilder} 提供默认实现
 *
 * @author 陈镕希
 * @since 2023-10-30
 */
class DefaultSqlBuilder implements SqlBuilder {
    private final StringBuilder builder;

    DefaultSqlBuilder() {
        this.builder = new StringBuilder();
    }

    @Override
    public SqlBuilder appendIdentifier(String identifier) {
        this.builder.append('"').append(identifier).append('"');
        return this;
    }

    @Override
    public SqlBuilder append(String value) {
        this.builder.append(value);
        return this;
    }

    @Override
    public SqlBuilder append(char value) {
        this.builder.append(value);
        return this;
    }

    @Override
    public SqlBuilder backspace(int count) {
        this.builder.setLength(this.builder.length() - count);
        return this;
    }

    @Override
    public SqlBuilder appendRepeatedly(String value, int count) {
        for (int i = 0; i < count; i++) {
            this.builder.append(value);
        }
        return this;
    }

    @Override
    public String toString() {
        return this.builder.toString();
    }
}
