/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jane.common.entity;

/**
 * 操作人相关上下文。
 *
 * @author 陈镕希 c00572808
 * @since 2023-12-15
 */
public class OperationContext {
    /**
     * 租户id
     */
    private String tenantId;

    /**
     * 操作人. 格式为中文名 工号 e.g. 张三 00123456
     */
    private String operator;

    /**
     * 全局UserId. 对应公司efs中/hr/getPersonInfo接口的global_user_id e.g. 167779546509824
     */
    private String globalUserId;

    /**
     * w3工号. 对应公司efs中/hr/getPersonInfo接口的w3account e.g. z00123456
     */
    private String w3Account;

    /**
     * 纯工号. 对应公司efs中/hr/getPersonInfo接口的employee_number e.g. 00123456
     */
    private String employeeNumber;

    /**
     * 对应公司efs中/hr/getPersonInfo接口的last_name
     */
    private String name;

    /**
     * 操作人ip信息
     */
    private String operatorIp;

    /**
     * 调用来源平台信息
     */
    private String sourcePlatform;

    private String language;

    /**
     * 无参构造函数
     */
    public OperationContext() {
    }

    public OperationContext(String tenantId, String operator, String globalUserId, String w3Account,
            String employeeNumber, String name, String operatorIp, String sourcePlatform, String language) {
        this.tenantId = tenantId;
        this.operator = operator;
        this.globalUserId = globalUserId;
        this.w3Account = w3Account;
        this.employeeNumber = employeeNumber;
        this.name = name;
        this.operatorIp = operatorIp;
        this.sourcePlatform = sourcePlatform;
        this.language = language;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getGlobalUserId() {
        return globalUserId;
    }

    public void setGlobalUserId(String globalUserId) {
        this.globalUserId = globalUserId;
    }

    public String getW3Account() {
        return w3Account;
    }

    public void setW3Account(String w3Account) {
        this.w3Account = w3Account;
    }

    public String getEmployeeNumber() {
        return employeeNumber;
    }

    public void setEmployeeNumber(String employeeNumber) {
        this.employeeNumber = employeeNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOperatorIp() {
        return operatorIp;
    }

    public void setOperatorIp(String operatorIp) {
        this.operatorIp = operatorIp;
    }

    public String getSourcePlatform() {
        return sourcePlatform;
    }

    public void setSourcePlatform(String sourcePlatform) {
        this.sourcePlatform = sourcePlatform;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}