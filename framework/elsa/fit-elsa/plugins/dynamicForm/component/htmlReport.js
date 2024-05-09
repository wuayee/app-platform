import {htmlDiv} from "../form.js";
import {ALIGN, DOCK_MODE} from "../../../common/const.js";
import {deleteRegion} from "../regions/deleteRegion.js";
import {reportEditRegion} from "../regions/reportEditRegion.js";

/**
 * 创建表格.
 *
 * @param report 报告对象.
 * @param shapeStore 图形仓库.
 * @param chartData 数据.
 */
const createTable = (report, shapeStore, chartData) => {
    const table = report.page.createShape("htmlReportTable", 0, 0);
    table.container = report.id;
    table.formDataRetrieved(shapeStore, chartData);
};

/**
 * 创建图表.
 *
 * @param report 报告对象.
 * @param shapeStore 图形仓库.
 * @param chartData 数据.
 */
const createChart = (report, shapeStore, chartData) => {
    const charts = report.page.createShape("htmlReportCharts", 0, 0);
    charts.container = report.id;
    charts.formDataRetrieved(shapeStore, chartData);
};

const typeCreatorMap = new Map();
typeCreatorMap.set("TABLE", createTable);
typeCreatorMap.set("BAR", createChart);
typeCreatorMap.set("LINE", createChart);
typeCreatorMap.set("PIE", createChart);
typeCreatorMap.set("MIX_LINE_BAR", createChart);


/**
 * 报表.
 *
 * @override
 */
const htmlReport = (id, x, y, width, height, parent, drawer) => {
    const self = htmlDiv(id, x, y, width, height, parent, drawer);
    self.type = "htmlReport";
    self.dockAlign = ALIGN.TOP;
    self.dockMode = DOCK_MODE.VERTICAL;
    self.autoFit = true;
    self.minHeight = 60;
    self.componentId = "report_" + self.id;
    self.meta = [{
        key: self.componentId, type: 'string', name: 'report_' + self.id
    }];
    self.deleteRegion = deleteRegion(self);

    /**
     * @override
     */
    self.childAllowed = (s) => {
        return s && s.type.startsWith("htmlReport");
    };

    /**
     * @override
     */
    self.initialize = () => {
        // 创建大标题.
        const header = self.page.createShape("htmlReportHeader");
        header.container = self.id;
        header.text = "经营分析报告";
        header.focusBackColor = "transparent";
        header.selectable = false;
        header.beginEdit();
    };

    /**
     * 是否可以编辑报表.runtime模式下可以触发编辑.
     *
     * @return {boolean} true/false.
     */
    self.getEditable = () => {
        return false;
    };

    /**
     * @override
     */
    self.getData = () => {
        const shapes = self.getShapes();
        if (shapes.length === 0) {
            return {};
        }

        const ans = [];

        shapes.forEach(s => {
            if (s.isTypeof("htmlReportHeader")) {
                return;
            }
            if (s.isTypeof("htmlReportTable") || s.isTypeof("htmlReportCharts")) {
                // 如果数据长度为0，表明数据出现问题，抛出异常.
                if (ans.length === 0) {
                    throw new Error("report structure is invalid.");
                }
                const qa = ans[ans.length - 1];
                const _data = s.getData();
                qa.answer.answer = _data.answer;
                qa.answer.type = _data.type;
                qa.answer.chartType.push(_data.chartType);
                qa.answer.chartData.push(_data.chartData);
                qa.answer.chartTitle.push(_data.chartTitle);
                qa.answer.chartAnswer.push(_data.chartAnswer);
            } else if (s.isTypeof("htmlReportSummary")) {
                ans[ans.length - 1].answer.chartSummary.push(s.getData());
            } else {
                const qa = {
                    answer: {
                        answer: "", chartType: [], chartData: [], chartTitle: [], chartAnswer: [], chartSummary: []
                    }
                };
                ans.push(qa);
                qa.question = s.getData();
            }
        });

        const result = {};
        result[self.componentId] = ans;
        return result;
    };

    /**
     * @override
     */
    self.formDataRetrieved = (shapeStore, data) => {
        if (!data || !data[self.componentId]) {
            return;
        }

        const reportData = JSON.parse(data[self.componentId]);
        if (!Array.isArray(reportData)) {
            throw new Error("reportData must be an array.");
        }

        if (reportData.length === 0) {
            return;
        }

        self.page.ignoreReact(() => {
            reportData.forEach(rd => {
                // 创建标题.
                const title = self.page.createShape("htmlReportTitle", 0, 0);
                title.container = self.id;
                title.formDataRetrieved(shapeStore, rd.question);

                // 遍历answer中的类型创建对应的图形.
                const answer = rd.answer;
                answer.chartType.forEach((type, index) => {
                    const chartData = {
                        chartType: type,
                        chartTitle: answer.chartTitle[index],
                        chartData: answer.chartData[index],
                        chartAnswer: answer.chartAnswer[index],
                        answer: answer.answer,
                        type: answer.type
                    };
                    const creator = typeCreatorMap.get(type);
                    if (!creator) {
                        throw new Error("type[" + type + "] is not supported.");
                    }
                    creator(self, shapeStore, chartData);

                    // 创建一个summary.
                    const chartSummary = self.page.createShape("htmlReportSummary", 0, 0);
                    chartSummary.container = self.id;
                    chartSummary.formDataRetrieved(shapeStore, answer.chartSummary[index]);
                });
            });
        });
    };

    /**
     * @override
     */
    self.formLoaded = () => {
        self.createEditRegion();
    };

    /**
     * 创建编辑region.
     */
    self.createEditRegion = () => {
        reportEditRegion(self, () => self.width - 40, () => 15);
    };

    /**
     * 不需要遍历子元素.
     *
     * @override
     */
    self.loadCustomizedData = (data) => {
        self.formDataRetrieved(self.page.shapeStore, data);
    };

    return self;
};

export {htmlReport};