/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.store.tool.parser.entity;

import static com.huawei.fitframework.inspection.Validation.notNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 表示方法实体类。
 *
 * @author 杭潇 h00675922
 * @since 2024-06-15
 */
public class MethodEntity {
    private String methodName;
    private String methodDescription;
    private String returnDescription;
    private String returnType;
    private final List<ParameterEntity> parameterEntities = new ArrayList<>();
    private Set<String> tags = new HashSet<>();
    private Map<String, Object> schemaInfo;
    private Map<String, Object> runnablesInfo;
    private String targetFilePath;

    /**
     * 无参构造方法构建 {@link MethodEntity} 实例。
     */
    public MethodEntity() {}

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
     * @param returnType 表示给定的返回值类型的 {@link String}。
     */
    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

    /**
     * 设置工具的名字。
     *
     * @param methodName 待设置的工具的名字的 {@link String}。
     */
    public void setMethodName(String methodName) {
        this.methodName = notNull(methodName, "The methodName can not be null.");
    }

    /**
     * 设置工具的描述。
     *
     * @param methodDescription 待设置的工具的描述的 {@link String}。
     */
    public void setMethodDescription(String methodDescription) {
        this.methodDescription = notNull(methodDescription, "The methodDescription can not be null.");
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
     * 表示获取方法的参数列表信息。
     *
     * @return 获取方法的参数列表信息的 {@link List}{@code <}{@link ParameterEntity}{@code >}。
     */
    public List<ParameterEntity> getParameterEntities() {
        return this.parameterEntities;
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
     * 表示设置工具的所有标签。
     *
     * @param tags 表示待设置的工具的标签的 {@link Set}{@code <}{@link String}{@code >}。
     */
    public void setTags(Set<String> tags) {
        this.tags = tags;
    }

    /**
     * 表示获取工具的所有的标签。
     *
     * @return 获取工具的所有的标签的 {@link Set}{@code <}{@link String}{@code >}。
     */
    public Set<String> getTags() {
        return tags;
    }

    /**
     * 表示设置 schema 数据。
     *
     * @param schemaInfo 待设置的 schema 数据的 {@link Map}{@code <}{@link String}{@code , }{@link Object}{@code >}。
     */
    public void setSchemaInfo(Map<String, Object> schemaInfo) {
        this.schemaInfo = schemaInfo;
    }

    /**
     * 表示获取 schema 数据。
     *
     * @return schema 数据的 {@link Map}{@code <}{@link String}{@code , }{@link Object}{@code >}。
     */
    public Map<String, Object> getSchemaInfo() {
        return schemaInfo;
    }

    /**
     * 表示设置 runnables 数据。
     *
     * @param runnablesInfo 待设置的 runnables 数据的 {@link Map}{@code <}{@link String}{@code , }{@link Object}{@code >}。
     */
    public void setRunnablesInfo(Map<String, Object> runnablesInfo) {
        this.runnablesInfo = runnablesInfo;
    }

    /**
     * 表示获取 runnables 数据。
     *
     * @return runnables 数据的 {@link Map}{@code <}{@link String}{@code , }{@link Object}{@code >}。
     */
    public Map<String, Object> getRunnablesInfo() {
        return runnablesInfo;
    }

    /**
     * 设置文件的目标路径。
     *
     * @param targetFilePath 待设置的目标文件路径的 {@link String}。
     */
    public void setTargetFilePath(String targetFilePath) {
        this.targetFilePath = targetFilePath;
    }

    /**
     * 获取目标文件路径。
     *
     * @return 目标文件路径的 {@link String}。
     */
    public String getTargetFilePath() {
        return this.targetFilePath;
    }
}