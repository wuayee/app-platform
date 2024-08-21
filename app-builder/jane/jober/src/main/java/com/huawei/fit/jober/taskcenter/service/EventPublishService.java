/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.service;

import modelengine.fitframework.event.Event;
import modelengine.fitframework.plugin.Plugin;

/**
 * 事件推送服务接口。
 *
 * @author 陈镕希
 * @since 2023-08-21
 */
public interface EventPublishService {
    /**
     * 发送Event。
     *
     * @param event the event
     */
    void sendEvent(Event event);

    /**
     * 获取插件。
     *
     * @return 插件
     */
    Plugin plugin();
}
