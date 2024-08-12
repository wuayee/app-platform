/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.entity;

import java.util.Map;

/**
 * 流程启动参数信息
 *
 * @author 杨祥宇
 * @since 2023/12/11
 */
public class FlowStartParameter {
    /**
     * 流程启动人
     */
    private String operator;

    /**
     * 流程执行所需的业务参数
     */
    private Map<String, Object> businessData;

    public FlowStartParameter() {
        this(null, null);
    }

    public FlowStartParameter(String operator, Map<String, Object> businessData) {
        this.operator = operator;
        this.businessData = businessData;
    }

    public String getOperator() {
        return operator;
    }

    public Map<String, Object> getBusinessData() {
        return businessData;
    }
}
