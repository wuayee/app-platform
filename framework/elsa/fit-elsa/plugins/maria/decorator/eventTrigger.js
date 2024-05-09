/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 */

/**
 * 事件触发器.
 *
 * @param callback 回调.
 * @return {{}} 触发器对象.
 */
export const eventTrigger = (callback) => {
    const self = {};
    Promise.resolve().then(() => callback((events) => self.eventCallback && self.eventCallback(events)));

    /**
     * 注册event处理事件.
     *
     * @param eventCallback event处理事件.
     */
    self.then = (eventCallback) => {
        self.eventCallback = eventCallback;
    }

    return self;
}