/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2024. All rights reserved.
 */

package com.huawei.fitframework.jvm.classfile;

import com.huawei.fitframework.inspection.Validation;
import com.huawei.fitframework.jvm.classfile.lang.U2;

import java.io.IOException;
import java.io.InputStream;

/**
 * 为方法提供信息。
 *
 * @author 梁济时
 * @since 2022-06-09
 */
public final class MethodInfo {
    private final U2 accessFlags;
    private final U2 nameIndex;
    private final U2 descriptorIndex;
    private final AttributeList attributes;

    private final MethodList list;

    public MethodInfo(MethodList list, InputStream in) throws IOException {
        this.list = Validation.notNull(list, "The owning list of a method cannot be null.");
        Validation.notNull(in, "The input stream to read field info cannot be null.");
        this.accessFlags = U2.read(in);
        this.nameIndex = U2.read(in);
        this.descriptorIndex = U2.read(in);
        this.attributes = new AttributeList(list.file(), in);
    }

    /**
     * 获取方法所属的方法列表。
     *
     * @return 表示方法列表的 {@link MethodList}。
     */
    public MethodList list() {
        return this.list;
    }

    /**
     * 获取方法所属的类文件。
     *
     * @return 表示方法所属的类文件的 {@link ClassFile}。
     */
    public ClassFile file() {
        return this.list().file();
    }

    /**
     * 获取方法的访问级别。
     *
     * @return 表示方法的访问级别的 {@link U2}。
     */
    public U2 accessFlags() {
        return this.accessFlags;
    }

    /**
     * 获取方法名称在常量池中的索引。
     * <p>该索引处必然是一个 {@link com.huawei.fitframework.jvm.classfile.constant.Utf8Info CONSTANT_Utf8_info}
     * 类型的常量。</p>
     *
     * @return 表示名称在常量池中的索引的 {@link U2}。
     */
    public U2 nameIndex() {
        return this.nameIndex;
    }

    /**
     * 获取方法的描述下标序号。
     *
     * @return 表示方法的描述下标序号的 {@link U2}。
     */
    public U2 descriptorIndex() {
        return this.descriptorIndex;
    }

    /**
     * 获取方法所属类文件的属性列表。
     *
     * @return 表示方法所属类文件的属性列表的 {@link AttributeList}。
     */
    public AttributeList attributes() {
        return this.attributes;
    }
}
