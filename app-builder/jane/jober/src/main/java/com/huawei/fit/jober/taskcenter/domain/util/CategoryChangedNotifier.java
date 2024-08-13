/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.domain.util;

import com.huawei.fit.jane.task.util.OperationContext;
import com.huawei.fit.jober.taskcenter.domain.TaskEntity;
import com.huawei.fit.jober.taskcenter.domain.TaskInstance;
import com.huawei.fit.jober.taskcenter.domain.util.support.DefaultCategoryChangedNotifier;
import com.huawei.fit.jober.taskcenter.service.CategoryService;
import com.huawei.fitframework.broker.client.BrokerClient;

import java.util.Collection;

/**
 * 为类目变化提供通知程序。
 *
 * @author 梁济时
 * @since 2023-08-28
 */
public interface CategoryChangedNotifier extends Runnable {
    /**
     * 提示任务实例的类目发生变化。
     *
     * @param instance 表示类目发生变化的任务实例的 {@link TaskInstance}。
     * @param olds 表示变化前的类目的集合的 {@link Collection}{@code <}{@link String}{@code >}。
     * @param news 表示变化后的类目的集合的 {@link Collection}{@code <}{@link String}{@code >}。
     * @param context context 表示操作上下文的 {@link OperationContext}。
     * @return 表示当前通知程序的 {@link CategoryChangedNotifier}。
     */
    CategoryChangedNotifier notice(TaskInstance instance, Collection<String> olds, Collection<String> news,
            OperationContext context);

    /**
     * of
     *
     * @param broker broker
     * @param task task
     * @param categoryService categoryService
     * @return CategoryChangedNotifier
     */
    static CategoryChangedNotifier of(BrokerClient broker, TaskEntity task, CategoryService categoryService) {
        return new DefaultCategoryChangedNotifier(broker, task, categoryService);
    }
}

