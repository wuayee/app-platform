/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 */

import {eventManager} from "./eventManager.js";

/**
 * 装饰器.
 *
 * @return {{}} 装饰器对象.
 */
export const decorator = () => {
    const self = {};
    self.eventManager = eventManager();

    /**
     * 对elsa对象进行装饰.
     *
     * @param elsaObj elsa对象.
     */
    self.decorate = (elsaObj) => {
    };

    return self;
}