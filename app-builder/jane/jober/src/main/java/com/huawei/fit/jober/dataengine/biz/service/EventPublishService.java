/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.dataengine.biz.service;

import com.huawei.fit.jane.task.util.OperationContext;
import com.huawei.fit.jober.common.event.TaskInstanceMetaDataEvent;
import com.huawei.fit.jober.dataengine.rest.response.TaskInstanceMetaData;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.plugin.Plugin;

/**
 * 发布事件
 *
 * @author 00693950
 * @since 2023/6/25
 */
@Component
public class EventPublishService {
    private final Plugin plugin;

    public EventPublishService(Plugin plugin) {
        this.plugin = plugin;
    }

    /**
     * 发送数据
     *
     * @param taskInfo 任务信息
     * @param context 表示用户信息上下文
     */
    public void sendData(TaskInstanceMetaData taskInfo, OperationContext context) {
        this.plugin.runtime().publisherOfEvents().publishEvent(new TaskInstanceMetaDataEvent(this, taskInfo, context));
    }
}
