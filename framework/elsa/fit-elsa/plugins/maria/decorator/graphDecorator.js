/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 */

import {eventTrigger} from "./eventTrigger.js";
import {decorator} from "./decorator.js";
import {Type} from "./decoratorConst.js";
import {DecoratorFactory} from "./decoratorFactory.js";

/**
 * 画布装饰器.
 *
 * @return {{}} 画布装饰器.
 */
export const graphDecorator = () => {
    const self = decorator();

    /**
     * 对画布进行装饰.
     *
     * @param graph 画布对象.
     * @return {{}} 触发器对象.
     */
    self.decorate = (graph) => {
        return eventTrigger((resolve) => {
            /**
             * todo@xiafei，是否需要这个回调，用户触发的时候自动就知道了
             *
             * 重写display方法.
             * 1、当页面发生变化时，需要发消息事件通知外部业务.
             *
             * @param index 下标.
             * @param div dom元素.
             */
            const display = graph.display;
            graph.display = (index, div) => {
                const prePage = graph.activePage.serialize();
                display.apply(graph, [index, div]);
                self.eventManager.addEvent({
                    type: Type.GRAPH,
                    id: graph.id,
                    property: "set_active_page",
                    value: graph.activePage.serialize(),
                    preValue: prePage
                });
                resolve(self.eventManager.getEvents());
                self.eventManager.clear();
            }

            /**
             * todo@xiafei，是否需要这个回调，用户触发的时候自动就知道了
             *
             * 重写addPage方法.
             * 1、当graph执行addPage操作时，需要通知外部发生了变化.
             *
             * @param name page名称.
             * @param id page的唯一标识.
             * @param targetDiv 目标dom.
             * @param index 下标.
             */
            const addPage = graph.addPage;
            graph.addPage = (name, id, targetDiv, index) => {
                const page = addPage.apply(graph, [name, id, targetDiv, index]);
                self.eventManager.addEvent({
                    type: Type.GRAPH, id: graph.id, property: "add_page", value: page.serialize()
                });
                resolve(self.eventManager.getEvents());
                self.eventManager.clear();
                return page;
            }

            /**
             * todo@xiafei，是否需要这个回调，用户触发的时候自动就知道了
             *
             * 重写removePage方法.
             * 1、当graph执行removePage操作时，需要通知外部发生了变化.
             *
             * @param index 下标.
             * @param isCoEditing 是否在协同编辑.
             */
            const removePage = self.removePage;
            self.removePage = (index, isCoEditing) => {
                const pageData = graph.getPageData(index);
                removePage.apply(graph, [index, isCoEditing]);
                self.eventManager.addEvent({
                    type: Type.GRAPH, id: graph.id, property: "remove_page", preValue: pageData
                });
                resolve(self.eventManager.getEvents());
                self.eventManager.clear();
            }

            /**
             * 重写newPage方法，为所有新增的page进行装饰(包括翻页时).
             *
             * @param targetDiv 目标dom.
             * @param mode 模式.
             * @param name page的名称.
             * @param id page的唯一标识.
             */
            const newPage = graph.newPage;
            graph.newPage = (targetDiv, mode, name, id) => {
                const page = newPage.apply(graph, [targetDiv, mode, name, id]);
                DecoratorFactory.getDecorator(Type.PAGE).decorate(page).then(events => resolve(events));
                return page;
            }
        });
    };

    return self;
}