/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

import {rectangle} from "../../core/rectangle.js";
import {rectangleDrawer} from "../../core/drawers/rectangleDrawer";
import * as echarts from 'echarts';

/**
 * 使用eCharts库绘制的图表
 * @param id
 * @param x
 * @param y
 * @param width
 * @param height
 * @param parent
 * @param drawer
 * @returns {WorkerGlobalScope | Window}
 */
const eChart = (id, x, y, width, height, parent, drawer) => {
    const self = rectangle(id, x, y, width, height, parent, eChartDrawer);
    self.type = "eChart";
    self.hideText = true;
    self.option = undefined;
    self.serializedFields.batchAdd("option");

    /**
     * 删除eChart的自身，以及释放图表持有的实例，防止内存泄漏
     *
     * @param source shape
     * @returns {any}
     */
    const remove = self.remove;
    self.remove = source => {
        self.drawer.dispose();
        return remove.call(self, source);
    };

    /**
     * 给图表设置数据，触发绘制
     *
     * @param option 图表的绘制数据和操作
     */
    self.setOption = (option) => {
        if (option && typeof option === 'object') {
            self.option = option;
            self.drawer.drawChart();
        }
    };

    /**
     * 撤销删除，这里需要重新走一遍创建当前shape eChart实例的过程
     *
     * @param host page
     */
    const undoRemove = self.undoRemove;
    self.undoRemove = (host) => {
        undoRemove.apply(self, [host]);
        self.drawer.initChartInstance();
        self.drawer.drawChart();
    };

    return self;
};

const eChartDrawer = (shape, div, x, y) => {
    const self = rectangleDrawer(shape, div, x, y);
    self.type = "eCharts drawer";
    self.eChartDom = self.createElement("div", "eChartContainer: " + shape.id);
    self.eChartDom.style.width = "100%";
    self.eChartDom.style.height = "100%";
    self.eChartDom.style.pointerEvents = 'auto';
    self.parent.appendChild(self.eChartDom);

    /**
     * 初始化当前的drawer，此方法调用在上面属性设置之后
     */
    const initialize = self.initialize;
    self.initialize = () => {
        initialize.apply(self);
        self.initChartInstance();
        self.drawChart();
    };

    /**
     * 释放eChart实例
     */
    self.dispose = () => {
        self.chartInstance && self.chartInstance.dispose();
    };

    /**
     * 初始化eChart实例
     */
    self.initChartInstance = () => {
        self.chartInstance = echarts.init(self.eChartDom, null, {
            renderer: 'canvas',
            useDirtyRect: false
        });
        self.observeResize();
    };

    /**
     * 监听rectangle dom元素宽高的变化，对图表做相应的resize
     */
    self.observeResize = () => {
        const observer = new ResizeObserver(() => {
            self.chartInstance && self.chartInstance.resize();
        });
        observer.observe(self.parent);
    };

    /**
     * 给eChart设置数据和类型，用于图表的绘制
     */
    self.drawChart = () => {
        if (shape.option && typeof shape.option === 'object') {
            self.chartInstance.setOption(shape.option);
        }
    };

    return self;
};

export {eChart, eChartDrawer, echarts};