/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 */

import {addCommand} from "../../../core/commands.js";
import {wantedShape} from "../../../core/page.js";

/**
 * page操作器.
 *
 * @param page 页面对象.
 */
export const pageOperator = (page) => {
    if (page === null || page === undefined) {
        throw new Error("page is null or undefined.");
    }

    const self = {};

    /**
     * 设置想要的shape的类型.
     *
     * @param type shape的类型.
     * @param properties shape的属性
     */
    self.setWantedShape = (type, properties) => {
        self.wantedShape = wantedShape(type, properties);
    };

    /**
     * 设置属性.
     *
     * @param attributes 属性集合.
     */
    self.setAttributes = (attributes) => {
        if (!attributes) {
            return;
        }
        Object.getOwnPropertyNames(attributes).forEach(f => page[f] = attributes[f]);
    }

    /**
     * 创建shape.
     *
     * @param shapeData shape数据.
     */
    self.createShape = (shapeData) => {
        let focusedShapes = page.getFocusedShapes();
        if (focusedShapes.length > 0) {
            focusedShapes.forEach(fs => {
                return fs.isFocused = false;
            });
        }
        let position = self.screenToPagePosition(shapeData);
        let newShape = page.createNew(shapeData.type, position.x, position.y, (page.shapes.length + 1).toString());
        newShape.width = self.screenToPageSizeX(shapeData.width);
        newShape.height = self.screenToPageSizeY(shapeData.height);
        if (shapeData.borderColor) {
            newShape.borderColor = shapeData.borderColor;
            newShape.focusBorderColor = shapeData.borderColor;
            newShape.mouseInColor = shapeData.borderColor;
        }
        if (shapeData.borderWidth) {
            newShape.borderWidth = shapeData.borderWidth;
        }
        if (shapeData.backColor) {
            newShape.backColor = shapeData.backColor;
            newShape.focusBackColor = shapeData.backColor;
        }
        if (shapeData.cornerRadius) {
            newShape.cornerRadius = shapeData.cornerRadius;
        }
        if (shapeData.dashWidth) {
            newShape.dashWidth = shapeData.dashWidth;
        }
        newShape.beginArrow = !!shapeData.beginArrow;
        newShape.endArrow = !!shapeData.endArrow;
        if (!newShape.isTypeof('svg')) {
            newShape.text = '';
        }
        newShape.editable = false;
        newShape.isFocused = true;
        newShape.invalidate();
        addCommand(page, [{shape: newShape}]);
        return newShape.id;
    };

    /**
     * 通过id删除shape.
     *
     * @param id shape的唯一标识.
     */
    self.removeShape = (id) => {
        page.getShapeById(id).remove();
    }

    function breakGroups(shapeIds) {
        let allShapes = [];
        let shapes = shapeIds.split(',').map(shapeId => page.getShapeById(shapeId));
        shapes.filter(shape => !shape.isTypeof("group")).forEach(shape => allShapes.push(shape))
        shapes.filter(shape => shape.isTypeof("group")).forEach(shape => {
            let subShapes = shape.break();
            subShapes.filter(subShape => !subShape.isTypeof("group")).forEach(subShape => allShapes.push(subShape))
            subShapes.filter(subShape => subShape.isTypeof("group")).forEach(subShape => subShape.break().forEach(sub => allShapes.push(sub)))
        });
        return allShapes;
    }

    /**
     * 将给定的图形组合，仅支持一层组合，不支持组合嵌套，嵌套的组合将被打开，最终组合为同一层次的组合
     *
     * 存在几种可能
     * 1. 传入的图形全都是普通图形 -> 直接组合即可
     * 2. 传入的图形包含组合过的图形 -> 需将组合过的图形先解组，再组合全部的图形
     * 3. 传入的图形包含选择框，选择框中的图形均是普通的图形 -> 先解除选择框的组合，再组合全部的图形
     * 4. 传入的图形包含选择框，选择框中的图形存在组合过的图形 -> 先解除选择框的组合，再解除其中的组合图形，最后再组合全部的图形
     *
     * @param shapeIds 待组合的shape列表，逗号分隔id
     */
    self.group = (shapeIds) => {
        let allShapes = breakGroups(shapeIds);
        page.group(allShapes);
    }

    /**
     * 解除给定的图形列表的组合，无论这些图形之前采用何种方式组合及选中，最终将完全解除组合，操作过的图形不存在任何组合的情况
     *
     * @param shapeIds 待解除组合的shape列表，逗号分隔id
     */
    self.ungroup = (shapeIds) => {
        let allShapes = breakGroups(shapeIds);
        // todo 代码重复，待提取
        if (allShapes.length > 1) {
            // container作为一个整体加入
            let containerIds = new Set();
            allShapes.filter(shape => shape.isTypeof("container")).forEach(shape => containerIds.add(shape.id));
            allShapes = allShapes.filter(shape => !containerIds.has(shape.container));
        }
        if (allShapes.length === 1) {
            allShapes[0].select();
            return;
        }
        // 构建一个选择框
        let g = page.createNew("freeLineSelection", 0, 0);
        g.select();
        g.group(allShapes);
    }

    /**
     * 通过id移动shape的层级
     *
     * @param id 待移动的shape的id
     * @param type 目标层级 up/down/top/bottom
     */
    self.moveShapeIndex = (id, type) => {
        let shape = page.getShapeById(id);
        switch (type) {
            case "up":
                page.moveIndexAfter(shape, shape.getIndex() + 1);
                break;
            case "down":
                page.moveIndexBefore(shape, shape.getIndex() - 1);
                break;
            case "top":
                page.moveIndexTop(shape);
                break;
            case "bottom":
                page.moveIndexBottom(shape);
                break;
            default:
                break;
        }
    }

    /**
     * 移动画布
     * @param offsetX 偏移量x
     * @param offsetY 偏移量y
     */
    self.move = (offsetX, offsetY) => {
        offsetX = self.screenToPageSizeX(offsetX);
        offsetY = self.screenToPageSizeY(offsetY);
        page.moveTo(page.x + offsetX, page.y + offsetY);
    }

    /**
     * 移动画布
     * @param x 目标位置
     * @param y 目标位置
     */
    self.moveTo = (x, y) => {
        let position = self.screenToPagePosition({x: x, y: y});
        page.moveTo(position.x, position.y);
    }

    /**
     * 缩放画布
     * @param rate 缩放的比例，增量值，放大为正数，缩小为负数
     * @param centerX 缩放中心点，可以不传，以0,0为原点缩放
     * @param centerY 缩放中心点，可以不传，以0,0为原点缩放
     */
    self.zoom = (rate, centerX, centerY) => {
        let position = self.screenToPagePosition({x: centerX, y: centerY});
        page.zoom(rate, position.x, position.y);
    }

    /**
     * 缩放画布
     * @param rate 缩放的比例
     * @param centerX 缩放中心点，可以不传，以0,0为原点缩放
     * @param centerY 缩放中心点，可以不传，以0,0为原点缩放
     */
    self.zoomTo = (rate, centerX, centerY) => {
        let position = self.screenToPagePosition({x: centerX, y: centerY});
        console.log('else.page.zoomTo', rate, position.x, position.y, page.width, page.height, page.x, page.y);
        page.zoomTo(rate, rate, position.x, position.y);
    }

    /**
     * 取消当前正在进行的手写
     * 当前用于处理手写和缩放冲突后的回退
     */
    self.cancelCurrentHandWriting = () => {
        page.cancelMouseDrag();
    }

    self.calcScreenPosition = (position) => {
        return {
            x: (position.x + page.x) * page.scaleX,
            y: (position.y + page.y) * page.scaleY
        };
    }

    self.screenToPagePosition = (position) => {
        return {
            x: position.x / page.scaleX - page.x,
            y: position.y / page.scaleY - page.y
        };
    }

    self.calcScreenSizeX = (sizeX) => {
        return sizeX * page.scaleX;
    }

    self.screenToPageSizeX = (sizeX) => {
        return sizeX / page.scaleX;
    }

    self.calcScreenSizeY = (sizeY) => {
        return sizeY * page.scaleY;
    }

    self.screenToPageSizeY = (sizeY) => {
        return sizeY / page.scaleY;
    }

    self.calcRealPosition = (positions) => {
        let result = [];
        if (!positions || positions.length === 0) {
            return result;
        }
        positions.forEach(position => {
            result.push(self.calcScreenPosition(position));
        })
        return result;
    }

    self.clear = () => {
        page.clearAllShapes();
    }

    self.redo = () => {
        page.graph.getHistory().redo(page);
    }

    self.canRedo = () => {
        return page.graph.getHistory().canRedo();
    }

    self.undo = () => {
        page.graph.getHistory().undo(page);
    }

    self.canUndo = () => {
        return page.graph.getHistory().canUndo();
    }

    return self;
}