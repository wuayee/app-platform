/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.taskcenter.service;

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
