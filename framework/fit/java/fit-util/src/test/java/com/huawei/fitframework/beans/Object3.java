/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fitframework.beans;

/**
 * 表示测试对象 3。
 *
 * @author 季聿阶
 * @since 2024-01-27
 */
public class Object3 {
    private String f1;
    private int f2;
    private Object1 o1;

    /**
     * 获取第一个属性。
     *
     * @return 表示第一个属性的 {@link String}。
     */
    public String getF1() {
        return this.f1;
    }

    /**
     * 设置第一个属性。
     *
     * @param f1 表示待设置的第一个属性的 {@link String}。
     */
    public void setF1(String f1) {
        this.f1 = f1;
    }

    /**
     * 获取第二个属性。
     *
     * @return 表示第二个属性的 {@link String}。
     */
    public int getF2() {
        return this.f2;
    }

    /**
     * 设置第二个属性。
     *
     * @param f2 表示待设置的第二个属性的 {@link String}。
     */
    public void setF2(int f2) {
        this.f2 = f2;
    }

    /**
     * 获取第三个属性。
     *
     * @return 表示第三个属性的 {@link Object1}。
     */
    public Object1 getO1() {
        return this.o1;
    }

    /**
     * 设置第三个属性。
     *
     * @param o1 表示待设置的第三个属性的 {@link Object1}。
     */
    public void setO1(Object1 o1) {
        this.o1 = o1;
    }
}
