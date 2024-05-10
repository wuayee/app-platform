/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.dummy;

import com.huawei.fit.jane.common.entity.OperationContext;

public class OperationContextDummy {
    public static final String DUMMY_TENANT_ID = "a1b2c3d4e5f6";
    public static final String DUMMY_OPERATOR = "Taylor 00000001";
    public static final String DUMMY_GLOBAL_USER_ID = "167777777509824";
    public static final String DUMMY_W3_ACCOUNT = "t00000001";
    public static final String DUMMY_EMPLOYEE_NUMBER = "00000001";
    public static final String DUMMY_NAME = "Taylor";
    public static final String DUMMY_OPERATOR_IP = "127.0.0.1";
    public static final String DUMMY_SOURCE_PLATFORM = "some Platform";
    public static final String DUMMY_LANGUAGE = "zh";

    public static OperationContext getDummy() {
        return new OperationContext(DUMMY_TENANT_ID,
                DUMMY_OPERATOR,
                DUMMY_GLOBAL_USER_ID,
                DUMMY_W3_ACCOUNT,
                DUMMY_EMPLOYEE_NUMBER,
                DUMMY_NAME,
                DUMMY_OPERATOR_IP,
                DUMMY_SOURCE_PLATFORM,
                DUMMY_LANGUAGE);
    }

    public static boolean operationContextDummyMatcher(OperationContext context) {
        return DUMMY_TENANT_ID.equals(context.getTenantId()) && DUMMY_OPERATOR.equals(context.getOperator())
                && DUMMY_GLOBAL_USER_ID.equals(context.getGlobalUserId())
                && DUMMY_W3_ACCOUNT.equals(context.getW3Account())
                && DUMMY_EMPLOYEE_NUMBER.equals(context.getEmployeeNumber()) && DUMMY_NAME.equals(context.getName())
                && DUMMY_OPERATOR_IP.equals(context.getOperatorIp())
                && DUMMY_SOURCE_PLATFORM.equals(context.getSourcePlatform())
                && DUMMY_LANGUAGE.equals(context.getLanguage());
    }
}
