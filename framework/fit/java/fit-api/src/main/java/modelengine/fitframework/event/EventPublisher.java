/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 */

package modelengine.fitframework.event;

/**
 * 为事件提供发布程序。
 *
 * @author 梁济时
 * @since 2022-11-22
 */
public interface EventPublisher {
    /**
     * 发布事件。
     *
     * @param event 表示待发布的事件的 {@link Event}。
     * @param <E> 表示事件的类型。
     */
    <E extends Event> void publishEvent(E event);
}
