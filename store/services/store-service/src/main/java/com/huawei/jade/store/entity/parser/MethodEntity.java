/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.store.entity.parser;

import java.util.ArrayList;
import java.util.List;

/**
 * 表示方法实体的实体。
 *
 * @author 杭潇 h00675922
 * @since 2024-06-15
 */
public class MethodEntity {
    private final String methodName;
    private final String methodDescription;
    private String returnDescription;
    private Object returnType;
    private final List<ParameterEntity> parameterEntities = new ArrayList<>();

    /**
     * 基于方法的名字与描述的构造方法。
     *
     * @param methodName {@link String}。
     * @param methodDescription 给定方法描述的 {@link String}。
     */
    public MethodEntity(String methodName, String methodDescription) {
        this.methodName = methodName;
        this.methodDescription = methodDescription;
    }

    /**
     * 表示设置返回值的描述。
     *
     * @param returnDescription 表示给定的返回值的描述的 {@link String}。
     */
    public void setReturnDescription(String returnDescription) {
        this.returnDescription = returnDescription;
    }

    /**
     * 表示设置的返回值类型。
     *
     * @param returnType 表示给定的返回值类型的 {@link Object}。
     */
    public void setReturnType(Object returnType) {
        this.returnType = returnType;
    }

    /**
     * 表示获取方法名。
     *
     * @return 获取的方法名的 {@link String}。
     */
    public String getMethodName() {
        return this.methodName;
    }

    /**
     * 表示获取方法的描述信息。
     *
     * @return 获取的方法的描述信息的 {@link String}。
     */
    public String getMethodDescription() {
        return this.methodDescription;
    }

    /**
     * 表示获取方法的返回值的描述信息。
     *
     * @return 获取方法返回值的描述信息的 {@link String}。
     */
    public String getReturnDescription() {
        return this.returnDescription;
    }

    /**
     * 表示获取方法的返回值类型。
     *
     * @return 获取方法的返回值类型的 {@link Object}。
     */
    public Object getReturnType() {
        return this.returnType;
    }

    /**
     * 表示获取方法的参数列表信息。
     *
     * @return 获取方法的参数列表信息的{@link List}{@code <}{@link ParameterEntity}{@code >}。
     */
    public List<ParameterEntity> getParameterEntities() {
        return this.parameterEntities;
    }

    @Override
    public String toString() {
        return "MethodEntity{" + "methodName='" + methodName + '\'' + ", methodDescription='" + methodDescription + '\''
                + ", returnDescription='" + returnDescription + '\'' + ", returnType=" + returnType
                + ", parameterEntities=" + parameterEntities + '}';
    }
}