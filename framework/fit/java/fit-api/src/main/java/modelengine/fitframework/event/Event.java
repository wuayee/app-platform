/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 */

package modelengine.fitframework.event;

/**
 * 为应用程序提供事件定义。
 *
 * @author 梁济时
 * @since 2022-11-18
 */
public interface Event {
    /**
     * 获取事件的发布者。
     *
     * @return 表示事件发布者的 {@link Object}。
     */
    Object publisher();
}
