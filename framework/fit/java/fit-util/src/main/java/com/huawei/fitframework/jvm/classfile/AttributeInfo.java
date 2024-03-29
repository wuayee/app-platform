/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 */

package com.huawei.fitframework.jvm.classfile;

import com.huawei.fitframework.inspection.Validation;
import com.huawei.fitframework.jvm.classfile.lang.U2;
import com.huawei.fitframework.jvm.classfile.lang.U4;
import com.huawei.fitframework.util.IoUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 表示属性的信息。
 *
 * @author 梁济时 l00815032
 * @since 2022-06-09
 */
public final class AttributeInfo {
    private final AttributeList list;
    private final U2 attributeNameIndex;
    private final U4 attributeLength;
    private final byte[] content;

    public AttributeInfo(AttributeList list, InputStream in) throws IOException {
        this.list = Validation.notNull(list, "The owning list of an attribute cannot be null.");
        Validation.notNull(in, "The input stream to read attribute cannot be null.");
        this.attributeNameIndex = U2.read(in);
        this.attributeLength = U4.read(in);
        this.content = IoUtils.read(in, this.attributeLength.intValue());
    }

    /**
     * 获取属性所属的属性列表。
     *
     * @return 表示属性所属的属性列表的 {@link AttributeList}。
     */
    public final AttributeList list() {
        return this.list;
    }

    /**
     * 获取属性所属的类文件。
     *
     * @return 表示属性所属的类文件的 {@link ClassFile}。
     */
    public final ClassFile file() {
        return this.list().file();
    }

    /**
     * 获取属性名字的下标序号。
     *
     * @return 表示属性名字的下标序号的 {@link U2}。
     */
    public final U2 attributeNameIndex() {
        return this.attributeNameIndex;
    }

    /**
     * 获取属性内容的长度。
     *
     * @return 表示属性内容长度的 {@link U4}。
     */
    public final U4 attributeLength() {
        return this.attributeLength;
    }

    /**
     * 获取包含属性内容的输入流。
     *
     * @return 表示包含属性内容的输入流的 {@link InputStream}。
     * @throws IOException 当读取过程中发生 IO 异常时。
     */
    public final InputStream read() throws IOException {
        return new ByteArrayInputStream(this.content);
    }
}
