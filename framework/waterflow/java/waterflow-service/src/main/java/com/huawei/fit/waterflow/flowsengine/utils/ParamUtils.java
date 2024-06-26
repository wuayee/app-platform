/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.waterflow.flowsengine.utils;

import com.huawei.fit.jane.common.entity.OperationContext;

import java.util.Objects;

/**
 * ParamUtils
 *
 * @author 陈镕希 c00572808
 * @since 2023-09-25
 */
public class ParamUtils {
    /**
     * 隐藏默认构造方法，避免工具类被实例化。
     */
    private ParamUtils() {
    }

    /**
     * 操作人相关信息转换。
     *
     * @param context 表示待转换的操作人信息的 {@link OperationContext}。
     * @return 表示转换后操作人信息的 {@link com.huawei.fit.jane.task.util.OperationContext}。
     */
    public static com.huawei.fit.jane.task.util.OperationContext convertToInternalOperationContext(
            OperationContext context) {
        com.huawei.fit.jane.task.util.OperationContext operationContext;
        if (Objects.nonNull(context)) {
            operationContext = com.huawei.fit.jane.task.util.OperationContext.custom()
                    .tenantId(context.getTenantId())
                    .operator(context.getOperator())
                    .operatorIp(context.getOperatorIp())
                    .sourcePlatform(context.getSourcePlatform())
                    .langage(context.getLanguage())
                    .build();
        } else {
            operationContext = com.huawei.fit.jane.task.util.OperationContext.custom().build();
        }
        return operationContext;
    }

    /**
     * 操作人相关信息转换。
     *
     * @param context 表示待转换的操作人信息的 {@link OperationContext}。
     * @return 表示转换后操作人信息的 {@link com.huawei.fit.jane.task.util.OperationContext}。
     */
    public static com.huawei.fit.jane.task.util.OperationContext convertOperationContext(
            com.huawei.fit.jober.entity.OperationContext context) {
        com.huawei.fit.jane.task.util.OperationContext operationContext;
        if (Objects.nonNull(context)) {
            operationContext = com.huawei.fit.jane.task.util.OperationContext.custom()
                    .tenantId(context.getTenantId())
                    .operator(context.getOperator())
                    .operatorIp(context.getOperatorIp())
                    .sourcePlatform(context.getSourcePlatform())
                    .langage(context.getLanguage())
                    .build();
        } else {
            operationContext = com.huawei.fit.jane.task.util.OperationContext.custom().build();
        }
        return operationContext;
    }

    public static com.huawei.fit.jober.entity.OperationContext convertOperationContext(
            com.huawei.fit.jane.task.util.OperationContext context) {
        com.huawei.fit.jober.entity.OperationContext operationContext;
        operationContext = new com.huawei.fit.jober.entity.OperationContext();
        if (Objects.nonNull(context)) {
            operationContext.setOperatorIp(context.operatorIp());
            operationContext.setOperator(context.operator());
            operationContext.setTenantId(context.tenantId());
            operationContext.setSourcePlatform(context.sourcePlatform());
            operationContext.setLanguage(context.language());
        }
        return operationContext;
    }
}
