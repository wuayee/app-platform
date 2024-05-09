import {PAGE_MODE} from "../../../common/const";
import "../../../common/extensions/collectionExtension";
import {htmlReportCharts} from "../../../plugins/dynamicForm/component/htmlReportCharts";
import {eChartDrawer} from "../../../plugins/chart/echart";
import {graph} from "../../../core/graph";
import 'core-js/stable';

const barData = [
    {
        "data": [
            "42.1",
            "40.5",
            "21.3",
            "30.0",
            "26.8",
            "31.8",
            "23.5",
            "11.6",
            "10.1",
            "8.4",
            "20.5",
            "14.6",
            "13.5",
            "8.2",
            "6.5",
            "12.9",
            "9.6",
            "3.9",
            "4.6",
            "5.5",
            "10.4",
            "4.7",
            "361.0"
        ],
        "title": "2022年"
    },
    {
        "data": [
            "57.4",
            "46.4",
            "43.0",
            "40.0",
            "38.3",
            "35.6",
            "27.7",
            "24.4",
            "24.0",
            "22.6",
            "22.3",
            "20.6",
            "19.8",
            "16.3",
            "13.0",
            "12.7",
            "12.4",
            "10.5",
            "7.5",
            "7.1",
            "6.8",
            "3.3",
            "511.6"
        ],
        "title": "2023年"
    }
];
const lineData = [
    {
        "data": [
            "36.3%",
            "14.7%",
            "101.8%",
            "33.5%",
            "42.5%",
            "11.8%",
            "17.9%",
            "109.8%",
            "137.3%",
            "169.5%",
            "8.5%",
            "41.3%",
            "45.9%",
            "99.1%",
            "101.2%",
            "-1.7%",
            "29.4%",
            "169.7%",
            "63.1%",
            "29.2%",
            "-34.3%",
            "-28.8%",
            "41.7%"
        ],
        "title": "同比增长(%)"
    }
];
const labelData = [
    "香港",
    "德国",
    "土耳其",
    "新加坡",
    "巴西",
    "南非",
    "法国",
    "西班牙",
    "尼日利亚",
    "意大利",
    "沙特",
    "阿联酋",
    "墨西哥",
    "印度尼西亚",
    "北欧",
    "瑞士",
    "西欧多国管理部",
    "菲律宾",
    "马来西亚",
    "泰国",
    "韩国",
    "日本",
    "汇总"
];

const mixLineBar = {
    "chartType": "MIX_LINE_BAR",
    "chartTitle": "口径：经营双算，单位：M$，指标：净销售收入",
    "chartData": {
        "bar": barData,
        "line": lineData,
        "labels": labelData
    }
};

const line = {
    "chartType": "LINE",
    "chartTitle": "口径：经营双算，单位：M$，指标：净销售收入",
    "chartData": {
        "line": lineData,
        "labels": labelData
    }
};

const bar = {
    "chartType": "BAR",
    "chartTitle": "口径：经营双算，单位：M$，指标：净销售收入",
    "chartData": {
        "bar": barData,
        "labels": labelData
    }
};

const _page = {
    id: "pageId", shapes: [], disableReact: true, mode: PAGE_MODE.CONFIGURATION
};

const shape = {type: "htmlReportCharts"};

_page.page = _page;
const _dom = {
    style: {}
}
_page.graph = graph(_dom, "");

const _drawer = jest.fn(() => {
    return eChartDrawer(shape, _page.div);
});

const reportCharts = htmlReportCharts("test-report-chart", 0, 0, 100, 100, _page, _drawer);

const chartMap = new Map();
chartMap.set("LINE", ["line"]);
chartMap.set("BAR", ["bar"]);
chartMap.set("MIX_LINE_BAR", ["bar", "line"]);
chartMap.set("PIE", ["pie"]);

describe("Basic properties", () => {
    afterEach(() => {
        jest.restoreAllMocks();
    });

    test("Basic properties", () => {
        expect(reportCharts.type).toBe("htmlReportCharts");
        expect(reportCharts.serializedFields).toContain("option", "chartType");
        expect(reportCharts.dashWidth).toBe(0);
        expect(reportCharts.chartMap).toEqual(chartMap);
    });
});

describe("getData test", () => {
    afterEach(() => {
        jest.restoreAllMocks();
        jest.clearAllMocks();
    });

    test("chart type is MIX_LINE_BAR", () => {
        reportCharts.formDataRetrieved(undefined, mixLineBar);
        const data = mixLineBar;
        data.chartData.bar.forEach(item => {
            item.data = item.data.map(num => parseFloat(num));
        });
        data.chartData.line.forEach(item => {
            item.data = item.data.map(num => parseFloat(num));
        });
        expect(reportCharts.getData()).toEqual(data);
    });

    test("chart type is BAR", () => {
        reportCharts.formDataRetrieved(undefined, bar);
        const data = bar;
        // 将原始数据字符串转为数值+去掉原始数据的%
        data.chartData.bar.forEach(item => {
            item.data = item.data.map(num => parseFloat(num));
        });
        expect(reportCharts.getData()).toEqual(data);
    });

    test("chart type is LINE", () => {
        reportCharts.formDataRetrieved(undefined, line);
        const data = line;
        // 将原始数据字符串转为数值+去掉原始数据的%
        data.chartData.line.forEach(item => {
            item.data = item.data.map(num => parseFloat(num));
        });
        expect(reportCharts.getData()).toEqual(data);
    });
});

describe("formDataRetrieved test", () => {
    afterEach(() => {
        jest.restoreAllMocks();
        jest.clearAllMocks();
    });


    test("chart type is MIX_LINE_BAR", () => {
        reportCharts.formDataRetrieved(undefined, mixLineBar);
        expect(reportCharts.option).toEqual({
            "color": ["#5E7CE0", "#6CBFFF", "#50D4AB", "#A6DD82", "#FAC20A"],
            "title": {
                "text": "口径：经营双算，单位：M$，指标：净销售收入",
                "textAlign": "justify",
                "textStyle": {
                    "overflow": "truncate",
                    "width": 700,
                    "fontWeight": 400,
                    "fontFamily": "PingFangSC-Regular, sans-serif",
                    "fontSize": 14,
                    "lineHeight": 20
                }
            },
            "grid": {"top": "10%", "right": "15%", "left": "5%"},
            "tooltip": {"trigger": "axis", "axisPointer": {"type": "shadow"}},
            "dataZoom": {"type": "slider", "start": 0, "end": 30},
            "toolbox": {
                "show": true,
                "itemSize": 15,
                "feature": {
                    "magicType": {
                        "show": true,
                        "type": ["bar", "line"],
                        "title": {"line": "折线图", "bar": "柱状图"}
                    }, "restore": {"title": "还原"}, "saveAsImage": {"title": "下载图片"}
                }
            },
            "legend": {"show": true, "type": "scroll", "orient": "vertical", "right": "right", "top": "15%"},
            "xAxis": {
                "type": "category",
                "axisTick": {"alignWithLabel": true},
                "axisLabel": {"show": true},
                "data": ["香港", "德国", "土耳其", "新加坡", "巴西", "南非", "法国", "西班牙", "尼日利亚", "意大利", "沙特", "阿联酋", "墨西哥", "印度尼西亚", "北欧", "瑞士", "西欧多国管理部", "菲律宾", "马来西亚", "泰国", "韩国", "日本", "汇总"]
            },
            "yAxis": [{"type": "value", "position": "left", "show": false}, {
                "type": "value",
                "position": "right",
                "show": false,
                "axisLabel": {"formatter": "{value}%"}
            }],
            "series": [{
                "type": "bar",
                "data": [42.1, 40.5, 21.3, 30, 26.8, 31.8, 23.5, 11.6, 10.1, 8.4, 20.5, 14.6, 13.5, 8.2, 6.5, 12.9, 9.6, 3.9, 4.6, 5.5, 10.4, 4.7, 361],
                "name": "2022年",
                "barMaxWidth": 30
            }, {
                "type": "bar",
                "data": [57.4, 46.4, 43, 40, 38.3, 35.6, 27.7, 24.4, 24, 22.6, 22.3, 20.6, 19.8, 16.3, 13, 12.7, 12.4, 10.5, 7.5, 7.1, 6.8, 3.3, 511.6],
                "name": "2023年",
                "barMaxWidth": 30
            }, {
                "type": "line",
                "smooth": true,
                "yAxisIndex": 1,
                "data": [36.3, 14.7, 101.8, 33.5, 42.5, 11.8, 17.9, 109.8, 137.3, 169.5, 8.5, 41.3, 45.9, 99.1, 101.2, -1.7, 29.4, 169.7, 63.1, 29.2, -34.3, -28.8, 41.7],
                "name": "同比增长(%)"
            }]
        });
    });

    test("chart type is BAR", () => {
        reportCharts.formDataRetrieved(undefined, bar);
        expect(reportCharts.option).toEqual({
            "color": ["#5E7CE0", "#6CBFFF", "#50D4AB", "#A6DD82", "#FAC20A"],
            "title": {
                "text": "口径：经营双算，单位：M$，指标：净销售收入",
                "textAlign": "justify",
                "textStyle": {
                    "overflow": "truncate",
                    "width": 700,
                    "fontWeight": 400,
                    "fontFamily": "PingFangSC-Regular, sans-serif",
                    "fontSize": 14,
                    "lineHeight": 20
                }
            },
            "grid": {"top": "10%", "right": "15%", "left": "5%"},
            "tooltip": {"trigger": "axis", "axisPointer": {"type": "shadow"}},
            "dataZoom": {"type": "slider", "start": 0, "end": 30},
            "toolbox": {
                "show": true,
                "itemSize": 15,
                "feature": {
                    "magicType": {
                        "show": true,
                        "type": ["bar", "line"],
                        "title": {"line": "折线图", "bar": "柱状图"}
                    }, "restore": {"title": "还原"}, "saveAsImage": {"title": "下载图片"}
                }
            },
            "legend": {"show": true, "type": "scroll", "orient": "vertical", "right": "right", "top": "15%"},
            "xAxis": {
                "type": "category",
                "axisTick": {"alignWithLabel": true},
                "axisLabel": {"show": true},
                "data": ["香港", "德国", "土耳其", "新加坡", "巴西", "南非", "法国", "西班牙", "尼日利亚", "意大利", "沙特", "阿联酋", "墨西哥", "印度尼西亚", "北欧", "瑞士", "西欧多国管理部", "菲律宾", "马来西亚", "泰国", "韩国", "日本", "汇总"]
            },
            "yAxis": [{"type": "value", "show": false}],
            "series": [{
                "type": "bar",
                "data": [42.1, 40.5, 21.3, 30, 26.8, 31.8, 23.5, 11.6, 10.1, 8.4, 20.5, 14.6, 13.5, 8.2, 6.5, 12.9, 9.6, 3.9, 4.6, 5.5, 10.4, 4.7, 361],
                "name": "2022年",
                "barMaxWidth": 30
            }, {
                "type": "bar",
                "data": [57.4, 46.4, 43, 40, 38.3, 35.6, 27.7, 24.4, 24, 22.6, 22.3, 20.6, 19.8, 16.3, 13, 12.7, 12.4, 10.5, 7.5, 7.1, 6.8, 3.3, 511.6],
                "name": "2023年",
                "barMaxWidth": 30
            }]
        });
    });

    test("chart type is LINE", () => {
        reportCharts.formDataRetrieved(undefined, line);
        expect(reportCharts.option).toEqual({
            "color": ["#5E7CE0", "#6CBFFF", "#50D4AB", "#A6DD82", "#FAC20A"],
            "title": {
                "text": "口径：经营双算，单位：M$，指标：净销售收入",
                "textAlign": "justify",
                "textStyle": {
                    "overflow": "truncate",
                    "width": 700,
                    "fontWeight": 400,
                    "fontFamily": "PingFangSC-Regular, sans-serif",
                    "fontSize": 14,
                    "lineHeight": 20
                }
            },
            "grid": {"top": "10%", "right": "15%", "left": "5%"},
            "tooltip": {"trigger": "axis", "axisPointer": {"type": "shadow"}},
            "dataZoom": {"type": "slider", "start": 0, "end": 30},
            "toolbox": {
                "show": true,
                "itemSize": 15,
                "feature": {
                    "magicType": {
                        "show": true,
                        "type": ["bar", "line"],
                        "title": {"line": "折线图", "bar": "柱状图"}
                    }, "restore": {"title": "还原"}, "saveAsImage": {"title": "下载图片"}
                }
            },
            "legend": {"show": true, "type": "scroll", "orient": "vertical", "right": "right", "top": "15%"},
            "xAxis": {
                "type": "category",
                "axisTick": {"alignWithLabel": true},
                "axisLabel": {"show": true},
                "data": ["香港", "德国", "土耳其", "新加坡", "巴西", "南非", "法国", "西班牙", "尼日利亚", "意大利", "沙特", "阿联酋", "墨西哥", "印度尼西亚", "北欧", "瑞士", "西欧多国管理部", "菲律宾", "马来西亚", "泰国", "韩国", "日本", "汇总"]
            },
            "yAxis": [{"type": "value", "show": false}],
            "series": [{
                "type": "line",
                "smooth": true,
                "data": [36.3, 14.7, 101.8, 33.5, 42.5, 11.8, 17.9, 109.8, 137.3, 169.5, 8.5, 41.3, 45.9, 99.1, 101.2, -1.7, 29.4, 169.7, 63.1, 29.2, -34.3, -28.8, 41.7],
                "name": "同比增长(%)"
            }]
        });
    });
});

