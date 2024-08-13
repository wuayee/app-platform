/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2022. All rights reserved.
 */

package com.huawei.fitframework.parameterization.support;

import com.huawei.fitframework.inspection.Validation;
import com.huawei.fitframework.parameterization.ResolvedParameter;
import com.huawei.fitframework.util.StringUtils;

/**
 * 为 {@link ResolvedParameter} 提供默认实现。
 *
 * @author 梁济时
 * @author 季聿阶
 * @since 2020-07-24
 */
class DefaultResolvedParameter implements ResolvedParameter {
    private final String name;
    private final int position;
    private final int length;
    private final int escapedPosition;

    /**
     * 使用解析到参数的解析器、参数位置索引及参数名称初始化 {@link DefaultResolvedParameter} 类的新实例。
     *
     * @param name 表示参数名称的 {@link String}。
     * @param position 表示参数在源字符串中的位置索引的 32 位整数。
     * @param length 表示参数在源字符串中的长度的 32 位整数。
     * @param escapedPosition 表示参数在转义后字符串中的位置的 {@code int}。
     */
    DefaultResolvedParameter(String name, int position, int length, int escapedPosition) {
        this.name = Validation.notBlank(name, "Name cannot be blank.");
        this.position = position;
        this.length = length;
        this.escapedPosition = escapedPosition;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public int getPosition() {
        return this.position;
    }

    @Override
    public int getLength() {
        return this.length;
    }

    /**
     * 获取参数在转义后的字符串中的位置。
     *
     * @return 表示参数在转义后字符串中位置的 {@code int}。
     */
    int getEscapedPosition() {
        return this.escapedPosition;
    }

    @Override
    public String toString() {
        return StringUtils.format("[position={0}, variable={1}]", this.position, this.name);
    }
}
