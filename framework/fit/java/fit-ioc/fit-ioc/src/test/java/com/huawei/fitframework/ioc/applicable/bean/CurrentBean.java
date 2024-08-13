/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2023. All rights reserved.
 */

package com.huawei.fitframework.ioc.applicable.bean;

import com.huawei.fitframework.annotation.ApplicableScope;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.ioc.BeanApplicableScope;

/**
 * 定义应用范围为 {@link BeanApplicableScope#CURRENT} 的 Bean。
 *
 * @author 梁济时
 * @since 2022-08-30
 */
@Component
@ApplicableScope(BeanApplicableScope.CURRENT)
public class CurrentBean {}
