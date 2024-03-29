/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 */

package com.huawei.fitframework.jvm.classfile.constant;

import com.huawei.fitframework.inspection.Validation;
import com.huawei.fitframework.jvm.classfile.Constant;
import com.huawei.fitframework.jvm.classfile.ConstantPool;
import com.huawei.fitframework.jvm.classfile.lang.U1;
import com.huawei.fitframework.jvm.classfile.lang.U2;
import com.huawei.fitframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Objects;

/**
 * 表示一个方法类型。
 *
 * @author 梁济时 l00815032
 * @since 2022-06-07
 */
public final class MethodTypeInfo extends Constant {
    /**
     * 表示常量的标签。
     */
    public static final U1 TAG = U1.of(16);

    private final U2 descriptorIndex;

    public MethodTypeInfo(ConstantPool pool, InputStream in) throws IOException {
        super(pool, TAG);
        Validation.notNull(in, "The input stream to read constant data cannot be null.");
        this.descriptorIndex = U2.read(in);
    }

    /**
     * 表示方法描述符在常量池中的索引。
     *
     * @return 表示方法描述符在常量池中的索引的 {@link U2}。
     */
    public U2 descriptorIndex() {
        return this.descriptorIndex;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj instanceof MethodTypeInfo) {
            MethodTypeInfo another = (MethodTypeInfo) obj;
            return another.pool() == this.pool() && Objects.equals(another.descriptorIndex, this.descriptorIndex);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(new Object[] {this.pool(), MethodTypeInfo.class, this.descriptorIndex});
    }

    @Override
    public String toString() {
        return StringUtils.format("[tag={0}, descriptor_index={1}]", this.tag(), this.descriptorIndex());
    }

    @Override
    public void write(OutputStream out) throws IOException {
        super.write(out);
        this.descriptorIndex().write(out);
    }
}
