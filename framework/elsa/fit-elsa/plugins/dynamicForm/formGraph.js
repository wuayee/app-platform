import {defaultGraph} from "../../core/defaultGraph.js";
import {sleep} from "../../common/util.js";

/**
 * from画布.
 *
 * @param div dom元素.
 * @param title 名称.
 */
export const formGraph = (div, title) => {
    const self = defaultGraph(div, title);
    self.type = "FORM";
    self.pageType = "formPage";

    /**
     * 导入form相关依赖.
     *
     * @override
     */
    const initialize = self.initialize;
    self.initialize = async () => {
        let finished = false;
        self.staticImport(() => import(/* webpackMode: "eager" */ "./form.js"))
            .staticImport(() => import(/* webpackMode: "eager" */ "./formPage.js"))
            .staticImport(() => import(/* webpackMode: "eager" */ "./component/htmlFile.js"))
            .staticImport(() => import(/* webpackMode: "eager" */ "./component/llmSelector.js"))
            .staticImport(() => import(/* webpackMode: "eager" */ "./component/htmlMind.js"))
            .staticImport(() => import(/* webpackMode: "eager" */ "./component/htmlVideoSummary.js"))
            .staticImport(() => import(/* webpackMode: "eager" */ "./component/htmlTaskDisplay.js"))
            .staticImport(() => import(/* webpackMode: "eager" */ "./component/htmlRadioBox.js"))
            .staticImport(() => import(/* webpackMode: "eager" */ "./component/buttons.js"))
            .staticImport(() => import(/* webpackMode: "eager" */ "./component/htmlIframe.js"))
            .staticImport(() => import(/* webpackMode: "eager" */ "./component/htmlFileDownload.js"))
            .staticImport(() => import(/* webpackMode: "eager" */ "./component/htmlVideo.js"))
            .staticImport(() => import(/* webpackMode: "eager" */ "./component/htmlAudio.js"))
            .staticImport(() => import(/* webpackMode: "eager" */ "./component/htmlReportCharts.js"))
            .staticImport(() => import(/* webpackMode: "eager" */ "./component/htmlText.js"))
            .staticImport(() => import(/* webpackMode: "eager" */ "./component/htmlPptDisplay.js"))
            .staticImport(() => import(/* webpackMode: "eager" */ "./component/htmlTextLink.js"))
            .staticImport(() => import(/* webpackMode: "eager" */ "./component/htmlReportTitle.js"))
            .staticImport(() => import(/* webpackMode: "eager" */ "./component/htmlReportHeader.js"))
            .staticImport(() => import(/* webpackMode: "eager" */ "./component/htmlReportTable.js"))
            .staticImport(() => import(/* webpackMode: "eager" */ "./component/htmlReportSummary.js"))
            .staticImport(() => import(/* webpackMode: "eager" */ "./component/htmlReport.js"))
            .staticImport(() => import(/* webpackMode: "eager" */ "./modeInitializer.js"))
            .then(() => {
                finished = true;
            });

        while (!finished) {
            await sleep(5);
        }
        return initialize.apply(self);
    };

    /**
     * runPage.
     *
     * @param pageName 页面名称.
     * @param sheetConfigData 数据.
     * @param mode 模式.
     * @param next 下一步操作.
     */
    self.runPage = (pageName, sheetConfigData, mode, next) => {
        const page = self.addPage(pageName, undefined, undefined, null, null, mode);
        page.loadForm(sheetConfigData);
        page.run(next.data, next.submittedCallback, next.onSubmitCallback);
        return page;
    };

    return self;
};