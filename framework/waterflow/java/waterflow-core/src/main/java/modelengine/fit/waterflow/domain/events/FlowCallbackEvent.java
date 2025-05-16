/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.domain.events;

import lombok.Getter;
import modelengine.fit.waterflow.domain.context.FlowContext;
import modelengine.fitframework.event.Event;

import java.util.List;

/**
 * 回调函数事件类
 *
 * @author 李哲峰
 * @since 1.0
 */
@Getter
public class FlowCallbackEvent<O> implements Event {
    private final List<FlowContext<O>> flowContexts;

    private final Object publisher;

    public FlowCallbackEvent(List<FlowContext<O>> flowContexts, Object publisher) {
        this.flowContexts = flowContexts;
        this.publisher = publisher;
    }

    @Override
    public Object publisher() {
        return this.publisher;
    }
}
