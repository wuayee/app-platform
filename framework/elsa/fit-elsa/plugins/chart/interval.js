/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

import {rectangle} from "../../core/rectangle.js";
import {customizedDrawer} from "../../core/drawers/htmlDrawer.js";

/**
 * 柱状图，使用阿里G2图库
 * @param id
 * @param x
 * @param y
 * @param width
 * @param height
 * @param parent
 * @param drawer
 * @returns {Atom}
 */
const intervalChart = (id, x, y, width, height, parent, drawer) => {
    const chartDrawer = customizedDrawer("div", (dom, d) => {
        const shape = d.shape;
        shape.element = dom;
        dom.id = `${shape.page.div.id}_${shape.id}_chart_container`;
        dom.style.pointerEvents = "auto";

        d.chart = new G2.Chart({
            container: dom,
            theme: 'classic',
            autoFit: true
        });

        d.chartInstance = d.chart
            .interval();
    });

    const self = rectangle(id, x, y, width, height, parent, chartDrawer);
    self.type = "intervalChart";
    self.hideText = true;

    self.addDetection(["width", "height"], (property, value, preValue) => {
        if (self[property] === preValue) return;
        self.drawer.chart && self.drawer.chart.forceFit();
    });

    const invalidateAlone = self.invalidateAlone;
    self.invalidateAlone = () => {
        invalidateAlone.apply(self);
        if(self.tag && self.tag.data && self.tag.setting) {
            let instance = self.drawer.chartInstance.data(self.tag.data);
            const setting = self.tag.setting;

            //分组展示
            if(setting.dodgeX) {
                instance = self.drawer.chartInstance.data(self.tag.data)
                    .transform({ type: 'dodgeX' });
            }
            // 按照某个维度排序展示
            if(setting.sort) {
                instance.transform(setting.sort)
            }

            instance.encode('x', self.tag.setting.x)
                .encode('y', self.tag.setting.y)
                .encode('color', 'genre');
            self.drawer.chart.forceFit();
        }
    }

    return self;
};


export {intervalChart};