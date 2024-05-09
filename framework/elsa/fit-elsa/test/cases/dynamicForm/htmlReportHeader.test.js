import {ALIGN, PAGE_MODE} from "../../../common/const";
import "../../../common/extensions/collectionExtension";
import {htmlReportHeader} from "../../../plugins/dynamicForm/component/htmlReportHeader";

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

const _reportHeader = htmlReportHeader("test-report-header", 0, 0, 100, 100, _page, _drawer);

describe("Basic properties", () => {
    afterEach(() => {
        jest.restoreAllMocks();
    });

    test("Basic properties", () => {
        expect(_reportHeader.type).toBe("htmlReportHeader");
        expect(_reportHeader.autoHeight).toBe(true);
        expect(_reportHeader.placeholder).toBe("请输入标题...");
        expect(_reportHeader.hAlign).toBe(ALIGN.MIDDLE);
        expect(_reportHeader.textAlign).toBe(ALIGN.MIDDLE);
        expect(_reportHeader.fontSize).toBe(28);
        expect(_reportHeader.fontWeight).toBe(600);
        expect(_reportHeader.lineHeight).toBe("30px");
        expect(_reportHeader.fontColor).toBe("rgb(37, 43, 58)");
    });
});