import {defaultGraph} from "../../core/defaultGraph.js";

/**
 * 结构化文档对应的graph.
 *
 * @author z00559346 张越.
 */
export const docGraph = (div, title) => {
    const self = defaultGraph(div, title);
    self.type = "docGraph";
    self.pageType = "docPage";
    self.setting.borderColor = "gray";
    div && (div.style.overflow = "visible");

    self.createDocument = title => {
        return self.addPage(title);
    }

    /**
     * 重写该方法，导入 {@link #docGraph} 特有的图形.
     *
     * @override
     */
    const initialize = self.initialize;
    self.initialize = async () => {
        await initialize.apply(self);
        return self.staticImport(() => import(/* webpackMode: "eager" */ "./docSection.js"))
            .staticImport(() => import(/* webpackMode: "eager" */ "./docPage.js"))
            .staticImport(() => import(/* webpackMode: "eager" */ "./docFrame.js"))
            .staticImport(() => import(/* webpackMode: "eager" */ "./docPen.js"));
    };

    /**
     * 在docGraph中，添加page时，需要删除第一次添加的page的command。
     * 目前没有添加第二页的需求，因此这里默认addPage时，都进行删除.
     *
     * @inheritDoc
     * @override
     */
    const addPage = self.addPage;
    self.addPage = (name, id, targetDiv, index, data) => {
        const page = addPage.apply(self, [name, id, targetDiv, index, data]);
        self.getHistory().removeLastCommand("pageAdd");
        return page;
    }

    return self;
}