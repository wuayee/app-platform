/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 */

package com.huawei.fitframework.jvm.classfile.attribute;

import com.huawei.fitframework.inspection.Validation;
import com.huawei.fitframework.jvm.classfile.AttributeInfo;
import com.huawei.fitframework.jvm.classfile.AttributeList;
import com.huawei.fitframework.jvm.classfile.constant.Utf8Info;
import com.huawei.fitframework.jvm.classfile.lang.U2;
import com.huawei.fitframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;

/**
 * 表示常量的值。
 *
 * @author 梁济时
 * @since 2022-06-09
 */
public final class ConstantValueAttribute extends AbstractAttribute {
    /**
     * 表示属性的名称。
     */
    public static final String NAME = "ConstantValue";

    private final U2 constantValueIndex;

    /**
     * 使用包含属性数据的输入流初始化 {@link ConstantValueAttribute} 类的新实例。
     *
     * @param attribute 表示所属的属性的 {@link AttributeInfo}。
     * @param in 表示包含属性信息的输入流的 {@link InputStream}。
     * @throws IllegalArgumentException {@code in} 为 {@code null}。
     * @throws IOException 读取过程发生输入输出异常。
     */
    public ConstantValueAttribute(AttributeInfo attribute, InputStream in) throws IOException {
        super(attribute);
        Validation.notNull(in, "The input stream to read constant value attribute cannot be null.");
        this.constantValueIndex = U2.read(in);
    }

    /**
     * 获取常量的值在常量池中的索引。
     *
     * @return 表示常量池中索引的 {@link U2}。
     */
    public U2 constantValueIndex() {
        return this.constantValueIndex;
    }

    /**
     * 从属性列表中查找常量值的属性。
     *
     * @param attributes 表示属性列表的 {@link AttributeList}。
     * @return 若存在常量值属性，则为表示该属性的 {@link ConstantValueAttribute}，否则为 {@code null}。
     * @throws IOException 查找过程发生输入输出异常。
     */
    public static ConstantValueAttribute lookup(AttributeList attributes) throws IOException {
        if (attributes == null) {
            return null;
        }
        for (AttributeInfo attribute : attributes) {
            String name = attribute.file().constants().<Utf8Info>get(attribute.attributeNameIndex()).stringValue();
            if (StringUtils.equalsIgnoreCase(name, NAME)) {
                try (InputStream in = attribute.read()) {
                    return new ConstantValueAttribute(attribute, in);
                }
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return this.attribute().list().file().constants().get(this.constantValueIndex).toString();
    }
}
