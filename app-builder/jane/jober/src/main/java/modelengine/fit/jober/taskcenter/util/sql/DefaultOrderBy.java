/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.taskcenter.util.sql;

import modelengine.fit.jober.common.ErrorCodes;
import modelengine.fit.jober.common.exceptions.BadRequestException;
import modelengine.fitframework.util.StringUtils;

/**
 * 为 {@link OrderBy} 提供默认实现。
 *
 * @author 梁济时
 * @since 2023-09-21
 */
class DefaultOrderBy implements OrderBy {
    private final String property;

    private final String order;

    /**
     * 默认的OrderBy
     *
     * @param property 属性
     * @param order 排序配置
     */
    public DefaultOrderBy(String property, String order) {
        if (StringUtils.isEmpty(this.property = StringUtils.trim(property))) {
            throw new BadRequestException(ErrorCodes.PROPERTY_REQUIRED_TO_SORT);
        }
        this.order = canonicalizeOrder(order);
    }

    @Override
    public String property() {
        return this.property;
    }

    @Override
    public String order() {
        return this.order;
    }

    @Override
    public String toString() {
        return StringUtils.format("{0}({1})", this.order, this.property);
    }

    static DefaultOrderBy parse(String value) {
        String actual = StringUtils.trim(value);
        if (StringUtils.isEmpty(actual)) {
            return null;
        }
        int propertyStart = value.indexOf('(');
        if (propertyStart < 0 || value.charAt(value.length() - 1) != ')') {
            return new DefaultOrderBy(value, ASCENDING);
        }
        String curOrder = canonicalizeOrder(value.substring(0, propertyStart));
        String curProperty = value.substring(propertyStart + 1, value.length() - 1);
        return new DefaultOrderBy(curProperty, curOrder);
    }

    private static String canonicalizeOrder(String order) {
        String actual = StringUtils.trim(order);
        if (StringUtils.isEmpty(actual) || StringUtils.equalsIgnoreCase(actual, ASCENDING)) {
            return ASCENDING;
        } else if (StringUtils.equalsIgnoreCase(actual, DESCENDING)) {
            return DESCENDING;
        } else {
            throw new BadRequestException(ErrorCodes.UNKNOWN_ORDER, actual);
        }
    }
}
