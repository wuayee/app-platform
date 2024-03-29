/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 */

package com.huawei.fitframework.jvm.classfile.lang;

import com.huawei.fitframework.util.Convert;
import com.huawei.fitframework.util.IoUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * 为JVM提供4字节的数据。
 *
 * @author 梁济时 l00815032
 * @since 2022-06-07
 */
public final class U4 implements Comparable<U4> {
    public static final U4 ZERO = U4.of(0);
    public static final U4 ONE = U4.of(1);
    public static final U4 TWO = U4.of(2);

    private static List<U4> cache;

    private final int value;

    private U4(int value) {
        this.value = value;
    }

    /**
     * 获取字节数据表现形式。
     *
     * @return 表示数据的字节表现形式。
     * @throws ValueOverflowException 数据超出表示范围。
     */
    public byte byteValue() {
        return ValueConvert.byteValue(this.value);
    }

    /**
     * 获取16位整数数据表现形式。
     *
     * @return 表示数据的16位整数表现形式。
     * @throws ValueOverflowException 数据超出表示范围。
     */
    public short shortValue() {
        return ValueConvert.shortValue(this.value);
    }

    /**
     * 获取32位整数数据表现形式。
     *
     * @return 表示数据的32位整数表现形式。
     */
    public int intValue() {
        return this.value;
    }

    /**
     * 获取64位整数数据表现形式。
     *
     * @return 表示数据的64位整数表现形式。
     */
    public long longValue() {
        return ValueConvert.longValue(this.value);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj instanceof U4) {
            U4 another = (U4) obj;
            return another.value == this.value;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(new Object[] {U4.class, this.value});
    }

    @Override
    public String toString() {
        return Integer.toUnsignedString(this.value);
    }

    /**
     * 返回一个16进制字符串，用以表示当前的数据。
     *
     * @return 表示当前数据的16进制字符串的 {@link String}。
     */
    public String toHexString() {
        return String.format("%08x", this.value);
    }

    @Override
    public int compareTo(U4 another) {
        return Integer.compare(this.value, another.value);
    }

    public U4 add(U4 another) {
        return of(this.value + another.value);
    }

    /**
     * 使用字节数据创建实例。
     *
     * @param value 表示包含数据的字节。
     * @return 表示4字节数据的 {@link U4}。
     */
    public static U4 of(byte value) {
        return of(ValueConvert.intValue(value));
    }

    /**
     * 使用16位整数数据创建实例。
     *
     * @param value 表示包含数据的16位整数。
     * @return 表示4字节数据的 {@link U4}。
     */
    public static U4 of(short value) {
        return of(ValueConvert.intValue(value));
    }

    /**
     * 使用32位整数数据创建实例。
     *
     * @param value 表示包含数据的32位整数。
     * @return 表示4字节数据的 {@link U4}。
     */
    public static U4 of(int value) {
        if (value < 0 || value >= 128) {
            return new U4(value);
        }
        if (cache == null) {
            cache = IntStream.range(0, 128).mapToObj(U4::new).collect(Collectors.toList());
        }
        return cache.get(value);
    }

    /**
     * 使用64位整数数据创建实例。
     *
     * @param value 表示包含数据的64位整数。
     * @return 表示4字节数据的 {@link U4}。
     * @throws ValueOverflowException 数据超出表示范围。
     */
    public static U4 of(long value) {
        return of(ValueConvert.intValue(value));
    }

    /**
     * 从输入流中读取4字节数据。
     *
     * @param in 表示包含数据的输入流的 {@link InputStream}。
     * @return 表示4字节数据的 {@link U4}。
     * @throws IOException 读取数据过程发生输入输出异常。
     */
    public static U4 read(InputStream in) throws IOException {
        return of(Convert.toInteger(IoUtils.read(in, 4)));
    }

    /**
     * 将数据写入到输出流中。
     *
     * @param out 表示待将数据写入到的输出流的 {@link OutputStream}。
     * @throws IOException 写入数据过程发生输入输出异常。
     */
    public void write(OutputStream out) throws IOException {
        out.write(Convert.toBytes(this.value));
    }
}
