/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 */

import {decorator} from "./decorator.js";
import {eventTrigger} from "./eventTrigger.js";
import {DecoratorFactory} from "./decoratorFactory.js";
import {Type} from "./decoratorConst.js";

/**
 * page装饰器.
 *
 * @return {{}} 页面装饰器.
 */
export const pageDecorator = () => {
    const self = decorator();

    /**
     * 对page进行装饰.
     *
     * @param page 页面对象.
     */
    self.decorate = (page) => {
        return eventTrigger(resolve => {
            DecoratorFactory.getDecorator(Type.SHAPE).decorate(page).then(events => resolve(events));

            /**
             * 重写page的shapeCreated方法.
             *
             * @param shape 形状对象.
             */
            const shapeCreated = page.shapeCreated;
            page.shapeCreated = (shape) => {
                shapeCreated.apply(page, [shape]);
                DecoratorFactory.getDecorator(Type.SHAPE).decorate(shape).then(events => resolve(events));
            }
        });
    };

    return self;
}