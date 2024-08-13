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
 * 表示包信息。
 *
 * @author 梁济时
 * @since 2022-06-07
 */
public final class PackageInfo extends Constant {
    /**
     * 表示常量的标签。
     */
    public static final U1 TAG = U1.of(20);

    private final U2 nameIndex;

    public PackageInfo(ConstantPool pool, InputStream in) throws IOException {
        super(pool, TAG);
        Validation.notNull(in, "The input stream to read constant data cannot be null.");
        this.nameIndex = U2.read(in);
    }

    /**
     * 获取包名称在常量池中的索引。
     *
     * @return 表示包名称在常量池中的索引的 {@link U2}。
     */
    public U2 nameIndex() {
        return this.nameIndex;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj instanceof PackageInfo) {
            PackageInfo another = (PackageInfo) obj;
            return another.pool() == this.pool() && Objects.equals(this.nameIndex, another.nameIndex);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(new Object[] {this.pool(), PackageInfo.class, this.nameIndex});
    }

    @Override
    public String toString() {
        return StringUtils.format("[tag={0}, name_index={1}]", this.tag(), this.nameIndex());
    }

    @Override
    public void write(OutputStream out) throws IOException {
        super.write(out);
        this.nameIndex().write(out);
    }
}
