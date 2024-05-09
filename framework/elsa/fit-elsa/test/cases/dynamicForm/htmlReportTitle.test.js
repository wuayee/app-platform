import 'core-js/stable';
import {ALIGN, PAGE_MODE} from "../../../common/const";
import {htmlReportTitle} from "../../../plugins/dynamicForm/component/htmlReportTitle";
import "../../../common/extensions/collectionExtension";

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

const _reportTitle = htmlReportTitle("test-report-title", 0, 0, 100, 100, _page, _drawer);

describe("Basic properties", () => {
    afterEach(() => {
        jest.restoreAllMocks();
    });

    test("Basic properties", () => {
        expect(_reportTitle.type).toBe("htmlReportTitle");
        expect(_reportTitle.serializedFields).toContain("data");
        expect(_reportTitle.autoHeight).toBe(true);
        expect(_reportTitle.placeholder).toBe("请输入标题...");
        expect(_reportTitle.hAlign).toBe(ALIGN.MIDDLE);
        expect(_reportTitle.textAlign).toBe(ALIGN.LEFT);
        expect(_reportTitle.fontSize).toBe(16);
        expect(_reportTitle.fontWeight).toBe(600);
        expect(_reportTitle.lineHeight).toBe("22px");
        expect(_reportTitle.fontColor).toBe("rgb(37, 43, 58)");
    });
});

describe("getData test", () => {
    afterEach(() => {
        jest.restoreAllMocks();
        jest.clearAllMocks();
    });

    test("report title has no text", () => {
        // prepare
        const originTitleText = _reportTitle.text;
        _reportTitle.text = undefined;

        // when
        const data = _reportTitle.getData();

        // then
        expect(data).toStrictEqual({"query": ""});

        _reportTitle.text = originTitleText;
    });

    test("report title has text", () => {
        // prepare
        jest.spyOn(_reportTitle, "getShapeText").mockImplementationOnce(() => "shapeText");

        // when
        const data = _reportTitle.getData();

        // then
        expect(data).toStrictEqual({"query": "shapeText"});
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
        _reportTitle.formDataRetrieved();

        // then
        expect(_reportTitle.data).toBeUndefined();
    });

    test("input with data", () => {
        // prepare
        // when
        _reportTitle.formDataRetrieved(null, {"query": "shapeText"});

        // then
        expect(_reportTitle.data).toBeDefined();
        expect(_reportTitle.text).toStrictEqual("shapeText");
    });
});