import {PAGE_MODE} from "../../../common/const";
import {htmlReportTable, htmlReportTableDrawer} from "../../../plugins/dynamicForm/component/htmlReportTable";
import "../../../common/extensions/collectionExtension";

const _graph = {
    createDom: () => {
        return document.createElement("div");
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

const _report_table = htmlReportTable("test-report-table", 0, 0, 100, 100, _page, _drawer);

test("Basic properties", () => {
    expect(_report_table.type).toBe("htmlReportTable");
    expect(_report_table.autoHeight).toBe(true);
    expect(_report_table.minHeight).toBe(60);
    expect(_report_table.hideText).toBe(true);
    expect(_report_table.serializedFields.has("tableData")).toBe(true);
});

describe("drawer test", () => {
    afterEach(() => {
        jest.restoreAllMocks();
        jest.clearAllMocks();
    });

    describe("initialize test", () => {
        afterEach(() => {
            jest.restoreAllMocks();
            jest.clearAllMocks();
        });

        test("normal test", () => {
            // prepare
            const _drawer = htmlReportTableDrawer(_report_table, null, 0, 0);

            // when
            _drawer.initialize();

            // then
            expect(!!_drawer.tableContainer).toBe(true);
            expect(_drawer.tableContainer.id).toBe("table_container_test-report-table");
            expect(_drawer.tableContainer.style.width).toBe("100%");
            expect(_drawer.tableContainer.style.pointerEvents).toBe("auto");
        });
    });

    describe("drawStatic test", () => {
        afterEach(() => {
            jest.restoreAllMocks();
            jest.clearAllMocks();
        });

        test("table data not exists", () => {
            // prepare
            const _drawer = htmlReportTableDrawer(_report_table, null, 0, 0);

            // when
            _drawer.initialize();
            _drawer.drawStatic();

            // then
            expect(_drawer.tableContainer.innerHTML).toBe("");
        });

        test("table data exists, chart title exists, chart data not exists", () => {
            // prepare
            const _drawer = htmlReportTableDrawer(_report_table, null, 0, 0);
            _report_table.tableData = {
                "chartTitle": "口径：经营双算，单位：M$，指标：净销售收入",
            };

            // when
            _drawer.initialize();
            _drawer.drawStatic();

            // then
            expect(_drawer.tableContainer.childNodes.length).toBe(1);
            const title = _drawer.tableContainer.childNodes[0];
            expect(title.style.fontSize).toBe("14px");
            expect(title.style.color).toBe("rgb(113, 117, 127)");
            expect(title.style.fontWeight).toBe("400");
        });

        test("table data exists, chart title exists, chart data exists", () => {
            // prepare
            const _drawer = htmlReportTableDrawer(_report_table, null, 0, 0);
            _report_table.tableData = {
                "chartTitle": "口径：经营双算，单位：M$，指标：净销售收入",
                "chartData": {
                    "columns": ["重点国", "2022年", "2023年", "同比增长(%)"],
                    "rows": [["香港", "42.1", "57.4", "36.3%"], ["德国", "40.5", "46.4", "14.7%"],
                        ["土耳其", "21.3", "43.0", "101.8%"], ["新加坡", "30.0", "40.0", "33.5%"],
                        ["巴西", "26.8", "38.3", "42.5%"], ["南非", "31.8", "35.6", "11.8%"],
                        ["法国", "23.5", "27.7", "17.9%"], ["西班牙", "11.6", "24.4", "109.8%"],
                        ["尼日利亚", "10.1", "24.0", "137.3%"], ["意大利", "8.4", "22.6", "169.5%"],
                        ["沙特", "20.5", "22.3", "8.5%"], ["阿联酋", "14.6", "20.6", "41.3%"],
                        ["墨西哥", "13.5", "19.8", "45.9%"], ["印度尼西亚", "8.2", "16.3", "99.1%"],
                        ["北欧", "6.5", "13.0", "101.2%"], ["瑞士", "12.9", "12.7", "-1.7%"],
                        ["西欧多国管理部", "9.6", "12.4", "29.4%"], ["菲律宾", "3.9", "10.5", "169.7%"],
                        ["马来西亚", "4.6", "7.5", "63.1%"], ["泰国", "5.5", "7.1", "29.2%"],
                        ["韩国", "10.4", "6.8", "-34.3%"], ["日本", "4.7", "3.3", "-28.8%"],
                        ["汇总", "361.0", "511.6", "41.7%"]]
                }
            };

            // when
            _drawer.initialize();
            _drawer.drawStatic();

            // then
            expect(_drawer.tableContainer.childNodes.length).toBe(2);
            const wrapper = _drawer.tableContainer.childNodes[1];
            expect(wrapper.classList.contains("table-wrapper")).toBe(true);
            expect(wrapper.style.marginTop).toBe("10px");

            expect(wrapper.childNodes.length).toBe(1);
            expect(wrapper.childNodes[0].tagName).toBe("TABLE");
        });
    });
});
