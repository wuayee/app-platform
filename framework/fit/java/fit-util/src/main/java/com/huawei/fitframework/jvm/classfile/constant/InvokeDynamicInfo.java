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
import java.util.Arrays;
import java.util.Objects;

/**
 * 表示动态计算的调用站点（{@link java.lang.invoke.CallSite} 的实例）。
 *
 * @author 梁济时
 * @since 2022-06-07
 */
public final class InvokeDynamicInfo extends Constant {
    /**
     * 表示常量的标签。
     */
    public static final U1 TAG = U1.of(18);

    private final U2 bootstrapMethodAttrIndex;
    private final U2 nameAndTypeIndex;

    public InvokeDynamicInfo(ConstantPool pool, InputStream in) throws IOException {
        super(pool, TAG);
        Validation.notNull(in, "The input stream to read constant data cannot be null.");
        this.bootstrapMethodAttrIndex = U2.read(in);
        this.nameAndTypeIndex = U2.read(in);
    }

    /**
     * 获取 {@code bootstrap_methods} 属性中的有效索引。
     *
     * @return 表示 {@code bootstrap_methods} 属性中的有效索引的 {@link U2}。
     */
    public U2 bootstrapMethodAttrIndex() {
        return this.bootstrapMethodAttrIndex;
    }

    /**
     * 获取常量池中 {@link NameAndTypeInfo} 常量的索引。
     *
     * @return 表示常量池中的索引的 {@link U2}，该索引处的常量必然是一个 {@link NameAndTypeInfo}。
     */
    public U2 nameAndTypeIndex() {
        return this.nameAndTypeIndex;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj instanceof InvokeDynamicInfo) {
            InvokeDynamicInfo another = (InvokeDynamicInfo) obj;
            return another.pool() == this.pool() && Objects.equals(this.bootstrapMethodAttrIndex,
                    another.bootstrapMethodAttrIndex) && Objects.equals(this.nameAndTypeIndex,
                    another.nameAndTypeIndex);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(new Object[] {
                this.pool(), InvokeDynamicInfo.class, this.bootstrapMethodAttrIndex, this.nameAndTypeIndex
        });
    }

    @Override
    public String toString() {
        return StringUtils.format("[tag={0}, bootstrap_method_attr_index={1}, name_and_type_index={2}]",
                this.tag(), this.bootstrapMethodAttrIndex(), this.nameAndTypeIndex());
    }
}
