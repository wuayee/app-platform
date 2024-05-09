import {ALIGN, DOCK_MODE, PAGE_MODE} from '../../common/const.js';
import {container} from '../../core/container.js';
import {rectangle} from '../../core/rectangle.js';
import {customizedDrawer} from '../../core/drawers/htmlDrawer.js';
import {hitRegion} from '../../core/hitRegion.js';
import {grid} from '../../core/grid.js';
import {componentIdConfiguration} from "./configurations/componentIdConfiguration.js";
import {descriptionConfiguration} from "./configurations/descriptionConfiguration.js";
import {scriptConfiguration} from "./configurations/scriptConfiguration.js";
import {labelContainer} from "./component/labelContainer.js";
import {deleteRegion} from "./regions/deleteRegion.js";
import {rectangleDrawer} from "../../core/drawers/rectangleDrawer.js";

const STANDARD_HEIGHT = 25, STANDARD_WIDTH = 100;
const MAIN_COLOR = "DIMGRAY", DASH_COLOR = "SILVER";
const ASSIT_COLOR = "whitesmoke", HIGH_LIGHT_COLOR = "black", DECORATE_COLOR = "orange";
const NODE_PAD = 14, NODE_HEIGHT = 22, CHECK_WIDTH = 14;
const CHECK_STATE = {CHECKED: "checked", PART_CHECKED: "partChecked", UNCHECKED: "unchecked"};

const formComponent = (type, id, x, y, width, height, parent, drawer) => {
    const self = type(id, x, y, width, height, parent, drawer);
    self.type = self.type === "container" ? "formContainerComponent" : "formComponent";

    /* 添加componentId，主要是在脚本中使用，用于唯一标识某一个组件 */
    self.serializedFields.batchAdd("componentId", "dockWeight", "script", "meta");
    self.namespace = "dynamic-form";
    self.padLeft = 0;

    // 适配ucd样式.
    self.backColor = "#ffffff";
    self.borderColor = "#d9dadb";
    self.focusBackColor = "#f7faff";
    self.focusBorderColor = "#047bfc";
    self.cornerRadius = 5;
    self.dashWidth = 5; // 边框设置为虚线.

    // 动态表单的图形都不需要绘制focusFrame.
    self.drawer.drawFocusFrame = () => {};

    /**
     * 获取表单对象.
     *
     * @returns {*|null} 表单对象.
     */
    self.getForm = () => {
        let parent = self;
        while (!parent.isTypeof("page") && !parent.isTypeof("form")) {
            parent = parent.getContainer();
            if (!parent) {
                return null;
            }
        }
        return parent;
    };

    /**
     * 持久化数据.
     *
     * @returns {*} 持久化所需的数据格式.
     */
    self.persist = () => {
        const serialized = self.serialize();
        const cloned = {...serialized};
        if (self.isTypeof("container")) {
            cloned.shapes = self.getShapes().filter(s => s.persist).map(s => s.persist());
        }
        return cloned;
    };

    /**
     * 重写runCode方法，支持裸脚本的方式.
     *
     * @override
     */
    self.runCode = (code) => {
        const codeString = self.get(code);
        if (!codeString) {
            return;
        }
        const complementCode = "function(shapeStore, shape) {\n" + codeString + "\n}";
        try {
            const evalString = "(async " + complementCode + ")(self.page.shapeStore, self);";
            eval(evalString);
        } catch (e) {
            console.warn("user input code execute error:\n" + e);
        }
    };

    /**
     * 获取配置.
     *
     * @override
     */
    self.getConfigurations = () => {
        const configs = [];
        configs.push(componentIdConfiguration(self, "componentId"));
        configs.push(descriptionConfiguration(self, "description"));
        configs.push(scriptConfiguration(self, "script"));
        return configs;
    };

    /**
     * 执行用户设置的脚本.
     */
    self.runScript = () => {
        self.runCode("script");
    };

    /**
     * 重写click方法，提供点击后的生命周期函数
     *
     * @override
     */
    const click = self.click;
    self.click = (x, y) => {
        click.apply(self, [x, y]);
        self.formClicked(self.page.shapeStore);
    };

    /**
     * 重写选择类控件的onChecked方法，提供选项更改后的生命周期函数
     * TODO(王成): 重写上传类控件的相应方法
     */

    if (self.onChecked) {
        const onChecked = self.onChecked;
        self.onChecked = (value) => {
            onChecked.apply(self, [value]);
            self.formContentChanged(self.page.shapeStore);
        };
    }

    /**
     * 表单控件渲染完毕后，应调用此方法，触发相关生命周期函数
     */
    self.notifyRendered = () => {
        // 如果是容器，则遍历其子元素.
        if (self.isTypeof("container")) {
            self.getShapes().forEach(s => {
                s.notifyRendered && s.notifyRendered();
            });
        }
        self.formRendered(self.page.shapeStore);
    };

    /**
     * 外层页面获得新数据并期望更新表单组件时，应调用此方法并传入自定义数据，触发相关生命周期函数
     */
    self.loadCustomizedData = (data) => {
        // 先处理本身的数据，当图形是容器时，再处理子元素.
        self.formDataRetrieved(self.page.shapeStore, data);

        // 如果是容器，则遍历其子元素.
        if (self.isTypeof("container")) {
            self.getShapes().forEach(s => {
                s.loadCustomizedData && s.loadCustomizedData(data);
            });
        }

        // 加载数据之后，统一刷新form表单.
        self.isTypeof("form") && self.invalidate();
    };

    /**
     * 生命周期函数，具体实现由组件开发者在编排页面注入.
     * 在表单UI初始化完毕后触发，只触发一次。注意此时表单的数据加载尚未结束，Elsa无法确保表单UI组件处于一致可用状态。
     * 请慎重使用此生命周期函数，原则上应避免对表单UI进行直接操作
     */
    self.formInitialized = () => {
    };

    /**
     * 生命周期函数，具体实现由组件开发者在编排页面注入.
     * 在加载了数据的表单UI首次渲染完毕后触发，只触发一次。此函数被调用时，Elsa保证表单处于一致可交互状态。
     */
    self.formRendered = () => {
    };


    /**
     * 生命周期函数，具体实现由组件开发者在编排页面注入.
     * 在每次外层页面获取数据，并尝试更新表单控件时触发，可多次触发。此函数被调用时，Elsa保证当前智能表单控件已经完成初始化。
     */
    self.formDataRetrieved = () => {
    };

    /**
     * 生命周期函数，具体实现由组件开发者在编排页面注入.
     * 在表单元素被点击时触发。如果对表单元素内容更改感兴趣，应优先使用语义更加正确的formContentChanged生命周期函数。
     */
    self.formClicked = () => {
    };

    /**
     * 生命周期函数，具体实现由组件开发者在编排页面注入.
     * 在表单元素的内容（选项）改变时触发，只适用于选择类和上传类表单元素
     */
    self.formContentChanged = () => {
    };

    /**
     * 是否激活交互功能.
     *
     * @returns {boolean} true/false.
     */
    self.getEnableInteract = () => {
        return false;
    };

    /**
     * 生命周期函数，具体实现由组件开发者在编排页面注入.
     * 在表单元素的内容（选项）改变时触发，只适用于选择类和上传类表单元素
     */
    self.formContentChanged = (page, shape, shapeStore) => {};

    /**
     * 是否允许region.
     *
     * @returns {boolean} true/false.
     */
    self.enableRegion = () => {
        return true;
    };

    /**
     * 只是为了能modeManager中能进行重写.
     *
     * @override
     */
    self.getBorderWidth = () => {
        return self.borderWidth;
    };

    self.addDetection(['componentId'], (property, value, preValue) => {
        if (value === preValue) {
            return;
        }
        self.meta.forEach(m => m.key = value);
    });

    return self;
};

/**
 * 模拟html的dom元素div
 * 辉子 2021
 */
let htmlDiv = (id, x, y, width, height, parent, drawer) => {
    let self = formComponent(container, id, x, y, width, height, parent, drawer);
    self.type = "htmlDiv";
    self.borderColor = DASH_COLOR;
    self.ifMaskItems = false;
    self.editable = false;

    // 这里有1的borderWidth，因此这里不能是0.
    self.itemPad = [2, 2, 10, 10];
    self.height = self.width = 200;
    self.itemSpace = 8;

    /* 默认从左边开始排列子组件 */
    self.dockAlign = ALIGN.LEFT;

    /* 默认水平排列 */
    self.dockMode = DOCK_MODE.HORIZONTAL;

    /**
     * 重写对dockMode的detection，当dockMode发生变化时，需要同步修改对应的dockAlign.
     */
    self.removeDetection("dockMode");
    self.addDetection(["dockMode"], (property, value, preValue) => {
        if (value === preValue) {
            return;
        }
        switch (value) {
            case DOCK_MODE.VERTICAL:
                self.dockAlign = ALIGN.TOP;
                break;
            case DOCK_MODE.HORIZONTAL:
                self.dockAlign = ALIGN.LEFT;
                break;
            default:
                break;
        }

        self.invalidate();
    });

    /**
     * 根据权重计算图形的宽或高.
     *
     * @override
     */
    // todo@zhangyue weight存在问题，暂时注释掉.
    // self.calculateDivision = (shape, area) => {
    //     const key = self.dockMode === DOCK_MODE.VERTICAL ? "height" : "width";
    //     const arrangeShapes = self.getArrangeShapes();
    //     const totalWeight = arrangeShapes.reduce((total, s) => total + s.dockWeight, 0);
    //     return (area[key] - self.itemSpace * (arrangeShapes.length - 1)) * shape.dockWeight / totalWeight;
    // };

    /**
     * 这里需要调用invalidate方法.
     * 当调用动态调用fillScreen时，若只调用invalidateAlone，会导致子元素不会进行重绘从而显示错乱.
     *
     * @override
     */
    self.resized = () => {
        if (self.page.disableReact) {
            return;
        }
        self.effectLines();
        self.effectGroup();
        self.invalidate();
        self.runCode("resizedCode");
    };

    return self;
};

/**
 * 模拟html的提交表单
 * 辉子 2021
 */
let form = (id, x, y, width, height, parent) => {
    let self = formComponent(container, id, x, y, width, height, parent);
    self.type = "form";
    self.serializedFields.batchAdd("formLoadedCode", "aippId", "aippInstanceId", "tenantId", "protocol", "domains");

    // 在type设置之后.
    self.text = "dynamic form";
    self.itemPad = [0, 0, 10, 10];
    self.fontColor = "whitesmoke";
    self.dockAlign = ALIGN.TOP;
    self.dockMode = DOCK_MODE.VERTICAL;
    self.cornerRadius = 8;
    self.autoFit = true;
    self.selectable = false;
    self.childDragable = true;
    self.backColor = "white";
    self.borderWidth = 0;

    /**
     * @override
     */
    self.childAllowed = child => child.namespace === self.namespace;

    /**
     * 获取整个表单的用户自定义数据.
     *
     * @returns {{}} 数据.
     */
    self.getData = () => {
        const json = {};
        self.traverse(s => {
            if (s.getData) {
                const data = s.getData();
                Object.keys(data).forEach(k => {
                    json[k] = data[k];
                });
            }
        });
        return json;
    };

    /**
     * form被加载之后.
     */
    self.formLoaded = () => {
        self.runCode("formLoadedCode");
        self.loaded = true;
        self.traverse(s => {
            s.formLoaded && s.formLoaded(self);
            s.formInitialized && s.formInitialized(self.page.shapeStore);
        });
    };

    /**
     * 遍历form中的所有子孙元素.
     *
     * @param action 子孙元素要执行的动作.
     */
    self.traverse = (action) => {
        const shapes = self.getShapes();
        while (shapes.length !== 0) {
            const shape = shapes.shift();
            action(shape);
            if (shape.isTypeof("container")) {
                shape.getShapes().forEach(s => shapes.push(s));
            }
        }
    };

    /**
     * 重写垂直排布方法.当
     *
     * @override
     */
    const vertical = self.vertical;
    self.vertical = () => {
        vertical.apply(self);
        if (self.autoFit && self.page.isReady) {
            self.page.interactDrawer.reset();
        }
    };

    /**
     * form在dataRetrieved时，需要保存部分参数，方便后续调用.
     *
     * @override
     */
    self.formDataRetrieved = (shapeStore, data) => {
        if (!data) {
            return;
        }
        data.aippId && (self.aippId = data.aippId);
        data.aippInstanceId && (self.aippInstanceId = data.aippInstanceId);
        data.tenantId && (self.tenantId = data.tenantId);
        data.protocol && (self.protocol = data.protocol);
        data.aippVersion && (self.aippVersion = data.aippVersion);
        if (data.domains) {
            self.domains = {...data.domains};
        }
    };

    /**
     * 图形删除时，不触发刷新，否则会导致其他删除图形的坐标异常，undo之后排列有问题.
     *
     * @override
     */
    self.shapeRemoved = () => {
    };

    /**
     * 当表单被提交后的回调.
     *
     * @param data 数据.
     */
    self.submitted = (data) => {
        // 提交后变更模式为history.
        self.page.serialize();
        const serialized = self.graph.serialize();
        self.page.clear();
        self.page.mode = PAGE_MODE.HISTORY;
        self.page.loadForm(serialized);
        self.submittedCallback && self.submittedCallback(data);
    };

    return self;
};

/**
 * 模拟html的dom元素table
 * 辉子 2021
 */
let htmlTable = (id, x, y, width, height, parent) => {
    let self = grid(id, x, y, width, height, parent);
    self.type = "htmlTable";
    return self;
};

/**
 * 标签，写字用
 * 辉子 2021
 */
let htmlLabel = (id, x, y, width, height, parent, drawer) => {
    let self = formComponent(rectangle, id, x, y, width, height, parent, drawer);
    self.type = "htmlLabel";
    self.text = "some label";
    // 中和textArea的10px内边距
    self.padTop = -10;
    self.height = STANDARD_HEIGHT;
    self.width = STANDARD_WIDTH;
    self.focusBorderColor = self.borderColor = DASH_COLOR;
    self.fontColor = MAIN_COLOR;
    self.hAlign = ALIGN.RIGHT;
    self.borderWidth = 0;
    self.backColor = "transparent";
    return self;
};

/**
 * 模拟html的dom元素input
 * 辉子 2021
 */
let htmlText = (id, x, y, width, height, parent, drawer) => {
    let self = formComponent(rectangle, id, x, y, width, height, parent, drawer ? drawer : htmlTextDrawer);
    self.type = "htmlText";
    self.serializedFields.add("placeholder");
    self.cornerRadius = 5;
    self.text = "some text";
    self.hAlign = ALIGN.LEFT;
    self.borderWidth = 1;
    self.focusBorderColor = self.borderColor = DASH_COLOR;
    self.focusBorderColor = self.borderColor = MAIN_COLOR;
    self.dataBinding = null;
    self.height = 110;

    // 适配ucd样式.
    self.backColor = "#ffffff";
    self.borderWidth = 0.53;
    self.borderColor = "#000000";
    self.mouseInBorderColor = "rgb(4, 123, 252)";
    self.fontColor = "rgba(0, 0, 0, 0.85)";
    self.mouseInFontColor = "rgba(0, 0, 0, 0.85)";

    self.placeholder = "请输入...";

    self.click = (x, y) => {
        self.beginEdit(x, y);
    };

    /**
     * form加载完成之后，如果是RUNTIME模式，则直接开启编辑.
     */
    self.formLoaded = () => {
        self.beginEdit();
    };

    /**
     * 解决图形跟随text自动变大后，变大的部分无法感应鼠标事件的问题.
     */
    const resize = self.drawer.resize;
    self.drawer.resize = () => {
        resize.apply(self.drawer);
        self.indexCoordinate();
    };

    return self;
};

/**
 * @override
 */
const htmlTextDrawer = (shape, div, x, y) => {
    const self = rectangleDrawer(shape, div, x, y);;

    /**
     * 监听text的高度，并通知form刷新.
     *
     * @override
     */
    const initialize = self.initialize;
    self.initialize = () => {
        initialize.apply(self);

        // 监听text的大小变化，并通知form进行刷新.
        new ResizeObserver(() => {
            if (!self.text
                || self.text.clientHeight === 0
                || shape.height === self.text.clientHeight) {
                return;
            }
            shape.height = self.text.clientHeight;
            const form = shape.getForm();
            if (form.loaded) {
                form.invalidate();
            }
        }).observe(self.text);
    };

    return self;
};

/**
 * 模拟html的dom元素输入框，包含一个label和一个text
 */
const htmlInput = (id, x, y, width, height, parent) => {
    const self = labelContainer(id, x, y, width, height, parent);
    self.type = "htmlInput";
    self.height = 160;
    self.autoFit = true;
    self.dockAlign = ALIGN.TOP;
    self.dockMode = DOCK_MODE.VERTICAL;
    self.componentId = "input_" + self.id;

    self.meta = [{
        key: self.componentId, type: 'string', name: '输入_' + self.id
    }];

    /**
     * @override.
     */
    self.createDeleteRegion = () => {
        return deleteRegion(self, undefined, () => {
            return 17;
        });
    };

    /**
     * 获取数据.
     *
     * @returns {{}} 数据.
     */
    self.getData = () => {
        let result = {};
        result[self.meta[0].key] = self.getInputText().getShapeText();
        return result;
    };

    /**
     * 接收数据并设置.
     *
     * @override
     */
    self.formDataRetrieved = (shapeStore, data) => {
        const text = data[self.meta[0].key];
        if (text) {
            self.getInputText().text = text.replaceAll("\n", "<br>");
            self.getForm().invalidate();
        }
    };

    /**
     * 初始化input.
     */
    const initialize = self.initialize;
    self.initialize = () => {
        const label = initialize.apply(self);
        label.text = "关键词";

        const inputText = self.page.createShape("htmlText", x, y + label.height);
        inputText.text = "";
        inputText.container = self.id;
        inputText.selectable = false;
        inputText.borderWidth = 1;
        inputText.cornerRadius = 4;
        inputText.borderColor = "rgb(4, 123, 252)";
        inputText.backColor = "rgb(230, 242, 255)";
        inputText.fontSize = 16;
        inputText.fontWeight = 400;
        inputText.autoHeight = true;
        inputText.minHeight = 110;
    };

    /**
     * 获取文本输入组件.
     *
     * @returns {*} 文本输入组件.
     */
    self.getInputText = () => {
        let inputText = self.getShapes().find(s => s.type === "htmlText");
        if (!inputText) {
            throw new Error("inputText not exist in the htmlInput.");
        }
        return inputText;
    };

    /**
     * input只允许label和text类型的子元素.
     *
     * @override
     */
    self.childAllowed = (child) => {
        return child.isTypeof("htmlLabel") || child.isTypeof("htmlText");
    };

    self.removeDetection("dockMode");
    self.addDetection(["dockMode"], (property, value, preValue) => {
        if (value === preValue) {
            return;
        }
        switch (value) {
            case DOCK_MODE.VERTICAL:
                self.dockAlign = ALIGN.TOP;
                self.getLabel().hAlign = ALIGN.LEFT;
                break;
            case DOCK_MODE.HORIZONTAL:
                self.dockAlign = ALIGN.LEFT;
                self.getLabel().hAlign = ALIGN.RIGHT;
                break;
            default:
                break;
        }

        self.invalidate();
    });

    return self;
};

/**
 * 模拟html的dom元素hr
 * 辉子 2021
 */
let htmlHr = (id, x, y, width, height, parent) => {
    let self = rectangle(id, x, y, width, height, parent);
    self.type = "htmlHr";
    self.editable = false;
    self.initConnectors = () => {
        self.connectors = [];
        self.text = "";
        // self.height = 3;
    };
    self.borderWidth = 0;
    self.backColor = MAIN_COLOR;
    const invalidate = self.invalidate;
    self.invalidate = () => {
        self.height = 3;
        invalidate.call(self);
    };
    return self;
};

/**
 * 模拟html的dom下拉列表
 * 辉子 2021
 */
let htmlComobox = (id, x, y, width, height, parent) => {
    let drawer = customizedDrawer("select", (select, d) => {
        d.shape.element = select;
        select.style.color = MAIN_COLOR;
        select.innerHTML = "<option value='1'>Option1</option><option value='2'>Option2</option><option value='3'>Option3</option><option value='4'>Option4</option>";
    });
    let self = rectangle(id, x, y, STANDARD_WIDTH, STANDARD_HEIGHT, parent, drawer);
    self.borderColor = MAIN_COLOR;
    self.type = "htmlComobox";
    self.editable = false;
    //self.click = ()=>self.element.fireEvent("onClick"); todo:how to send the event back to the event

    return self;
};

/**
 * 模拟html的dom列表
 * 辉子 2021
 */
let htmlListBox = (id, x, y, width, height, parent) => {
    let self = htmlComobox(id, x, y, width, height, parent);
    self.type = "htmlListBox";
    self.height = 80;
    self.element.size = "10";
    return self;
};

//------------------------------tab--------------------------------------
/**
 * 页签群
 * 辉子 2021
 */
let tab = (id, x, y, width, height, parent) => {
    let self = container(id, x, y, width, height, parent);
    self.type = "tab";
    self.width = 400;
    self.height = 280;
    self.dockMode = DOCK_MODE.FILL;
    self.itemPad = [1, 4, 32, 1];
    self.borderColor = self.fontColor = MAIN_COLOR;
    self.backColor = "white";
    self.showNewTabIcon = true;
    self.text = "";
    self.editable = false;
    self.childAllowed = child => child.isType('tabPage');

    self.getTabPages = () => self.getShapes();
    self.getSelectedPage = () => self.getShapes().find(s => s.visible);

    self.addTabPage = function (title) {
        const shapes = self.getShapes(s => s.isType('tabPage'));
        shapes.forEach(s => s.visible = false);
        let tp = self.page.createNew("tabPage", 0, 0, undefined, {container: self.id, text: title}, undefined, true);
        // tp.tabLabelRegion = tabLabelRegion(tp, self);
        // tp.container = self.id;
        // tp.text = title;
        return tp;
    };
    self.removeTabPage = function (tabpage) {
        let index = tabPage.selectRegion.index - 1;
        tabpage.remove();
        if (index >= 0) {
            self.getShapes()[index].select();
        }
    };

    self.initialize = args => {
        self.addTabPage("新页面1");
        self.addTabPage("新页面2");
        self.addTabPage("新页面3");
        self.invalidate();
    };
    let keyPressed = self.keyPressed;
    self.keyPressed = e => {
        if (e.code === "KeyN" && (e.shiftKey)) {
            self.addTabPage("新页面" + (self.getShapes().length + 1));
            return false;
        }
        return keyPressed.apply(self, [e]);
    };
    const arrangeShapes = self.arrangeShapes;
    self.arrangeShapes = () => {
        arrangeShapes.call(self);
        const pages = self.getShapes(s => s.isType('tabPage'));
        pages.forEach(p => {
            (!p.tabLabelRegion) && (p.tabLabelRegion = tabLabelRegion(p, self));
        });
    };

    return self;
};

/**
 * 某个页签
 * 辉子 2021
 */
let tabPage = (id, x, y, width, height, parent) => {
    let self = htmlDiv(id, x, y, width, height, parent);
    self.type = "tabPage";
    self.hideText = true;

    self.initConnectors = () => self.connectors = [];
    self.containerAllowed = parent => parent.isType('tab');

    self.selected = () => {
        let parent = self.getContainer();
        if (parent.type !== "tab") {
            return;
        }
        parent.isFocused = false;
        parent.getShapes().forEach(s => s.isFocused = s.visible = false);
        self.isFocused = self.visible = true;
        parent.invalidate();
    };

    self.afterRemoved = () => {
        let parent = self.getContainer();
        parent.removeRegion(self.tabLabelRegion.index);
        parent.invalidate();

    };

    //--------------------------sdetection------------------------------
    self.addDetection(["text"], (property, value, preValue) => {
        self.getContainer().invalidate();
    });
    self.addDetection(["visible"], (property, value, preValue) => {
        self.tabLabelRegion.draw();
    });
    //-------------------------------------------------------------------

    self.keyPressed = e => self.getContainer().keyPressed(e);

    return self;
};

let tabLabelRegion = (tabpage, tab) => {
    if (tabPage.tabLabelRegion) {
        return tabPage.tabLabelRegion;
    }
    const MIN_WIDTH = 50;
    let getx = (tab, self) => tab.regions.filter(r => r.type === 'tabLabelRegion' && r.index < self.index).sum(r => r.width);
    let gety = (tab, self) => 5;
    //let getWidth = (tab, self) => self.textWidth + 10;
    //let getHeight = () => 25;
    let self = hitRegion(tab, getx, gety);//, getWidth, getHeight);
    self.type = "tabLabelRegion";
    self.editable = true;

    self.getEditRect = () => ({x: self.x + 18, y: self.y + 3, width: self.width - 18, height: self.height - 6});
    self.resized = () => tab.regions.filter(r => r.index > self.index).forEach(r => r.draw(0, 0));
    self.drawStatic = (context, x, y) => {
        let fontColor = MAIN_COLOR;
        let iconColor = MAIN_COLOR;
        context.font = "normal bold 12px Arial";

        self.text = tabpage.text;
        let width = Math.round(context.measureText(tabpage.text).width) + 30;
        if (width < MIN_WIDTH) {
            width = MIN_WIDTH;
        }
        if (self.width !== width) {
            self.width = width;
            self.draw(0, 0);
            return;
        }
        if (tabpage.visible) {
            context.beginPath();
            context.rect(0, 0, self.width, self.height);
            context.fillStyle = ASSIT_COLOR;
            context.fill();
            fontColor = HIGH_LIGHT_COLOR;
            iconColor = DECORATE_COLOR;
        }
        context.beginPath();
        context.moveTo(self.width - 2, 3);
        context.lineTo(self.width - 2, self.height - 6);
        context.strokeStyle = MAIN_COLOR;
        context.lineWidth = 1;
        context.stroke();
        context.beginPath();
        context.arc(x + 10, y + 10, 3, 0, 2 * Math.PI);
        context.fillStyle = iconColor;
        context.fill();
        context.fillStyle = fontColor;
        context.fillText(self.text, self.getEditRect().x - self.x, self.getEditRect().y + 11 - self.y);
    };

    let endEdit = self.endEdit;
    self.endEdit = (text) => {
        tabpage.text = text;
        endEdit.apply(self, [text]);
    };
    self.click = () => {
        tab.getShapes(p => p.type === "tabPage").forEach(p => p.visible = false);
        tabpage.visible = true;
        tabpage.select();
        tab.invalidate();
    };

    return self;
};

//-----------------------------tree-------------------------------
/**
 * 树
 * 辉子 2021
 */
const htmlTree = (id, x, y, width, height, parent) => {
    const self = container(id, x, y, width, height, parent);
    self.type = "htmlTree";
    self.width = 150;
    self.height = 180;
    self.text = "";
    self.editable = false;
    self.itemPad = [1, 3, 1, 1];
    self.itemSpace = 0;
    self.dockMode = DOCK_MODE.VERTICAL;
    self.borderColor = self.fontColor = MAIN_COLOR;
    self.backColor = "white";
    self.nodeSpace = NODE_HEIGHT;
    self.childAllowed = child => child.isType('htmlTreeNode');

    self.addNode = (title = "new node") => {
        return self.page.createNew("htmlTreeNode", 0, 0, undefined, {container: self.id, text: title}, self, true);
        // node.container = self.id;
        // node.text = title;//title === undefined ? "new node" : title;
        // return node;
    };
    self.getNodes = () => self.getShapes(s => s.isType('htmlTreeNode'));

    self.initialize = args => {
        let n = self.addNode().addNode();
        n.addNode();
        n.addNode();
        n = self.addNode();
        n.addNode();
        n.addNode();
        self.addNode();
    };
    let invalidate = self.invalidate;
    self.invalidate = () => {
        self.getNodes().forEach(n => {
            n.height = self.nodeSpace;
            n.padLeft = NODE_PAD * n.level + CHECK_WIDTH;
        });
        invalidate.apply(self);
    };

    //--------serialize and property listener-----------
    // self.serializedFields.batchAdd("nodeSpace");

    return self;
};

/**
 * 树上某个节点
 * 辉子 2021
 */
let htmlTreeNode = (id, x, y, width, height, parent) => {
    let self = rectangle(id, x, y, width, height, parent);
    self.type = "htmlTreeNode";
    self.parentNode = "";//parentNode is node id
    self.borderWidth = 0;
    self.level = 1;
    self.padLeft = NODE_PAD + CHECK_WIDTH;
    self.expanded = true;
    self.checked = CHECK_STATE.UNCHECKED;
    self.height = NODE_HEIGHT;
    self.hAlign = ALIGN.LEFT;
    self.enableAnimation = false;
    self.containerAllowed = parent => parent.isType('htmlTree');
    self.initConnectors = () => self.connectors = [];
    self.addNode = (title = "new node") => {
        const properties = {
            container: self.container, parentNode: self.id, level: self.level + 1, text: title, expanded: true
        }
        let node = self.page.createNew("htmlTreeNode", 0, 0, undefined, properties, undefined, true);
        node.container = self.container;
        node.parentNode = self.id;
        node.level = self.level + 1;
        node.text = title;
        self.expanded = true;
        return node;
    };
    self.getParentNode = () => self.getContainer().getShapes().find(s => s.id === self.parentNode);
    self.getNodes = () => self.getContainer().getShapes().filter(s => s.parentNode === self.id);
    self.checkChanged = (checked) => {
    };
    self.expandChanged = (expanded) => {
    };

    //---------------add hitregions---------------------
    expandRegion(self);
    checkRegion(self);

    //--------------------------serialization & detection------------------------------
    // self.serializedFields.batchAdd("parentNode");
    // self.serializedFields.batchAdd("level");
    // self.serializedFields.batchAdd("checked");
    // self.addDetection(["level"], (property, value, preValue) => {
    //     self.padLeft = NODE_PAD * value + CHECK_WIDTH;
    // });
    self.addDetection(["expanded"], (property, value, preValue) => {
        let parent = self.getContainer();
        let setVisible = (node, value) => {
            node.visible = value;
            parent.getShapes().filter(s => s.parentNode === node.id).forEach(s => setVisible(s, value && node.expanded));
        };
        parent.getShapes().filter(s => s.parentNode === self.id).forEach(s => setVisible(s, value && self.visible));
        parent.invalidate();
        self.expandChanged(self.expanded);

    });
    self.addDetection(["checked"], (property, value, preValue) => {
        let parent = self.getContainer();
        let checkedReact = () => {
            //set children checked
            parent.getShapes().filter(s => s.parentNode === self.id).forEach(node => {
                if (node.checked !== CHECK_STATE.CHECKED) {
                    node.checked = CHECK_STATE.CHECKED;
                }
            });
            //set parents part checked or full checked
            if (self.parentNode === "") {
                return;
            }
            self.page.getShapeById(self.parentNode).checked = (parent.getShapes().filter(s => s.parentNode === self.parentNode && s.checked !== CHECK_STATE.CHECKED).length === 0) ? CHECK_STATE.CHECKED : CHECK_STATE.PART_CHECKED;
        };
        let partCheckedReact = () => {
            if (self.parentNode !== "") {
                self.page.getShapeById(self.parentNode).checked = CHECK_STATE.PART_CHECKED;
            }
        };
        let uncheckedReact = () => {
            //set children unchecked
            parent.getShapes().filter(s => s.parentNode === self.id).forEach(node => {
                if (node.checked !== CHECK_STATE.UNCHECKED) {
                    node.checked = CHECK_STATE.UNCHECKED;
                }
            });
            //set parents part checked or unchecked
            if (self.parentNode === "") {
                return;
            }
            self.page.getShapeById(self.parentNode).checked = (parent.getShapes().filter(s => s.parentNode === self.parentNode && s.checked !== CHECK_STATE.UNCHECKED).length === 0) ? CHECK_STATE.UNCHECKED : CHECK_STATE.PART_CHECKED;
        };
        eval((value + "React()"));
        self.invalidate();
        self.checkChanged(self.checked);
    });
    //----------------------------------------------------------------------------------------------

    return self;
};

let expandRegion = node => {
    let getx = () => 0;
    let gety = () => 0;
    let getWidth = (shape, self) => {
        self.width = node.padLeft - CHECK_WIDTH;
        return self.width;
    };
    let self = hitRegion(node, getx, gety, getWidth);
    self.height = node.height;
    self.editable = false;
    self.type = "expandRegion";

    self.drawStatic = (context, x, y) => {
        const MID_Y = 12;
        if (node.getNodes().length === 0) {
            context.drawLine(self.width - 7, MID_Y, self.width - 2, MID_Y, 1, MAIN_COLOR);
        } else {
            context.font = "normal 14px Arial";
            context.strokeText(node.expanded ? "-" : "+", self.width - (node.expanded ? 9 : 10), node.expanded ? 15 : 17);
        }
        if (node.parentNode !== "") {
            let level = node.level;
            while (level > 0) {
                const finaly = (node.level - level) === 0 ? MID_Y : node.height;
                context.dashedLineTo(self.width - 7 - (node.level - level) * NODE_PAD, 0, self.width - 7 - (node.level - level) * NODE_PAD, finaly, 2, 1, MAIN_COLOR);
                level--;
            }
        }
    };

    self.click = () => {
        node.expanded = !node.expanded;
    };
    return self;
}
let checkRegion = node => {
    let getx = node => node.padLeft - CHECK_WIDTH;
    let gety = node => 6;
    let self = hitRegion(node, getx, gety);
    self.width = self.height = CHECK_WIDTH;
    self.editable = false;
    self.type = "checkRegion";

    self.drawStatic = (context, x, y) => {
        const OFFSET = 2, R = 11;
        context.beginPath();
        context.rect(0, 0, R, R)
        context.strokeStyle = MAIN_COLOR;
        context.lineWidth = 1;
        context.stroke();

        if (node.checked !== CHECK_STATE.UNCHECKED) {
            context.beginPath();
            context.rect(OFFSET, OFFSET, R - 2 * OFFSET, R - 2 * OFFSET)
            context.fillStyle = node.checked === CHECK_STATE.PART_CHECKED ? DASH_COLOR : MAIN_COLOR;
            context.fill();
        }
    };

    self.click = () => {
        node.checked = (node.checked !== CHECK_STATE.CHECKED) ? CHECK_STATE.CHECKED : CHECK_STATE.UNCHECKED;
    };

    return self;
};

export {
    htmlDiv,
    form,
    htmlTable,
    htmlLabel,
    htmlText,
    htmlTextDrawer,
    htmlInput,
    htmlHr,
    htmlComobox,
    htmlListBox,
    tab,
    tabPage,
    htmlTree,
    htmlTreeNode,
    formComponent
};