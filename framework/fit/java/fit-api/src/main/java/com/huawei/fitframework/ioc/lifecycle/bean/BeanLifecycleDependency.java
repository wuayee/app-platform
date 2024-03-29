/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 */

package com.huawei.fitframework.ioc.lifecycle.bean;

/**
 * 表示Bean生命周期的依赖，实现了该接口的Bean不会被 {@link BeanLifecycleInterceptor} 拦截。
 *
 * @author 梁济时 l00815032
 * @since 2022-05-20
 */
public interface BeanLifecycleDependency {}
