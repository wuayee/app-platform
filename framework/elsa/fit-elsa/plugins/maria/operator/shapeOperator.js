/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 */

import {dataCommand, deleteCommand} from "../../../core/commands.js";

/**
 * shape操作器.
 *
 * @param page 页面对象.
 */
export const shapeOperator = (page, pageOperator) => {
    if (page === null || page === undefined) {
        throw new Error("page is null or undefined.");
    }

    const self = {};

    /**
     * 设置属性.
     *
     * @param id shape的唯一标识.
     * @param attributes 属性集合.
     */
    self.setAttributes = (id, attributes) => {
        if (!attributes) {
            return;
        }
        const shape = page.getShapeById(id);
        if (!shape) {
            throw new Error("shape[" + id + "] not exists.");
        }
        let values = {shape: shape};
        Object.getOwnPropertyNames(attributes).forEach(f => {
            values[f] = attributes[f];
        })
        dataCommand(page, [values]).execute(page);
    }

    self.remove = shapeId => {
        const shape = page.getShapeById(shapeId);
        if (!shape) {
            throw new Error("shape[" + shapeId + "] not exists.");
        }
        deleteCommand(page, [{shape: shape}]).execute();
    }

    self.copy = (shapeId, x, y) => {
        let data = page.copy([shapeId]);
        let pagePosition = pageOperator.screenToPagePosition({x, y});
        const offsetX = pagePosition.x - Math.min(...data.map(c => c.x));
        const offsetY = pagePosition.y - Math.min(...data.map(c => c.y));
        const topShapes = page.paste(data, (data) => data.x + offsetX, () => data.y + offsetY);
        topShapes && topShapes.forEach(s => s.select());
    }

    return self;
}