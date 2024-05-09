/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 */

/**
 * 事件管理器.
 *
 * @return {{}} 事件管理器对象.
 */
export const eventManager = () => {
    const self = {};
    const eventMap = new Map();

    /**
     * 添加事件.
     *
     * @param update 改变数据.
     */
    self.addEvent = (update) => {
        let event = eventMap.get(update.id);
        if (!event) {
            event = initEvent(update);
            eventMap.set(update.id, event);
        }
        event.changes.push({property: update.property, value: update.value, preValue: update.preValue});
    }

    const initEvent = (update) => {
        return {type: update.type, id: update.id, changes: []};
    }

    /**
     * 获取所有事件.
     *
     * @return {[]} 事件数组.
     */
    self.getEvents = () => {
        return Array.from(eventMap.values());
    }

    /**
     * 清空事件.
     */
    self.clear = () => {
        eventMap.clear();
    }

    return self;
}