/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.taskcenter.util.sql;

import modelengine.fit.jober.taskcenter.util.sql.condition.AlwaysFalseCondition;
import modelengine.fit.jober.taskcenter.util.sql.condition.AlwaysTrueCondition;
import modelengine.fit.jober.taskcenter.util.sql.condition.BetweenCondition;
import modelengine.fit.jober.taskcenter.util.sql.condition.ContainsCondition;
import modelengine.fit.jober.taskcenter.util.sql.condition.DefaultCondition;
import modelengine.fit.jober.taskcenter.util.sql.condition.EqualsCondition;
import modelengine.fit.jober.taskcenter.util.sql.condition.GreaterThanCondition;
import modelengine.fit.jober.taskcenter.util.sql.condition.GreaterThanOrEqualsCondition;
import modelengine.fit.jober.taskcenter.util.sql.condition.InCondition;
import modelengine.fit.jober.taskcenter.util.sql.condition.LessThanCondition;
import modelengine.fit.jober.taskcenter.util.sql.condition.NotEqualsCondition;

import java.util.Collection;
import java.util.List;

/**
 * 表示条件。
 *
 * @author 梁济时
 * @since 2023-09-11
 */
@FunctionalInterface
public interface Condition {
    /**
     * toSql
     *
     * @param sql sql
     * @param args args
     */
    void toSql(SqlBuilder sql, List<Object> args);

    /**
     * 获取一个值，该值指示当前条件是否是一个条件组。
     *
     * @return 若是条件组，则为 {@code true}，否则为 {@code false}。
     */
    default boolean isGroup() {
        return false;
    }

    /**
     * Connector
     *
     * @author 梁济时
     * @since 2023-09-15
     */
    enum Connector {
        /**
         * 表示需要满足所有条件。
         */
        AND,

        /**
         * 表示需要满足任意条件。
         */
        OR,
    }

    /**
     * 创建一个指定语句与参数的条件。
     *
     * @param conditionSql 表示条件语句的 {@link String}。
     * @param conditionArgs 表示条件参数的 {@link Collection}。
     * @return 表示新创建的查询条件的 {@link Condition}。
     */
    static Condition of(String conditionSql, Collection<?> conditionArgs) {
        return new DefaultCondition(conditionSql, conditionArgs);
    }

    /**
     * 使用逻辑与连接两个条件。
     *
     * @param conditions 表示待连接的条件的 {@link Condition}{@code []}。
     * @return 表示连接后的条件的 {@link Condition}。
     */
    static Condition and(Condition... conditions) {
        return Conditions.combine(Connector.AND, conditions);
    }

    /**
     * 使用逻辑或连接两个条件。
     *
     * @param conditions 表示待连接的条件的 {@link Condition}{@code []}。
     * @return 表示连接后的条件的 {@link Condition}。
     */
    static Condition or(Condition... conditions) {
        return Conditions.combine(Connector.OR, conditions);
    }

    /**
     * 使用逻辑与将当前条件与另一个条件连接。
     *
     * @param another 表示待与当前条件连接的另一个条件的 {@link Condition}。
     * @return 表示连接后的条件的 {@link Condition}。
     */
    default Condition and(Condition another) {
        return and(this, another);
    }

    /**
     * 使用逻辑或将当前条件与另一个条件连接。
     *
     * @param another 表示待与当前条件连接的另一个条件的 {@link Condition}。
     * @return 表示连接后的条件的 {@link Condition}。
     */
    default Condition or(Condition another) {
        return or(this, another);
    }

    /**
     * 获取恒为真的查询条件。
     *
     * @return 表示恒为真的查询条件的 {@link Condition}。
     */
    static Condition alwaysTrue() {
        return AlwaysTrueCondition.INSTANCE;
    }

    /**
     * 获取恒为假的查询条件。
     *
     * @return 表示恒为假的查询条件的 {@link Condition}。
     */
    static Condition alwaysFalse() {
        return AlwaysFalseCondition.INSTANCE;
    }

    /**
     * 创建一个判定指定列为指定值的判定条件。
     *
     * @param column 表示待判定的列的 {@link String}。
     * @param value 表示待判定的值的 {@link Object}。
     * @return 表示新创建的判定条件的 {@link Condition}。
     */
    static Condition expectEqual(String column, Object value) {
        return expectEqual(ColumnRef.of(column), value);
    }

    /**
     * 创建一个判定指定列为指定值的判定条件。
     *
     * @param column 表示待判定的列的 {@link ColumnRef}。
     * @param value 表示待判定的值的 {@link Object}。
     * @return 表示新创建的判定条件的 {@link Condition}。
     */
    static Condition expectEqual(ColumnRef column, Object value) {
        return new EqualsCondition(column, value);
    }

    /**
     * 创建一个判定指定列不为指定值的判定条件。
     *
     * @param column 表示待判定的列的 {@link String}。
     * @param value 表示待判定的值的 {@link Object}。
     * @return 表示新创建的判定条件的 {@link Condition}。
     */
    static Condition expectNotEqual(String column, Object value) {
        return expectNotEqual(ColumnRef.of(column), value);
    }

    /**
     * 创建一个判定指定列不为指定值的判定条件。
     *
     * @param column 表示待判定的列的 {@link ColumnRef}。
     * @param value 表示待判定的值的 {@link Object}。
     * @return 表示新创建的判定条件的 {@link Condition}。
     */
    static Condition expectNotEqual(ColumnRef column, Object value) {
        return new NotEqualsCondition(column, value);
    }

    /**
     * 创建一个判定指定列是否在有效值域内的条件。
     *
     * @param column 表示待判定的列的 {@link String}。
     * @param values 表示有效值域的 {@link List}。
     * @return 表示新创建的判定条件的 {@link Condition}。
     */
    static Condition expectIn(String column, Collection<?> values) {
        return expectIn(ColumnRef.of(column), values);
    }

    /**
     * 创建一个判定指定列是否在有效值域内的条件。
     *
     * @param column 表示待判定的列的 {@link ColumnRef}。
     * @param values 表示有效值域的 {@link List}。
     * @return 表示新创建的判定条件的 {@link Condition}。
     */
    static Condition expectIn(ColumnRef column, Collection<?> values) {
        return new InCondition(column, values);
    }

    /**
     * 创建一个指定列在指定有效区间内的条件。
     *
     * @param column 表示待判定的列的 {@link String}。
     * @param minimum 表示有效区间的最小值的 {@link Object}，最小值在有效区间内。
     * @param maximum 表示有效区间的最大值的 {@link Object}，最大值在有效区间内。
     * @return 表示新创建的判定条件的 {@link Condition}。
     */
    static Condition between(String column, Object minimum, Object maximum) {
        return between(ColumnRef.of(column), minimum, maximum);
    }

    /**
     * 创建一个指定列在指定有效区间内的条件。
     *
     * @param column 表示待判定的列的 {@link ColumnRef}。
     * @param minimum 表示有效区间的最小值的 {@link Object}，最小值在有效区间内。
     * @param maximum 表示有效区间的最大值的 {@link Object}，最大值在有效区间内。
     * @return 表示新创建的判定条件的 {@link Condition}。
     */
    static Condition between(ColumnRef column, Object minimum, Object maximum) {
        return new BetweenCondition(column, minimum, maximum);
    }

    /**
     * 创建一个指定列的值包含指定文本的条件。
     *
     * @param column 表示待判定的列的 {@link String}。
     * @param value 表示待检查包含文本的 {@link String}。
     * @return 表示新创建的判定条件的 {@link Condition}。
     */
    static Condition contains(String column, String value) {
        return contains(ColumnRef.of(column), value);
    }

    /**
     * 创建一个指定列的值包含指定文本的条件。
     *
     * @param column 表示待判定的列的 {@link ColumnRef}。
     * @param value 表示待检查包含文本的 {@link String}。
     * @return 表示新创建的判定条件的 {@link Condition}。
     */
    static Condition contains(ColumnRef column, String value) {
        return new ContainsCondition(column, value);
    }

    /**
     * 创建一个指定列的值小于指定值的条件
     *
     * @param column 表示待判定的列的 {@link String}。
     * @param value 表示待检查包含文本的 {@link Object}。
     * @return 表示新创建的判定条件的 {@link Condition}。
     */
    static Condition lessThan(String column, Object value) {
        return lessThan(ColumnRef.of(column), value);
    }

    /**
     * 创建一个指定列的值小于指定值的条件
     *
     * @param column 表示待判定的列的 {@link ColumnRef}。
     * @param value 表示待检查包含文本的 {@link Object}。
     * @return 表示新创建的判定条件的 {@link Condition}。
     */
    static Condition lessThan(ColumnRef column, Object value) {
        if (value == null) {
            return alwaysFalse();
        } else {
            return new LessThanCondition(column, value);
        }
    }

    /**
     * 创建一个指定列的值大于指定值的条件
     *
     * @param column 表示待判定的列的 {@link String}。
     * @param value 表示待检查包含文本的 {@link Object}。
     * @return 表示新创建的判定条件的 {@link Condition}。
     */
    static Condition greaterThan(String column, Object value) {
        return greaterThan(ColumnRef.of(column), value);
    }

    /**
     * 创建一个指定列的值大于指定值的条件
     *
     * @param column 表示待判定的列的 {@link ColumnRef}。
     * @param value 表示待检查包含文本的 {@link Object}。
     * @return 表示新创建的判定条件的 {@link Condition}。
     */
    static Condition greaterThan(ColumnRef column, Object value) {
        if (value == null) {
            return alwaysFalse();
        } else {
            return new GreaterThanCondition(column, value);
        }
    }

    /**
     * 创建一个指定列的值大于或等于指定值的条件
     *
     * @param column 表示待判定的列的 {@link ColumnRef}。
     * @param value 表示待检查包含文本的 {@link Object}。
     * @return 表示新创建的判定条件的 {@link Condition}。
     */
    static Condition greaterThanOrEquals(String column, Object value) {
        return greaterThanOrEquals(ColumnRef.of(column), value);
    }

    /**
     * 创建一个指定列的值大于或等于指定值的条件
     *
     * @param column 表示待判定的列的 {@link ColumnRef}。
     * @param value 表示待检查包含文本的 {@link Object}。
     * @return 表示新创建的判定条件的 {@link Condition}。
     */
    static Condition greaterThanOrEquals(ColumnRef column, Object value) {
        if (value == null) {
            return alwaysFalse();
        } else {
            return new GreaterThanOrEqualsCondition(column, value);
        }
    }
}
