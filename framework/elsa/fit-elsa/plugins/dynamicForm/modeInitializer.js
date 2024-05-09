import {MODE_MANAGER} from "../../common/mode/modeManager.js";
import {EVENT_TYPE, PAGE_MODE} from "../../common/const.js";

const dynamicFormConfiguration = () => {
    MODE_MANAGER.registerForbiddenMethods(PAGE_MODE.CONFIGURATION, "htmlText", "beginEdit");
    MODE_MANAGER.registerForbiddenMethods(PAGE_MODE.CONFIGURATION, "htmlButton", "click");
    MODE_MANAGER.registerForbiddenMethods(PAGE_MODE.CONFIGURATION, "htmlTextDisplay", "enableTextPointerEvents");
    MODE_MANAGER.registerOverrideMethodBatch(PAGE_MODE.CONFIGURATION,
        [
            "htmlFileInput",
            "htmlImageInput",
            "htmlRadioBox"
        ],
        "getEnableInteract",
        () => false);
};

const dynamicFormDisplay = () => {
    MODE_MANAGER.registerForbiddenMethods(PAGE_MODE.DISPLAY, "htmlText", "beginEdit");
    MODE_MANAGER.registerForbiddenMethods(PAGE_MODE.DISPLAY, "htmlLabel", "beginEdit");
};

const dynamicFormPresentation = () => {
    MODE_MANAGER.registerForbiddenMethods(PAGE_MODE.PRESENTATION, "htmlText", "beginEdit");
    MODE_MANAGER.registerForbiddenMethods(PAGE_MODE.PRESENTATION, "htmlLabel", "beginEdit");
};

const dynamicFormView = () => {
    MODE_MANAGER.registerForbiddenMethods(PAGE_MODE.VIEW, "htmlText", "beginEdit");
    MODE_MANAGER.registerForbiddenMethods(PAGE_MODE.VIEW, "htmlLabel", "beginEdit");
};

const dynamicFormRuntime = () => {
    MODE_MANAGER.registerForbiddenMethods(PAGE_MODE.RUNTIME, "htmlLabel", "beginEdit");
    MODE_MANAGER.registerForbiddenMethods(PAGE_MODE.RUNTIME, "htmlReportHeader", "beginEdit");

    MODE_MANAGER.registerOverrideMethod(PAGE_MODE.RUNTIME, "htmlButton", "getEnableInteract", () => false);
    MODE_MANAGER.registerOverrideMethodBatch(PAGE_MODE.RUNTIME,
        [
            "formComponent",
            "htmlTaskDisplay",
            "htmlFileInput",
            "htmlTextLink"
        ],
        "getEnableInteract",
        () => true);
    MODE_MANAGER.registerOverrideMethodBatch(PAGE_MODE.RUNTIME,
        [
            "formContainerComponent",
            "htmlTextDisplay",
            "htmlTextLink",
            "htmlReportTable",
            "htmlReportTitle",
            "htmlReportHeader",
            "htmlReportSummary"
        ],
        "getBorderWidth",
        () => 0);
    MODE_MANAGER.registerOverrideMethodBatch(PAGE_MODE.RUNTIME,
        [
            "aippState",
            "aippStart",
            "aippEnd"
        ],
        "click",
        remedyClickOnRuntime);
    MODE_MANAGER.registerOverrideMethodBatch(PAGE_MODE.RUNTIME,
        [
            "formComponent",
            "formContainerComponent"
        ],
        "enableRegion",
        () => false);
};

const dynamicFormHistory = () => {
    MODE_MANAGER.registerForbiddenMethods(PAGE_MODE.HISTORY, "htmlLabel", "beginEdit");
    MODE_MANAGER.registerForbiddenMethods(PAGE_MODE.HISTORY, "htmlText", "beginEdit");
    MODE_MANAGER.registerForbiddenMethods(PAGE_MODE.HISTORY, "htmlReportHeader", "beginEdit");
    MODE_MANAGER.registerForbiddenMethods(PAGE_MODE.HISTORY, "htmlReport", "createEditRegion");

    MODE_MANAGER.registerOverrideMethod(PAGE_MODE.HISTORY, "htmlButton", "getVisibility", () => false);
    MODE_MANAGER.registerOverrideMethod(PAGE_MODE.HISTORY, "htmlText", "formLoaded", () => {
        // todo@zhangyue 去掉这行代码，htmlText变为只读，但是无法选中其中的文字.
        // return proxy.drawer.enableTextPointerEvents();
    });
    MODE_MANAGER.registerOverrideMethod(PAGE_MODE.HISTORY, "htmlTextLink", "getEnableInteract", () => true);
    MODE_MANAGER.registerOverrideMethodBatch(PAGE_MODE.HISTORY,
        [
            "formComponent",
            "htmlFileInput",
            "htmlImageInput",
            "htmlRadioBox",
            "htmlButton"
        ],
        "getEnableInteract",
        () => false);
    MODE_MANAGER.registerOverrideMethodBatch(PAGE_MODE.HISTORY,
        [
            "formContainerComponent",
            "htmlTextDisplay",
            "htmlTextLink",
            "htmlReportTable",
            "htmlReportTitle",
            "htmlReportHeader",
            "htmlReportSummary"
        ],
        "getBorderWidth",
        () => 0);
    MODE_MANAGER.registerOverrideMethodBatch(PAGE_MODE.HISTORY,
        [
            "formComponent",
            "formContainerComponent"
        ],
        "enableRegion",
        () => false);
};

// TODO: 需要更通用化的解决方案
const remedyClickOnRuntime = (() => {
    // 闭包变量，所有回调函数共有。保证页面空白的FOCUSED_SHAPES_CHANGE事件只被触发一次。
    let shouldFireEmptyEvent = false;
    return (proxy) => {
        proxy.page.triggerEvent({
            type: EVENT_TYPE.FOCUSED_SHAPES_CHANGE,
            value: [proxy]
        });
        shouldFireEmptyEvent = true;
        /*
         * 当用户点击节点又点击空白页面时，一次性重写原有方法并手动触发FOCUSED_SHAPES_CHANGE事件。触发完毕后，恢复原有事件处理器。
         */
        const click = proxy.page.click;
        proxy.page.click = (x, y) => {
            click.apply(proxy.page, [x, y]);
            shouldFireEmptyEvent && proxy.page.triggerEvent({
                type: EVENT_TYPE.FOCUSED_SHAPES_CHANGE,
                value: []
            });
            shouldFireEmptyEvent = false;
            proxy.page.click = click;
        };
    };
})();

/**
 * 初始化动态表单的mode管理.
 */
(() => {
    dynamicFormConfiguration();
    dynamicFormDisplay();
    dynamicFormPresentation();
    dynamicFormView();
    dynamicFormRuntime();
    dynamicFormHistory();
})();