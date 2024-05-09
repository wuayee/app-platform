/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 */

/**
 * graph操作器.
 *
 * @param graph 画布对象.
 */
export const graphOperator = (graph) => {
    if (graph === null || graph === undefined) {
        throw new Error("graph is null or undefined.");
    }

    const self = {};

    /**
     * 添加page.
     *
     * @param name page名称.
     * @param id page的唯一标识.
     * @param div 需要渲染的dom.
     * @param index 下标.
     * @return {*} page序列化后的数据.
     */
    self.addPage = (name, id, div, index) => {
        if (!graph.lastPageId) {
            graph.lastPageId = 0;
        }
        graph.lastPageId++;
        let page = graph.addPage("", "" + graph.lastPageId, div, index);
        page.backColor = '#3E4346';
        page.startAnimation();
        page.inHandDrawing = true;
        graph.activePage = page;
        return page.serialize();
    };

    /**
     * 删除页面.
     *
     * @param pageId 页面的唯一标识.
     */
    self.removePage = (pageId) => graph.removePageById(pageId);

    /**
     * 移动页面.
     *
     * @param pageId 页面的唯一标识.
     * @param index 将页面移动到指定的位置
     */
    self.changePageIndex = (pageId, index) => graph.changePageIndexById(pageId, index);

    self.getPages = () => {
        return graph.pages;
    }

    /**
     * 设置当前显示页面.
     *
     * @param pageId 页面的唯一标识.
     */
    self.setActivePage = (pageId) => {
        const index = graph.getPageIndex(pageId);
        graph.activePage = graph.edit(index);
        graph.activePage.startAnimation();
    };

    self.getActivePage = () => {
        return graph.activePage.serialize();
    }

    /**
     * 导入自定义shape.
     *
     * @param path 自定义shape的路径.
     * @param definedShapes 定义的shape的集合.
     * @return {*} import的后续操作.
     */
    self.importShape = (path, definedShapes) => {
        return graph.staticImport(() => import(/* webpackMode: "eager" */ path), definedShapes);
    };

    return self;
}