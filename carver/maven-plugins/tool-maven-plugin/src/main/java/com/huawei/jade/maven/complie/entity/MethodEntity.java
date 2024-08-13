/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.maven.complie.entity;

import com.huawei.jade.carver.tool.annotation.ToolMethod;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 表示 {@link ToolMethod} 注解下的方法实体。
 *
 * @author 杭潇
 * @since 2024-06-13
 */
public class MethodEntity {
    private String fitableId;
    private String genericableId;
    private String methodName;
    private String methodDescription;
    private List<ParameterEntity> parameterEntities;
    private String returnDescription;
    private String returnType;
    private Set<String> tags;

    /**
     * 方法实体的无参构造方法。
     */
    public MethodEntity() {
        this.parameterEntities = new ArrayList<>();
    }

    /**
     * 获取 Fitable Id 值。
     *
     * @return 获取 Fitable Id 值的 {@link String}。
     */
    public String getFitableId() {
        return this.fitableId;
    }

    /**
     * 设置 Fitable Id 值。
     *
     * @param fitableId 待设置的 Fitable Id 值。
     */
    public void setFitableId(String fitableId) {
        this.fitableId = fitableId;
    }

    /**
     * 获取 Genericable Id 值。
     *
     * @return 获取 Genericable Id 值的 {@link String}。
     */
    public String getGenericableId() {
        return this.genericableId;
    }

    /**
     * 设置 Genericable Id 值。
     *
     * @param genericableId 待设置的 Genericable Id 值。
     */
    public void setGenericableId(String genericableId) {
        this.genericableId = genericableId;
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
     * 表示设置方法名。
     *
     * @param methodName 表示给定的方法名的 {@link String}。
     */
    public void setMethodName(String methodName) {
        this.methodName = methodName;
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
     * 表示设置方法描述。
     *
     * @param methodDescription 表示给定的方法描述的 {@link String}。
     */
    public void setMethodDescription(String methodDescription) {
        this.methodDescription = methodDescription;
    }

    /**
     * 表示获取方法的参数列表信息。
     *
     * @return 获取方法的参数列表信息的 {@link List}{@code <}{@link ParameterEntity}{@code >}。
     */
    public List<ParameterEntity> getParameterEntities() {
        return this.parameterEntities;
    }

    /**
     * 表示设置方法的参数信息列表。
     *
     * @param parameterEntities 表示给定的参数信息列表的 {@link List}{@code <}{@link ParameterEntity}{@code >}。
     */
    public void setParameterEntities(List<ParameterEntity> parameterEntities) {
        this.parameterEntities = parameterEntities;
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
     * 表示设置返回值的描述。
     *
     * @param returnDescription 表示给定的返回值的描述的 {@link String}。
     */
    public void setReturnDescription(String returnDescription) {
        this.returnDescription = returnDescription;
    }

    /**
     * 表示获取方法的返回值类型。
     *
     * @return 获取方法的返回值类型的 {@link String}。
     */
    public String getReturnType() {
        return this.returnType;
    }

    /**
     * 表示设置的返回值类型。
     *
     * @param returnType 表示给定的返回值类型的 {@link String}。
     */
    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

    /**
     * 表示获取方法的标签。
     *
     * @return 获取方法的标签的 {@link Set}{@code <}{@link String}{@code >}。
     */
    public Set<String> getTags() {
        return this.tags;
    }

    /**
     * 表示设置的方法的标签。
     *
     * @param tags 表示给定的方法标签的 {@link Set}{@code <}{@link String}{@code >}。
     */
    public void setTags(Set<String> tags) {
        this.tags = tags;
    }
}