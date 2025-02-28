/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.taskcenter.util.sql.condition;

import modelengine.fit.jober.taskcenter.util.sql.ColumnRef;
import modelengine.fit.jober.taskcenter.util.sql.Condition;
import modelengine.fit.jober.taskcenter.util.sql.SqlBuilder;

import java.util.Arrays;
import java.util.List;

/**
 * 为模糊匹配文本的条件提供基类。
 *
 * @author 梁济时
 * @since 2024-01-12
 */
public abstract class AbstractLikeCondition implements Condition {
    private static final char[] ESCAPING_CHARS = new char[] {'\\', '%', '_'};

    private static final char ESCAPE_CHARACTER = '\\';

    static {
        Arrays.sort(ESCAPING_CHARS);
    }

    private final ColumnRef column;

    private final String value;

    public AbstractLikeCondition(ColumnRef column, String value) {
        this.column = column;
        this.value = value;
    }

    /**
     * 用于包装值的方法
     *
     * @param value 被包装的 {@link String}
     * @return 包装后的  {@link String}
     */
    protected abstract String wrapValue(String value);

    @Override
    public void toSql(SqlBuilder sql, List<Object> args) {
        if (this.value == null) {
            sql.append("1 <> 1");
        } else if (this.value.isEmpty()) {
            sql.append(this.column).append(" IS NOT NULL");
        } else {
            sql.append(this.column).append(" LIKE ? ESCAPE '").append(ESCAPE_CHARACTER).append("'");
            StringBuilder escaped = new StringBuilder(this.value.length() << 2);
            for (int i = 0; i < this.value.length(); i++) {
                char ch = this.value.charAt(i);
                if (Arrays.binarySearch(ESCAPING_CHARS, ch) > -1) {
                    escaped.append(ESCAPE_CHARACTER);
                }
                escaped.append(ch);
            }
            args.add(this.wrapValue(escaped.toString()));
        }
    }
}
