import {CopyPasteHelpers, DIRECTION, node, rectangleDrawer, isPointInRect} from "@fit-elsa/elsa-core";
import ReactDOM from "react-dom/client";
import {DefaultRoot} from "@/components/DefaultRoot.jsx";
import {v4 as uuidv4} from "uuid";
import {Header} from "@/components/Header.jsx";
import {Footer} from "@/components/Footer.jsx";
import {NODE_STATUS, SECTION_TYPE, VIRTUAL_CONTEXT_NODE} from "@/common/Consts.js";
import React, {useRef, useState} from "react";
import {HORIZONTAL_LEFT, HORIZONTAL_RIGHT, VERTICAL_DOWN, VERTICAL_UP} from "@/components/asserts/waterDropRawSvg.js";
import RunningStatusPanel from "@/components/flowRunComponent/RunningStatusPanel.jsx";
import {Button} from "antd";
import ToolIcon from './asserts/icon-tool.svg?react';

/**
 * jadeStream中的流程编排节点.
 *
 * @override
 */
export const jadeNode = (id, x, y, width, height, parent, drawer) => {
    const self = node(id, x, y, width, height, parent, false, drawer ? drawer : jadeNodeDrawer);
    self.type = "jadeNode";
    self.serializedFields.batchAdd("toolConfigs", "componentName", "flowMeta", "outlineWidth", "outlineColor", "sourcePlatform");
    self.eventType = "jadeEvent";
    self.hideText = true;
    self.autoHeight = true;
    self.width = 360;
    self.borderColor = "rgba(28,31,35,.08)";
    self.outlineColor = "rgba(74,147,255,0.12)";
    self.borderWidth = 1;
    self.focusBorderWidth = 1;
    self.outlineWidth = 10;
    self.dashWidth = 0;
    self.backColor = "white";
    self.focusBackColor = "white";
    self.borderRadius = 8;
    self.cornerRadius = 8;
    self.enableAnimation = false;
    self.modeRegion.visible = false;
    self.runStatus = NODE_STATUS.DEFAULT;
    self.emphasizedOffset = -5;
    self.flowMeta = {
        "triggerMode": "auto",
        "jober": {
            "type": "general_jober",
            "name": "",
            "fitables": [],
            "converter": {
                "type": "mapping_converter"
            },
        }
    };
    self.sourcePlatform = "official";

    const observed = [];

    /**
     * 默认的header配置.
     *
     * @return 下拉按钮配置项
     */
    self.getToolMenus = () => {
        return [{
            key: '1', label: "复制", action: () => {
                self.duplicate();
            }
        }, {
            key: '2', label: "删除", action: () => {
                self.remove();
            }
        }, {
            key: '3', label: "重命名", action: (setEdit) => {
                setEdit(true);
            }
        }];
    };

    /**
     * 获取节点默认的测试报告章节
     */
    self.getRunReportSections = () => {
        // 这里的data是每个节点的每个章节需要展示的数据，比如工具节点展示为输入、输出的数据
        return [{no: "1", name: "输入", type: SECTION_TYPE.DEFAULT, data: self.input ? self.input : {}}, {
            no: "2",
            name: "输出",
            type: SECTION_TYPE.DEFAULT,
            data: self.getOutputData(self.output)
        }];
    };

    /**
     * 获取输出的数据
     *
     * @param source 数据源
     * @return {*|{}}
     */
    self.getOutputData = (source) => {
        if (self.runStatus === NODE_STATUS.ERROR) {
            return self.errorMsg;
        } else {
            return source ? source : {};
        }
    }

    /**
     * 获取节点默认的测试报告章节 todo 这里是否需要是先到DefaultRoot中，用来进行组件刷新操作，现目前状态可以刷新
     */
    self.setRunReportSections = (data) => {
        // 把节点推送来的的data处理成Section
        // 开始节点只有输入，结束节点只有输出，普通节点输入输出，条件节点有条件1...n和输出
        self.output = JSON.parse(data.parameters[0].output);
        self.input = JSON.parse(data.parameters[0].input);
        self.errorMsg = data.errorMsg;
        self.cost = data.runCost;
    };

    /**
     * 处理传递的元数据
     *
     * @param metaData 元数据信息
     */
    self.processMetaData = (metaData) => {
    };

    /**
     * 设置方向为W和N的connector不支持拖出连接线
     *
     * @override
     */
    const initConnectors = self.initConnectors;
    self.initConnectors = () => {
        initConnectors.apply(self);
        self.connectors.remove(c => c.direction.key === DIRECTION.S.key || c.direction.key === DIRECTION.N.key
                || c.direction.key === "ROTATE");
        self.connectors.forEach(connector => {
            connector.isSolid = true;
            if (connector.direction.key === DIRECTION.W.key) {
                connector.allowFromLink = false
            }
            if (connector.direction.key === DIRECTION.E.key) {
                connector.allowToLink = false
            }
        })
    };

    /**
     * 获取节点之前可达的节点信息
     *
     * @returns {*[]} 包含前方节点的名称和info信息的数组
     */
    self.getPreNodeInfos = () => {
        if (!self.allowToLink) {
            return [];
        }
        const allLines = self.page.shapes.filter(s => s.type === "jadeEvent");
        const formerNodesInfo = []; // 存储前方节点信息的数组
        const visitedShapes = new Set(); // 记录已经访问过的形状id

        /**
         * 递归函数，探索当前shape前方所有shape
         *
         * @param currentShapeId 当前shape的id
         */
        const explorePreShapesRecursive = (currentShapeId) => {
            // 如果当前形状已经访问过，则跳过
            if (visitedShapes.has(currentShapeId)) {
                return;
            }
            // 将当前形状id添加到visitedShapes中
            visitedShapes.add(currentShapeId);

            // 获取当前形状对象
            const currentShape = self.page.getShapeById(currentShapeId);
            if (!currentShape) {
                return; // 如果找不到当前形状对象，则返回
            }

            // 将当前形状的名称和获取到的info添加到formerNodes中
            formerNodesInfo.push({
                id: currentShape.id,
                node: currentShape,
                name: currentShape.text,
                observableList: currentShape.page.getObservableList(currentShape.id)
            });

            // 找到当前形状连接的所有线
            const connectedLines = allLines.filter(s => s.toShape === currentShapeId);

            // 遍历连接线，将每个连接线的起点形状ID递归探索
            for (const line of connectedLines) {
                explorePreShapesRecursive(line.fromShape);
            }
        };

        // 从当前节点开始启动递归
        explorePreShapesRecursive(self.id);
        // 统一加上虚拟上下文节点信息
        formerNodesInfo.push({
            id: VIRTUAL_CONTEXT_NODE.id,
            name: VIRTUAL_CONTEXT_NODE.name,
            observableList: self.page.getObservableList(VIRTUAL_CONTEXT_NODE.id)
        });
        formerNodesInfo.shift();
        return formerNodesInfo;
    };

    /**
     * 获取直接前继节点信息
     */
    self.getDirectPreNodeIds = () => {
        if (!self.allowToLink) {
            return [];
        }
        return self.page.shapes.filter(s => s.type === "jadeEvent")
                .filter(s => s.toShape === self.id)
                .map(line => self.page.getShapeById(line.fromShape));
    };

    /**
     * 监听dom容器resize的变化.
     */
    self.observe = () => {
        self.drawer.observe();
    };

    /**
     * 获取Header组件
     *
     * @param disabled 是否禁用.
     * @return {JSX.Element}
     */
    self.getHeaderComponent = (disabled) => {
        return (<Header shape={self} disabled={disabled}/>);
    }

    /**
     * 获取Footer组件
     *
     * @return {JSX.Element}
     */
    self.getFooterComponent = () => {
        return (<Footer shape={self}/>);
    }

    /**
     * 获取用户自定义组件.
     *
     * @return {*}
     */
    self.getComponent = () => {
        return self.graph.plugins[self.componentName](self.flowMeta.jober.converter.entity);
    };

    /**
     * 监听的值发生变化时触发.
     *
     * @param observableId 监听的id.
     * @param value 值.
     * @param type 类型.
     */
    self.emit = (observableId, {value, type}) => {
        const observable = self.page.getObservable(self.id, observableId);
        if (observable) {
            observable.observers.forEach(o => {
                if (o.status === "enable") {
                    o.observe({
                        value: value !== null && value !== undefined ? value : observable.value,
                        type: type !== null && type !== undefined ? type : observable.type
                    });
                }
            });

            // 是空字符串的情况下，需要修改值.
            if (value !== null && value !== undefined) {
                observable.value = value;
            }

            // 是空字符串的情况下，需要修改值.
            if (type !== null && type !== undefined) {
                observable.type = type;
            }
        }
    };

    /**
     * @override
     */
    const remove = self.remove;
    self.remove = (source) => {
        // 如果有连线，需要同时删除连线.
        const events = self.page.shapes
                .filter(s => s.isTypeof("jadeEvent"))
                .filter(s => s.fromShape === self.id || s.toShape === self.id);
        const lineRemoved = events.flatMap(e => e.remove());

        // 删除图形本身.
        const removed = remove.apply(self, [source]);

        // 清理observables.
        self.cleanObservables();

        return [...removed, ...lineRemoved];
    };

    /**
     * 复制节点.
     */
    self.duplicate = () => {
        const shapes = JSON.stringify([self.serialize()]);
        CopyPasteHelpers.pasteShapes(shapes, "", self.page);
    };

    /**
     * 更新jadeConfig.
     *
     * @override
     */
    const serialize = self.serialize;
    self.serialize = () => {
        if (self.getLatestJadeConfig) {
            self.serializerJadeConfig();
        }
        return serialize.apply(self);
    };

    self.serializerJadeConfig = () => {
        self.flowMeta.jober.converter.entity = self.getLatestJadeConfig();
    }

    // 可实现动态替换其中react组件的能力.
    self.addDetection(["componentName"], (property, value, preValue) => {
        if (value === preValue) {
            return;
        }
        self.drawer.unmountReact();
        self.invalidateAlone();
    });

    /**
     * 更新粘贴出来的图形的id
     *
     * @param entity entity
     */
    const updateCopiedNodeIds = entity => {
        if (typeof entity === 'object' && entity !== null) {
            if (Array.isArray(entity)) {
                entity.forEach((item) => {
                    updateCopiedNodeIds(item);
                });
            } else {
                Object.keys(entity).forEach((key) => {
                    if (key === 'id') {
                        entity[key] = uuidv4();
                    } else {
                        updateCopiedNodeIds(entity[key]);
                    }
                });
            }
        }
    };

    /**
     * 图形粘贴后的回调
     */
    self.pasted = () => {
        updateCopiedNodeIds(self.getEntity());
    };

    /**
     * 获取flowMeta的entity
     */
    self.getEntity = () => {
        return self.flowMeta.jober.converter.entity;
    };

    /**
     * 有子类重写.
     */
    self.getHeaderIcon = () => {
    };

    /**
     * 本方法提供默认值-工具，具体可由子类重写.
     */
    self.getHeaderTypeIcon = () => {
        return (
            <div className={"jade-node-custom-header-type-icon-wrapper"}>
                <ToolIcon className="jade-node-custom-header-type-icon"/>
            </div>
        );
    };

    /**
     * 监听.
     *
     * @param nodeId 被监听节点的id.
     * @param observableId 待监听的id.
     * @param observer 监听器.
     * @return {(function(): void)|*}
     */
    self.observeTo = (nodeId, observableId, observer) => {
        const preNodeInfos = self.getPreNodeInfos();
        const preNodeIdSet = new Set(preNodeInfos.map(n => n.id));
        const observerProxy = ObserverProxy(nodeId, observableId, observer);
        observerProxy.status = preNodeIdSet.has(nodeId) ? "enable" : "disable";
        observed.push(observerProxy);
        self.page.observeTo(nodeId, observableId, observerProxy);

        // 监听时，主动推送一次数据.
        const observable = self.page.getObservable(nodeId, observableId);
        observerProxy.observe({value: observable.value, type: observable.type});

        // 返回取消监听的方法.
        return () => {
            const index = observed.findIndex(o => o === observerProxy);
            observed.splice(index, 1);
            self.page.stopObserving(nodeId, observableId, observerProxy);
        };
    };

    /**
     * 断开连接.
     */
    self.offConnect = () => {
        const preNodeInfos = self.getPreNodeInfos();
        const preNodeIdSet = new Set(preNodeInfos.map(n => n.id));
        observed.filter(o => o.status === "enable").filter(o => !preNodeIdSet.has(o.nodeId)).forEach(o => {
            o.observe({value: null, type: null});
            o.status = "disable";
        });
        const nextNodes = getNextNodes();
        nextNodes.length > 0 && nextNodes.forEach(n => n.offConnect());
    };

    /**
     * 连接.
     */
    self.onConnect = () => {
        const preNodeInfos = self.getPreNodeInfos();
        const preNodeIdSet = new Set(preNodeInfos.map(n => n.id));
        observed.filter(o => o.status === "disable").filter(o => preNodeIdSet.has(o.nodeId)).forEach(o => {
            o.status = "enable";
            const observable = self.page.getObservable(o.nodeId, o.observableId);
            o.observe({value: observable.value, type: observable.type});
        });
        const nextNodes = getNextNodes();
        nextNodes.length > 0 && nextNodes.forEach(n => n.onConnect());
    };

    /**
     * 清理所有的observables.
     */
    self.cleanObservables = () => {
        // 清除我提供的observable
        self.page.removeObservable(self.id);

        // 清除我的observer.
        observed.forEach(o => self.page.stopObserving(o.nodeId, o.observableId, o));
    };

    /*
     * 获取下一批节点.
     */
    const getNextNodes = () => {
        const lines = self.page.shapes.filter(s => s.type === "jadeEvent").filter(l => l.fromShape === self.id);
        if (!lines || lines.length === 0) {
            return [];
        }
        return self.page.shapes.filter(s => s.type !== "jadeEvent").filter(s => lines.some(l => l.toShape === s.id));
    };

    /**
     * 校验节点状态是否正常.
     *
     * @return Promise 校验结果
     */
    self.validate = () => {
        return new Promise((resolve, reject) => {
            try {
                const preNodeInfos = self.getPreNodeInfos();
                const preNodeIdSet = new Set(preNodeInfos.map(n => n.id));
                observed.forEach(o => {
                    const node = self.page.getShapeById(o.nodeId);
                    if (!node) {
                        throw new Error("节点[" + o.nodeId + "]不存在.");
                    }
                    if (!preNodeIdSet.has(o.nodeId)) {
                        throw new Error("节点[" + node.text + "]和节点[" + self.text + "]未连接.");
                    }
                });

                // 调用 form 本身的校验能力，校验所有字段
                self.validateForm().then(resolve).catch(reject);
            } catch (error) {
                reject(error);
            }
        });
    };

    /**
     * 当节点被取消选中时，校验表单中的数据.
     */
    const unSelect = self.unSelect;
    self.unSelect = () => {
        unSelect.apply(self);
        self.validateForm && self.validateForm();
    };

    /**
     * 需要加上report的范围.
     *
     * @override
     */
    const getShapeFrame = self.getShapeFrame;
    self.getShapeFrame = (withMargin) => {
        const frame = getShapeFrame.apply(self, [withMargin]);
        const reportFrame = self.drawer.getReportFrame();
        if (!reportFrame) {
            return frame;
        }
        frame.x2 = reportFrame.x + reportFrame.width;
        frame.y2 = Math.max(frame.y2, reportFrame.y + reportFrame.height);
        return frame;
    };

    /**
     * 需要加上report的范围.
     *
     * @override
     */
    const getBound = self.getBound;
    self.getBound = () => {
        const bound = getBound.apply(self);
        const reportFrame = self.drawer.getReportFrame();
        if (!reportFrame) {
            return bound;
        }
        bound.width = reportFrame.x + reportFrame.width - bound.x;
        bound.height = Math.max(reportFrame.y + reportFrame.height, self.x + self.height)
                - Math.min(self.y, reportFrame.y);
        return bound;
    };

    /**
     * 节点的高度变化不需要触发dirties.
     *
     * @param property 属性名称.
     * @param value 属性值.
     * @param preValue 属性之前的值.
     * @return {boolean|*} true/false.
     */
    const load = self.load;
    self.load = () => {
        load.apply(self);
        const propertyChanged = self.propertyChanged;
        self.propertyChanged = (property, value, preValue) => {
            if (property === "height") {
                return false;
            }
            return propertyChanged.apply(self, [property, value, preValue]);
        };
    };

    /**
     * 设置节点状态.
     *
     * @param status 状态.
     */
    self.setRunStatus = (status) => {
        self.runStatus = status;
        self.emphasized = status === NODE_STATUS.RUNNING;
        const focused = self.page.getFocusedShapes();
        if (focused.length === 0) {
            self.isFocused = status === NODE_STATUS.RUNNING;
        }
        self.drawer.setRunStatus(status);
    };

    return self;
};

/**
 * 监听代理.
 *
 * @param nodeId 被监听节点的id.
 * @param observableId 待监听的id.
 * @param observer 监听器.
 * @return {{}}
 * @constructor
 */
const ObserverProxy = (nodeId, observableId, observer) => {
    const self = {};
    self.nodeId = nodeId;
    self.observableId = observableId;
    self.status = "enable";
    self.origin = observer;

    /**
     * 触发监听.
     *
     * @param args 参数.
     */
    self.observe = (args) => {
        if (self.status === "enable") {
            self.origin(args);
        }
    };

    return self;
};

const WATER_DROP_DISTANCE = 500;

/**
 * jadeNode绘制器.
 *
 * @override
 */
const jadeNodeDrawer = (shape, div, x, y) => {
    const self = rectangleDrawer(shape, div, x, y);
    self.reactContainer = null;
    self.panelRef = null;
    self.waterDrops = [];

    /**
     * @override
     */
    const initialize = self.initialize;
    self.initialize = () => {
        initialize.apply(self);
        self.reactContainer = document.createElement("div");
        self.reactContainer.id = "react-container-" + shape.id;
        self.reactContainer.style.width = "100%";
        self.parent.appendChild(self.reactContainer);
        self.parent.style.pointerEvents = "auto";
    };

    /**
     * 写在react.
     */
    self.unmountReact = () => {
        if (!self.root) {
            return;
        }
        self.root.unmount();
        self.root = null;
    };

    /**
     * @override
     */
    self.drawStatic = () => {
        if (!shape.componentName || self.root) {
            return;
        }
        self.root = ReactDOM.createRoot(self.reactContainer);
        self.root.render(<Root/>);
    };

    const Root = () => {
        self.panelRef = useRef();
        const [runStatus, setRunStatus] = useState(shape.runStatus);
        const [disabled, setDisabled] = useState(false);

        // 设置运行状态.
        self.setRunStatus = status => {
            setRunStatus(status);
        };

        // 设置是否禁用.
        self.setDisabled = (disabled) => {
            setDisabled(disabled);
        };

        // 当展示报告时，需要重新计算area.
        const onReportShow = () => {
            shape.indexCoordinate();
        };

        return (<>
            {runStatus !== NODE_STATUS.DEFAULT &&
                    <RunningStatusPanel shape={shape} ref={self.panelRef} onReportShow={onReportShow}/>}
            <div style={{position: "relative", background: "white", padding: 16, borderRadius: shape.borderRadius}}>
                <DefaultRoot shape={shape} component={shape.getComponent()} disabled={disabled} />
            </div>
        </>);
    };

    self.getReportFrame = () => {
        if (!self.panelRef || !self.panelRef.current || !self.panelRef.current.getRunReportRect()) {
            return null;
        }
        const rect = self.panelRef.current.getRunReportRect();
        const position = shape.page.calculatePosition({clientX: rect.x, clientY: rect.y})
        return {
            x: position.x,
            y: position.y,
            width: rect.width / shape.page.scaleX,
            height: rect.height / shape.page.scaleY
        }
    };

    /**
     * 存在report时，需要判断是否坐标在report dom中.
     *
     * @override
     */
    const containsBack = self.containsBack;
    self.containsBack = (x, y) => {
        const reportFrame = self.getReportFrame();
        if (!reportFrame) {
            return containsBack.apply(self, [x, y]);
        }
        return containsBack.apply(self, [x, y]) || isPointInRect({x, y}, reportFrame);
    };

    /**
     * 不绘制focusFrame.
     */
    self.drawFocusFrame = () => {
    };

    /**
     * @override
     */
    const drawBorder = self.drawBorder;
    self.drawBorder = () => {
        drawBorder.apply(self);
        if (shape.isFocused || shape.emphasized) {
            self.parent.style.outline = shape.outlineWidth + "px" + " solid " + shape.outlineColor;
        } else {
            self.parent.style.outline = "";
        }
    };

    /**
     * 删除之前清理掉react相关的组件.
     *
     * @override
     */
    const beforeRemove = self.beforeRemove;
    self.beforeRemove = () => {
        beforeRemove.apply(self);
        self.unmountReact();
    };

    /**
     * 监听parent的变化，当发生变化时，需要修改图形的高度和宽度.
     */
    let prevHeight = 0;
    self.observe = () => {
        new ResizeObserver((entries) => {
            if (prevHeight === self.parent.offsetHeight) {
                return;
            }

            // 删除之后不需要修改图形大小.
            if (shape.container === "") {
                return;
            }

            shape.resize(shape.width, self.parent.offsetHeight);
            prevHeight = self.parent.offsetHeight;
            self.panelRef?.current?.setHeight(shape.height);
            generateWaterDrops();
        }).observe(self.parent);
    };

    const generateWaterDrops = () => {
        self.waterDrops = [];
        const width = shape.width;
        const height = shape.height;
        const emphasizedOffset = shape.emphasizedOffset;

        // 上右.
        self.waterDrops.push(waterDrop(HORIZONTAL_RIGHT,
                (x, ctl) => x + ctl.percent * width,
                (y) => y + emphasizedOffset));

        // 下左.
        self.waterDrops.push(waterDrop(HORIZONTAL_LEFT,
                (x, ctl) => x + width - ctl.percent * width,
                (y) => y + height - emphasizedOffset));
        const count = Math.floor(height / WATER_DROP_DISTANCE) + 1;
        for (let i = 0; i < count; i++) {
            // 左上
            self.waterDrops.push(waterDrop(VERTICAL_UP,
                    (x) => x + emphasizedOffset,
                    (y, ctl) => y + height / count * (count - i) - ctl.percent * height / count));

            // 右下.
            self.waterDrops.push(waterDrop(VERTICAL_DOWN, (x) => x + width - emphasizedOffset,
                    (y, ctl) => y + height / count * i + ctl.percent * height / count))
        }
    };

    /**
     * 绘制水滴流转效果.
     *
     * @override
     */
    self.drawDynamic = (context, x, y) => {
        self.waterDrops.forEach(wd => {
            if (wd.loaded) {
                wd.draw(context, x, y, shape)
            } else {
                wd.load();
            }
        })
    };

    return self;
};

const STEP = 0.009

/**
 * 水滴绘制器.
 *
 * @param rawSvg 原始svg字符串.
 * @param getX 获取x的值.
 * @param getY 获取y的值.
 * @return {{}} 对象.
 */
const waterDrop = (rawSvg, getX, getY) => {
    const self = {};
    self.img = null;
    self.control = { percent: 0, times: 0 };

    /**
     * 绘制.
     *
     * @param context canvas2dContext对象.
     * @param x 横坐标.
     * @param y 纵坐标.
     * @param shape 图形对象.
     */
    self.draw = (context, x, y, shape) => {
        self.control.percent += STEP;
        if (self.control.percent >= 1) {
            self.control.times = 100;
            self.control.percent = 0;
            return;
        }
        x -= shape.width / 2;
        y -= shape.height / 2;
        x = getX(x, self.control);
        y = getY(y, self.control);
        context.drawImage(self.img, x, y);
    };

    /**
     * 加载svg为image对象.
     */
    self.load = () => {
        const svg = new Blob([rawSvg], {type:"image/svg+xml;charset=utf-8"});
        const url = URL.createObjectURL(svg);
        const img = new Image();
        img.src = url;
        img.onload = function () {
            URL.revokeObjectURL(url);
            self.img = img;
            self.loaded = true;
        };
    }

    return self;
};