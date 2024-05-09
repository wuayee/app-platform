import 'core-js/stable';
import {ALIGN, PAGE_MODE} from "../../../common/const";
import "../../../common/extensions/collectionExtension";
import {htmlReportSummary} from "../../../plugins/dynamicForm/component/htmlReportSummary";

const _graph = {
    createDom: () => {
        return {
            style: {}
        };
    },
    addEventListener: () => {
    },
    setting: {
        borderColor: "red"
    }
};

const _page = {
    id: "pageId", shapes: [], disableReact: true, mode: PAGE_MODE.CONFIGURATION, addEventListener: () => {
    }
};

_page.page = _page;
_page.graph = _graph;

const _drawer = jest.fn(() => {
    return {}
});

const _reportSummary = htmlReportSummary("test-report-summary", 0, 0, 100, 100, _page, _drawer);

describe("Basic properties", () => {
    afterEach(() => {
        jest.restoreAllMocks();
    });

    test("Basic properties", () => {
        expect(_reportSummary.type).toBe("htmlReportSummary");
        expect(_reportSummary.serializedFields).toContain("data");
        expect(_reportSummary.autoHeight).toBe(true);
        expect(_reportSummary.placeholder).toBe("请输入标题...");
        expect(_reportSummary.hAlign).toBe(ALIGN.MIDDLE);
        expect(_reportSummary.textAlign).toBe(ALIGN.LEFT);
        expect(_reportSummary.fontSize).toBe(14);
        expect(_reportSummary.fontWeight).toBe(400);
        expect(_reportSummary.lineHeight).toBe("20px");
        expect(_reportSummary.fontColor).toBe("rgb(113, 117, 127)");
    });
});

describe("getData test", () => {
    afterEach(() => {
        jest.restoreAllMocks();
        jest.clearAllMocks();
    });

    test("report summary has no text", () => {
        // prepare
        const originTitleText = _reportSummary.text;
        _reportSummary.text = undefined;

        // when
        const data = _reportSummary.getData();

        // then
        expect(data).toStrictEqual("");

        _reportSummary.text = originTitleText;
    });

    test("report summary has text", () => {
        // prepare
        jest.spyOn(_reportSummary, "getShapeText").mockImplementationOnce(() => "shapeText");

        // when
        const data = _reportSummary.getData();

        // then
        expect(data).toStrictEqual("shapeText");
    });
});

describe("formDataRetrieved test", () => {
    afterEach(() => {
        jest.restoreAllMocks();
        jest.clearAllMocks();
    });

    test("input without params", () => {
        // prepare
        // when
        _reportSummary.formDataRetrieved();

        // then
        expect(_reportSummary.text).toStrictEqual("some text");
    });

    test("input with data", () => {
        // prepare
        // when
        _reportSummary.formDataRetrieved(null, "shapeText");

        // then
        expect(_reportSummary.text).toStrictEqual("shapeText");
    });
});