/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.common.aop;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * 租户权限校验注解
 *
 * @author 陈镕希
 * @since 2023-10-30
 */
@Target(METHOD)
@Retention(RUNTIME)
public @interface TenantAuthentication {}
