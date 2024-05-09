/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 */

import {decorator} from "./decorator.js";
import {eventTrigger} from "./eventTrigger.js";
import {Type} from "./decoratorConst.js";

const FLUSHING = new WeakMap();

/**
 * shape装饰器.
 *
 * @return {{}} 形状装饰器.
 */
export const shapeDecorator = () => {
    const self = decorator();

    /**
     * 对shape进行装饰.
     *
     * @param shape 形状对象.
     */
    self.decorate = (shape) => {
        return eventTrigger(resolve => {
            /**
             * 重写propertyChanged方法.当属性改变时需要发送通知.
             *
             * @param property 改变的属性.
             * @param value 当前值.
             * @param preValue 之前的值.
             */
            const propertyChanged = shape.propertyChanged;
            shape.propertyChanged = (property, value, preValue) => {
                propertyChanged.apply(shape, [property, value, preValue]);

                // 构建事件对象.
                self.eventManager.addEvent({
                    type: shape.isTypeof("page") ? Type.PAGE : Type.SHAPE, id: shape.id, property, value, preValue
                });

                // 宏任务，发送变更信息.
                if (!FLUSHING.get(shape.page)) {
                    FLUSHING.set(shape.page, true);
                    Promise.resolve().then(() => {
                        FLUSHING.set(shape.page, false);
                        resolve(self.eventManager.getEvents());
                        self.eventManager.clear();
                    });
                }
            };
        });
    };

    return self;
}