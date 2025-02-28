/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.dataengine.biz.service;

import modelengine.fit.jane.task.util.OperationContext;
import modelengine.fit.jober.common.event.TaskInstanceMetaDataEvent;
import modelengine.fit.jober.dataengine.rest.response.TaskInstanceMetaData;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.plugin.Plugin;

/**
 * 发布事件
 *
 * @author 晏钰坤
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
