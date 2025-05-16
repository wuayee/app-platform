/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.taskcenter.util.sql;

/**
 * 为 {@link SqlValue} 提供工具方法。
 *
 * @author 梁济时
 * @since 2023-10-24
 */
abstract class SqlValues implements SqlValue {
    private final Object value;

    SqlValues(Object value) {
        this.value = value;
    }

    @Override
    public Object get() {
        return this.value;
    }

    static class JsonSqlValue extends SqlValues {
        JsonSqlValue(Object value) {
            super(value);
        }

        @Override
        public String wrapPlaceholder(String placeholder) {
            return placeholder + "::JSON";
        }
    }
}
