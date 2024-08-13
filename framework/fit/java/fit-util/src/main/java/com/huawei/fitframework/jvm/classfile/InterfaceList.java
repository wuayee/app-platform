/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 */

package com.huawei.fitframework.jvm.classfile;

import com.huawei.fitframework.inspection.Validation;
import com.huawei.fitframework.jvm.classfile.lang.U2;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * 获取类型所实现的接口的列表。
 *
 * @author 梁济时
 * @since 2022-06-07
 */
public final class InterfaceList implements Iterable<U2> {
    private final ClassFile file;
    private final List<U2> interfaces;

    public InterfaceList(ClassFile file, InputStream in) throws IOException {
        this.file = Validation.notNull(file, "The owning class file of the interfaces cannot be null.");
        U2 count = U2.read(in);
        this.interfaces = new ArrayList<>(count.intValue());
        for (U2 i = U2.ZERO; i.compareTo(count) < 0; i = i.add(U2.ONE)) {
            this.interfaces.add(U2.read(in));
        }
    }

    /**
     * 获取接口列表所属的类文件。
     *
     * @return 表示接口列表所属类文件的 {@link ClassFile}。
     */
    public ClassFile file() {
        return this.file;
    }

    /**
     * 获取列表中包含接口的数量。
     *
     * @return 表示接口数量的 {@link U2}。
     */
    public U2 count() {
        return U2.of(this.interfaces.size());
    }

    /**
     * 获取指定索引处的接口。
     *
     * @param index 表示待获取的接口在列表中的索引的 {@link U2}。
     * @return 表示该索引处的接口的 {@link U2}。
     */
    public U2 get(U2 index) {
        return this.interfaces.get(index.intValue());
    }

    @Override
    public Iterator<U2> iterator() {
        return Collections.unmodifiableList(this.interfaces).iterator();
    }
}
