/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 */

import {DecoratorFactory} from "./decorator/decoratorFactory.js";
import {Type} from "./decorator/decoratorConst.js";
import {graph} from "../../core/graph.js";
import {whiteBoard} from "../whiteBoard/whiteBoard.js";
import {graphOperator} from "./operator/graphOperator.js";
import {pageOperator} from "./operator/pageOperator.js";
import {shapeOperator} from "./operator/shapeOperator.js";
import {eventOperator} from "./operator/eventOperator.js";
import {EVENT_TYPE} from "../../common/const.js";

/**
 * 交给外部用户使用的核心画布.不提供存储功能.
 */
export const MARIA = (() => {
    const self = {};
    self.graph = graph;
    self.whiteBoard = whiteBoard;
    let selfGraph = null;

    /**
     * 获取画布操作器.
     *
     * @return {{}} 画布操作器对象.
     */
    self.getGraphOperator = () => {
        return graphOperator(selfGraph);
    }

    /**
     * 获取页面操作器.
     *
     * @return {{}} 页面操作器对象.
     */
    self.getPageOperator = () => {
        return pageOperator(selfGraph.activePage);
    }

    /**
     * 获取图形操作器.
     *
     * @return {{}} 图形操作器对象.
     */
    self.getShapeOperator = () => {
        return shapeOperator(selfGraph.activePage, self.getPageOperator());
    }

    /**
     * 获取画布操作器.
     *
     * @return {{}} 画布操作器对象.
     */
    self.getEventOperator = () => {
        return eventOperator(selfGraph.activePage);
    }

    /**
     * 渲染指定dom.
     *
     * @param graphData 待渲染的数据.
     * @param domId dom的唯一标识.
     * @param onChange 核心画布发生变化时的监听事件.
     */
    self.render = async (graphData, domId, onChange) => {
        const dom = document.getElementById(domId);
        selfGraph = self[graphData.type](dom, graphData.title);
        await selfGraph.initialize();
        await selfGraph.import('../whiteBoard/whiteBoard.js');
        await selfGraph.import('../whiteBoard/selection.js');
        deserialize(graphData);
        DecoratorFactory.getDecorator(Type.GRAPH).decorate(selfGraph).then(events => onChange(events));
        if (selfGraph.pages.length > 0) {
            const p = selfGraph.edit(0, dom);
            p.startAnimation();
            p.backColor = 'black';
        }
        return selfGraph.id;
    };

    const deserialize = (data) => {
        if (!data) {
            return;
        }
        if (data.pages) {
            data.pages.forEach(p => p.shapes.orderBy(s => s.index));
            data.pages.orderBy(page => page.index);
        }
        selfGraph.deSerialize(data);
    }

    /**
     * 新增graph.
     *
     * @param graphType 画布类型.
     * @param domId dom的唯一标识.
     * @param onChange 画布改变后的回调事件.
     * @return {Promise<void>} Promise对象.
     */
    self.newGraph = async (graphType, domId, onChange) => {
        return await self.render({type: graphType, title: ""}, domId, onChange);
    }

    /**
     * 获取graph的全部数据.
     *
     * @return {*} graph的数据.
     */
    self.getGraph = () => selfGraph.serialize();

    /**
     * 通过id获取对应图形的缩略图.
     *
     * @param id 图形id.
     */
    self.getImageById = (id) => {
        //todo@xiafei 可能需要修改辉哥的toImage方法.
    };

    /**
     * 通过条件获取shape数据.
     *
     * @return {*[]|*} shape数据集合.
     */
    self.getFocusedShapes = () => {
        if (selfGraph === null) {
            return [];
        }
        return selfGraph.activePage.getFocusedShapes().map(shape => {
            let serialize = shape.serialize();
            serialize.focus = shape.isFocused;
            serialize.lock = !!shape.protected;
            return serialize;
        });
    };

    self.addFocusedShapeChangeListener = (handler) => {
        selfGraph.addEventListener(EVENT_TYPE.FOCUSED_SHAPE_CHANGE, handler);
    }

    self.addFocusedShapesListener = (handler) => {
        selfGraph.addEventListener(EVENT_TYPE.FOCUSED_SHAPES_CHANGE, handler);
    }

    self.addShapeMovedListener = (handler) => {
        selfGraph.addEventListener(EVENT_TYPE.SHAPE_MOVED, handler);
    }

    self.addShapeResizedListener = (handler) => {
        selfGraph.addEventListener(EVENT_TYPE.SHAPE_RESIZED, handler);
    }

    self.addPageLongClick = (handler) => {
        selfGraph.addEventListener(EVENT_TYPE.PAGE_LONG_CLICK, handler);
    }

    self.addShapeLongClick = (handler) => {
        selfGraph.addEventListener(EVENT_TYPE.SHAPE_LONG_CLICK, handler);
    }

    self.addTouchStartListener = (handler) => {
        selfGraph.addEventListener(EVENT_TYPE.TOUCH_START, handler);
    }

    self.addTouchEndListener = (handler) => {
        selfGraph.addEventListener(EVENT_TYPE.TOUCH_END, handler);
    }

    self.addPageHistoryListener = (handler) => {
        selfGraph.addEventListener(EVENT_TYPE.PAGE_HISTORY, handler);
    }

    self.addPageDirtyListener = (handler) => {
        selfGraph.addEventListener(EVENT_TYPE.PAGE_DIRTY, handler);
    }

    return self;
})();