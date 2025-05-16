/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import {ALIGN, DOCK_MODE, PAGE_MODE} from "../../../common/const";
import {htmlReport} from "../../../plugins/dynamicForm/component/htmlReport";
import "../../../common/extensions/collectionExtension";

const _graph = {
    createDom: () => {
        return {
            style: {}
        };
    }, setting: {
        borderColor: "red"
    }
};

const _page = {
    id: "pageId", shapes: [], disableReact: true, mode: PAGE_MODE.CONFIGURATION
};

_page.page = _page;
_page.graph = _graph;

const _drawer = jest.fn(() => {
    return {}
});

const _report = htmlReport("test-report", 0, 0, 100, 100, _page, _drawer);

describe("Basic properties", () => {
    afterEach(() => {
        jest.restoreAllMocks();
    });

    test("Basic properties", () => {
        expect(_report.type).toBe("htmlReport");
        expect(_report.dockAlign).toBe(ALIGN.TOP);
        expect(_report.dockMode).toBe(DOCK_MODE.VERTICAL);
        expect(_report.autoFit).toBe(true);
    });
});

describe("getData test", () => {
    afterEach(() => {
        jest.restoreAllMocks();
        jest.clearAllMocks();
    });

    test("report has no shapes", () => {
        // prepare
        jest.spyOn(_report, "getShapes").mockImplementationOnce(() => []);

        // when
        const data = _report.getData();

        // then
        expect(data).toStrictEqual({});
    });

    test("report only has header", () => {
        // prepare
        const _child = {
            isTypeof: (type) => {
                return type === "htmlReportHeader";
            }, getData: () => {
                return "经营分析报告";
            }
        };
        jest.spyOn(_report, "getShapes").mockImplementationOnce(() => [_child]);

        // when
        const data = _report.getData();

        // then
        const equal = {};
        equal[_report.componentId] = [];
        expect(data).toStrictEqual(equal);
    });

    test("report only has table or echarts, throw error", () => {
        // prepare
        const _child = {
            isTypeof: (type) => {
                return type === "htmlReportTable" || type === "htmlReportCharts";
            }
        };
        jest.spyOn(_report, "getShapes").mockImplementationOnce(() => [_child]);

        // when
        // then
        expect(() => _report.getData()).toThrow("report structure is invalid.");
    });

    test("report only has table/echarts and question, order is not right, throw error", () => {
        // prepare
        const _question = {
            isTypeof: (type) => {
                return type === "htmlReportTitle";
            },
            getData: () => {
                return {
                    "createUser": "00498867",
                    "appId": "218",
                    "conversationId": 0,
                    "query": "2023年贵州代表处整体收入情况"
                };
            }
        };
        const _table = {
            isTypeof: (type) => {
                return type === "htmlReportTable" || type === "htmlReportCharts";
            }
        };
        jest.spyOn(_report, "getShapes").mockImplementationOnce(() => [_table, _question]);

        // when
        // then
        expect(() => _report.getData()).toThrow("report structure is invalid.");
    });

    test("report only has table/echarts and question, order is right", () => {
        // prepare
        const _question = {
            isTypeof: (type) => {
                return type === "htmlReportTitle";
            },
            getData: () => {
                return {
                    "createUser": "00498867",
                    "appId": "218",
                    "conversationId": 0,
                    "query": "2023年贵州代表处整体收入情况"
                };
            }
        };
        const _table = {
            isTypeof: (type) => {
                return type === "htmlReportTable" || type === "htmlReportCharts";
            },
            getData: () => {
                return {
                    "chartType": "TABLE",
                    "chartTitle": "口径：经营双算，单位：M$，指标：净销售收入",
                    "chartData": {
                        "columns": [
                            "重点国",
                            "2022年",
                            "2023年",
                            "同比增长(%)"
                        ],
                        "rows": [
                            [
                                "香港",
                                "42.1",
                                "57.4",
                                "36.3%"
                            ]
                        ]
                    },
                    "chartAnswer": "2023年存储企业海外分重点国的收入和同比，结果如下：",
                    "answer": "2023年存储企业海外分重点国的收入和同比，结果如下：----- ",
                    "type": "ELSA"
                }
            }
        };
        jest.spyOn(_report, "getShapes").mockImplementationOnce(() => [_question, _table]);

        // when
        const result = _report.getData();

        // then
        const equal = {};
        equal[_report.componentId] = [
            {
                "question": {
                    "createUser": "00498867",
                    "appId": "218",
                    "conversationId": 0,
                    "query": "2023年贵州代表处整体收入情况"
                },
                "answer": {
                    "answer": "2023年存储企业海外分重点国的收入和同比，结果如下：----- ",
                    "chartType": [
                        "TABLE"
                    ],
                    "chartData": [
                        {
                            "columns": [
                                "重点国",
                                "2022年",
                                "2023年",
                                "同比增长(%)"
                            ],
                            "rows": [
                                [
                                    "香港",
                                    "42.1",
                                    "57.4",
                                    "36.3%"
                                ]
                            ]
                        },
                    ],
                    "chartTitle": [
                        "口径：经营双算，单位：M$，指标：净销售收入",
                    ],
                    "chartAnswer": [
                        "2023年存储企业海外分重点国的收入和同比，结果如下：",
                    ],
                    "chartSummary": [],
                    "type": "ELSA"
                },
            }
        ];
        expect(result).toStrictEqual(equal);
    });
});

describe("formDataRetrieved test", () => {
    afterEach(() => {
        jest.restoreAllMocks();
        jest.clearAllMocks();
    });

    test("report data is not a string.", () => {
        // prepare
        // when
        // then
        const data = {};
        data[_report.componentId] = {};
        expect(() => _report.formDataRetrieved(null, data)).toThrow(Error);
    });

    test("report data is not an array after json parse.", () => {
        // prepare
        // when
        // then
        const data = {};
        data[_report.componentId] = "{}";
        expect(() => _report.formDataRetrieved(null, data)).toThrow("reportData must be an array.");
    });

    test("report data array length is 0.", () => {
        // prepare
        _report.page.ignoreReact = jest.fn((func) => {
            return func();
        });

        // when
        const data = {};
        data[_report.componentId] = "[]";
        _report.formDataRetrieved(null, data);

        // then
        expect(_report.page.ignoreReact).toHaveBeenCalledTimes(0);
    });

    test("report data array is valid.", () => {
        // prepare
        _report.page.ignoreReact = jest.fn((func) => {
            return func();
        });

        _report.page.createShape = jest.fn(() => {
            return {
                formDataRetrieved: () => {}
            };
        });

        // when
        const data = {};
        data[_report.componentId] = JSON.stringify([{question: "胜多负少的福建省?", answer: {
                "chartType": ["TABLE"],
                "chartTitle": ["口径：经营双算，单位：M$，指标：净销售收入"],
                "chartData": [{
                    "columns": [
                        "重点国",
                        "2022年",
                        "2023年",
                        "同比增长(%)"
                    ],
                    "rows": [
                        [
                            "香港",
                            "42.1",
                            "57.4",
                            "36.3%"
                        ]
                    ]
                }],
                "chartAnswer": ["2023年存储企业海外分重点国的收入和同比，结果如下："],
                "answer": ["2023年存储企业海外分重点国的收入和同比，结果如下：----- "],
                "chartSummary": ["2023年存储企业国内分代表处的收入和同比，总收入为14,803.3万元，同比增长-4.2%"],
                "type": "ELSA"
            }}]);
        _report.formDataRetrieved(null, data);

        // then
        expect(_report.page.createShape).toHaveBeenCalledTimes(3);
    });

    test("chart type not support.", () => {
        // prepare
        _report.page.ignoreReact = jest.fn((func) => {
            return func();
        });

        _report.page.createShape = jest.fn(() => {
            return {
                formDataRetrieved: () => {}
            };
        });

        // when
        // then
        const data = {};
        data[_report.componentId] = JSON.stringify([{question: "胜多负少的福建省?", answer: {
                "chartType": ["GRAPH"],
                "chartTitle": ["口径：经营双算，单位：M$，指标：净销售收入"],
                "chartData": [{
                    "columns": [
                        "重点国",
                        "2022年",
                        "2023年",
                        "同比增长(%)"
                    ],
                    "rows": [
                        [
                            "香港",
                            "42.1",
                            "57.4",
                            "36.3%"
                        ]
                    ]
                }],
                "chartAnswer": ["2023年存储企业海外分重点国的收入和同比，结果如下："],
                "answer": ["2023年存储企业海外分重点国的收入和同比，结果如下：----- "],
                "chartSummary": ["2023年存储企业国内分代表处的收入和同比，总收入为14,803.3万元，同比增长-4.2%"],
                "type": "ELSA"
            }}]);
        expect(() => _report.formDataRetrieved(null, data)).toThrow("type[GRAPH] is not supported.");
    });
});

