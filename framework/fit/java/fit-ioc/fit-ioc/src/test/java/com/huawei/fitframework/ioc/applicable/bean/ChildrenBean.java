/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2023. All rights reserved.
 */

package com.huawei.fitframework.ioc.applicable.bean;

import com.huawei.fitframework.annotation.ApplicableScope;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.ioc.BeanApplicableScope;

/**
 * 定义应用范围为 {@link BeanApplicableScope#CHILDREN} 的 Bean。
 *
 * @author 梁济时 l00815032
 * @since 2022-08-30
 */
@Component
@ApplicableScope(BeanApplicableScope.CHILDREN)
public class ChildrenBean {}
