/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fit.jober.aipp.events;


import modelengine.fitframework.event.Event;

/**
 * 应用创建事件。
 *
 * @author 李金绪
 * @since 2024-12-05
 */
public class AppCreatingEvent implements Event {
    private final Object publisher;

    public AppCreatingEvent(Object publisher) {
        this.publisher = publisher;
    }

    @Override
    public Object publisher() {
        return this.publisher;
    }
}
