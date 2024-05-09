import {formGraph} from "./formGraph.js";
import {EVENT_TYPE} from "../../common/const.js";
import {formDeleteCommand} from "./commands.js";
import {weHttpUtil} from "./http/weHttpUtil.js";

const AVAILABLE_SHAPE_TYPES = ["htmlInput", "htmlButton", "htmlFileInput",
    "llmSelector", "htmlImageDisplay", "htmlImageInput",
    "startButton", "confirmButton", "htmlMind",
    "htmlRadioBox", "htmlIframe", "htmlFileDownload",
    "htmlPdfInput","htmlDocInput", "htmlTextDisplay",
    "sessionStartButton", "sessionContinueButton", "htmlVideoInput",
    "htmlPptDisplay", "htmlVideoSummary", "htmlAudioInput",
    "htmlTaskDisplay", "htmlTextLink", "htmlReportTable",
    "htmlReportTitle", "htmlReportHeader", "htmlReportSummary",
    "htmlReport", "htmlReportCharts", "reportSaveButton"];

/**
 * 表单代码，对外暴露接口，以便对表单进行操作以及获取数据.
 *
 * @returns {{}}
 */
const formAgent = (graph, page) => {
    const self = {};
    self.graph = graph;
    self.page = page;

    /**
     * 添加图形.
     *
     * @param shapeType 图形类型.
     * @param properties 初始化属性.
     */
    self.want = (shapeType, properties) => {
        if (!AVAILABLE_SHAPE_TYPES.includes(shapeType)) {
            throw new Error("不支持此shapeType");
        }
        page.want(shapeType, properties);
    };

    /**
     * 序列化数据.
     *
     * @returns {*} graph的全量序列化数据.
     */
    self.serialize = () => {
        // 序列化之前，先取消选中的图形，保证已修改的文本同步到textInnerHtml中.
        page.getFocusedShapes().forEach(s => s.unSelect());
        return graph.serialize();
    };

    /**
     * 加载数据.
     *
     * @param graphData 画布数据.
     * @param div 需要渲染的目标div.optional
     * @param mode 模式,runtime/history.
     * @returns {any}
     */
    self.loadFormFromGraph = (graphData, div, mode) => {
        const next = createNext();
        new Promise(resolve => resolve()).then(() => {
            const getPage = () => {
                if (div) {
                    const page = graph.addPage("form-page", undefined, div, null, null, mode);
                    graph.environment !== "pc" && page.setHttpUtil(weHttpUtil());
                    return page;
                }
                self.page.clear();
                return self.page;
            }
            const page = getPage();
            page.loadForm(graphData);
            page.run(next.data, next.submittedCallback, next.onSubmitCallback);
            next.then && next.then(formAgent(graph, page));
        });
        return next;
    };

    /**
     * 设置http工具对象.
     *
     * @param httpUtil http工具对象.
     */
    self.setHttpUtil = (httpUtil) => {
        page.setHttpUtil(httpUtil);
    };

    self.listenersMapping = new Map();

    /**
     * 添加focusedShapes的监听器.
     *
     * @param handler 事件处理器.
     */
    self.addFocusedShapeChangeListener = (handler) => {
        // 当未选中图形时，返回form对象.
        const _handler = (shapes) => {
            handler(shapes.length === 0 ? [page.getForm()] : shapes);
        };
        self.listenersMapping.set(handler, _handler);
        graph.addEventListener(EVENT_TYPE.FOCUSED_SHAPES_CHANGE, _handler);
    };

    /**
     * 取消focusedShapes的监听器.
     *
     * @param handler 监听处理器.
     */
    self.removeFocusedShapeChangeListener = (handler) => {
        const _handler = self.listenersMapping.get(handler);
        if (_handler) {
            graph.removeEventListener(EVENT_TYPE.FOCUSED_SHAPES_CHANGE, _handler);
        }
    };

    /**
     * 获取graph的信息.
     *
     * @returns {{id, version}} 画布信息.
     */
    self.getGraphInfo = () => {
        return {
            id: graph.id, version: graph.version
        };
    };

    /**
     * 清楚所有图形.
     */
    self.clear = () => {
        const form = page.getForm();
        const cmd = formDeleteCommand(page, form.getShapes().map(s => {
            return {shape: s};
        }));
        cmd.execute(page);
    };

    /**
     * 设置表单名称.
     *
     * @param name 名称.
     */
    self.setFormName = (name) => {
        const form = page.getForm();
        if (form) {
            form.name = name;
        }
    };

    /**
     * 获取form对象.
     *
     * @returns {*} form对象.
     */
    self.getForm = () => {
        return page.getForm();
    };

    /**
     * 设置错误异常处理器
     *
     * @param errorHandler 错误异常处理器
     */
    self.onError = (errorHandler) => {
        graph.addEventListener(EVENT_TYPE.ERROR_OCCURRED, errorHandler);
    };

    return self;
};

const createNext = () => {
    const next = {
        then: (then) => next.then = then, initializeData: (data) => {
            next.data = data;
            return {
                then: next.then, submitted: next.submitted, onSubmit: next.onSubmit
            }
        }, submitted: (callback) => {
            next.submittedCallback = callback;
            return {
                initializeData: next.initializeData, then: next.then, onSubmit: next.onSubmit
            }
        }, onSubmit: (callback) => {
            next.onSubmitCallback = callback;
            return {
                initializeData: next.initializeData, then: next.then, submitted: next.submitted
            }
        }
    };
    return next;
};

/**
 * 表单对外接口.
 *
 * @type {function(): {}} 表单对象.
 */
export const FORM = (() => {
    const self = {};

    /**
     * 新建表单.
     *
     * @param div 待渲染的dom元素.
     * @param environment 环境.
     */
    self.new = async (div, environment = "pc") => {
        div.innerHTML = "";
        const g = formGraph(div, "");
        g.collaboration.mute = true;
        g.environment = environment;

        await g.initialize();
        g.version = "1.0.0";
        const page = g.addPage("defaultFormPage");
        page.createNew("form", 0, 0);
        page.reset();
        page.fillScreen();
        environment !== "pc" && page.setHttpUtil(weHttpUtil());
        return formAgent(g, page);
    };

    /**
     * 编辑数据.
     *
     * @param div dom元素.
     * @param sheetConfigData 画布配置数据.
     * @param environment 环境.
     * @returns {Promise<{}>}
     */
    self.edit = async (div, sheetConfigData, environment = "pc") => {
        const g = formGraph(div, "");
        g.collaboration.mute = true;
        g.environment = environment;

        await g.initialize();
        g.deSerialize(sheetConfigData);
        const pageData = g.getPageData(0);
        const page = await g.edit(0, div, pageData.id);
        environment !== "pc" && page.setHttpUtil(weHttpUtil());
        return formAgent(g, page);
    };

    /**
     * 展示表单.
     *
     * @param div 待渲染的dom元素.
     * @param sheetConfigData 表单元数据.
     * @param environment 环境.
     * @param mode 模式.
     */
    self.run = (div, sheetConfigData, mode, environment = "pc") => {
        const g = formGraph(div, "");
        g.collaboration.mute = true;
        g.environment = environment;

        const next = createNext();
        g.initialize().then(() => {
            // 直接传入mode，否则会导致page的overrideMethods异常.
            const page = g.runPage("defaultFormPage", sheetConfigData, mode, next);
            environment !== "pc" && page.setHttpUtil(weHttpUtil());
            next.then && next.then(formAgent(g, page));
        });

        return next;
    };

    /**
     * 获取可用的图形类型列表.
     *
     * @returns {*[]} 图形类型列表.
     */
    self.getAvailableShapeTypes = () => {
        return AVAILABLE_SHAPE_TYPES;
    };

    return self;
})();