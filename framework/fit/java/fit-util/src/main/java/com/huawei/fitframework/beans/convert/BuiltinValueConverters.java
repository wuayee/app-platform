/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2023. All rights reserved.
 */

package com.huawei.fitframework.beans.convert;

import com.huawei.fitframework.util.ObjectUtils;
import com.huawei.fitframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

/**
 * 为 {@link ValueConverter} 提供内置转换程序的实现。
 *
 * @author 梁济时 l00815032
 * @since 2022-12-27
 */
public final class BuiltinValueConverters {
    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";

    /**
     * 隐藏默认构造方法，避免工具类被实例化。
     */
    private BuiltinValueConverters() {}

    @ConverterMethod
    public static Byte toByte(Number number) {
        return number.byteValue();
    }

    @ConverterMethod
    public static Byte toByte(String string) {
        return Byte.parseByte(string);
    }

    @ConverterMethod
    public static Short toShort(Number number) {
        return number.shortValue();
    }

    @ConverterMethod
    public static Short toShort(String string) {
        return Short.parseShort(string);
    }

    @ConverterMethod
    public static Integer toInteger(Number number) {
        return number.intValue();
    }

    @ConverterMethod
    public static Integer toInteger(String string) {
        return Integer.parseInt(string);
    }

    @ConverterMethod
    public static Long toLong(Number number) {
        return number.longValue();
    }

    @ConverterMethod
    public static Long toLong(String string) {
        return Long.parseLong(string);
    }

    @ConverterMethod
    public static Float toFloat(Number number) {
        return number.floatValue();
    }

    @ConverterMethod
    public static Float toFloat(String string) {
        return Float.parseFloat(string);
    }

    @ConverterMethod
    public static Double toDouble(Number number) {
        return number.doubleValue();
    }

    @ConverterMethod
    public static Double toDouble(String string) {
        return Double.parseDouble(string);
    }

    @ConverterMethod
    public static Boolean toBoolean(BigInteger integer) {
        return !Objects.equals(integer, BigInteger.ZERO);
    }

    @ConverterMethod
    public static Boolean toBoolean(BigDecimal decimal) {
        return !Objects.equals(decimal, BigDecimal.ZERO.setScale(decimal.scale(), BigDecimal.ROUND_FLOOR));
    }

    @ConverterMethod
    public static Boolean toBoolean(String string) {
        return Boolean.parseBoolean(string);
    }

    @ConverterMethod
    public static Character toCharacter(Number number) {
        return (char) number.longValue();
    }

    @ConverterMethod
    public static Character toCharacter(String string) {
        if (string.isEmpty()) {
            return '\0';
        } else {
            return string.charAt(0);
        }
    }

    @ConverterMethod
    public static String toString(Object object) {
        return ObjectUtils.toString(object);
    }

    @ConverterMethod
    public static BigInteger toBigInteger(Number number) {
        if (number instanceof BigDecimal) {
            return ((BigDecimal) number).toBigInteger();
        } else {
            return BigInteger.valueOf(number.longValue());
        }
    }

    @ConverterMethod
    public static BigInteger toBigInteger(String string) {
        return new BigInteger(string);
    }

    @ConverterMethod
    public static BigDecimal toBigDecimal(Number number) {
        if (number instanceof BigInteger) {
            return new BigDecimal((BigInteger) number);
        } else {
            return BigDecimal.valueOf(number.doubleValue());
        }
    }

    @ConverterMethod
    public static BigDecimal toBigDecimal(String string) {
        return new BigDecimal(string);
    }

    @ConverterMethod
    public static Date toDate(String string) {
        DateFormat format = new SimpleDateFormat(DATE_FORMAT);
        try {
            return format.parse(string);
        } catch (ParseException ex) {
            throw new IllegalStateException(StringUtils.format("Illegal date string. [value={0}]", string), ex);
        }
    }
}
