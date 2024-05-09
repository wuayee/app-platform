/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 */

import {graphDecorator} from "./graphDecorator.js";
import {pageDecorator} from "./pageDecorator.js";
import {shapeDecorator} from "./shapeDecorator.js";
import {Type} from "./decoratorConst.js";

/**
 * 装饰器工厂.
 *
 * @author z00559346 张越
 * @since 2022.04.07
 */
export const DecoratorFactory = (() => {

    let self = {};
    const _DECORATORS = {
        graph: graphDecorator(), page: pageDecorator(), shape: shapeDecorator()
    }

    /**
     * 根据事件类型获取装饰器.
     *
     * @param eventType 事件类型.
     * @return {{}} 装饰器对象.
     */
    self.getDecorator = (eventType) => {
        switch (eventType) {
            case Type.GRAPH:
                return _DECORATORS.graph;
            case Type.PAGE:
                return _DECORATORS.page;
            case Type.SHAPE:
                return _DECORATORS.shape;
            default:
                throw new Error("illegal event type: " + eventType + ".");
        }
    }
    return self;
})();