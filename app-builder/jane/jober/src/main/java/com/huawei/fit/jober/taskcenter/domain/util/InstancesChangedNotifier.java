/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.domain.util;

import com.huawei.fit.jane.task.util.OperationContext;
import com.huawei.fit.jober.taskcenter.domain.TaskEntity;
import com.huawei.fit.jober.taskcenter.domain.TaskInstance;
import com.huawei.fit.jober.taskcenter.domain.util.support.DefaultInstancesChangedNotifier;
import modelengine.fitframework.broker.client.BrokerClient;

/**
 * 为任务实例属性变化提供通知程序。
 *
 * @author 梁济时
 * @since 2023-08-28
 */
public interface InstancesChangedNotifier extends Runnable {
    /**
     * notice
     *
     * @param newInstance current
     * @param oldInstance origin
     * @param context context
     * @return InstancesChangedNotifier
     */
    InstancesChangedNotifier notice(TaskInstance newInstance, TaskInstance oldInstance, OperationContext context);

    /**
     * of
     *
     * @param brokerClient brokerClient
     * @param task task
     * @return InstancesChangedNotifier
     */
    static InstancesChangedNotifier of(BrokerClient brokerClient, TaskEntity task) {
        return new DefaultInstancesChangedNotifier(brokerClient, task);
    }
}
