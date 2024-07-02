import {CopyPasteHelpers, DIRECTION, node} from "@fit-elsa/elsa-core";
import {v4 as uuidv4} from "uuid";
import {CONNECTOR, NODE_STATUS, SECTION_TYPE, SOURCE_PLATFORM, VIRTUAL_CONTEXT_NODE} from "@/common/Consts.js";
import React from "react";
import {jadeNodeDrawer} from "@/components/jadeNodeDrawer.jsx";

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
    self.sourcePlatform = SOURCE_PLATFORM.OFFICIAL;
    self.observed = [];

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
    };

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
            connector.radius = CONNECTOR.RADIUS;
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
        const observerProxy = ObserverProxy(nodeId, observableId, observer, self);
        observerProxy.status = preNodeIdSet.has(nodeId) ? "enable" : "disable";
        self.observed.push(observerProxy);
        self.page.observeTo(nodeId, observableId, observerProxy);

        // 监听时，主动推送一次数据.
        const observable = self.page.getObservable(nodeId, observableId);
        if (observable.value || observable.type) {
            observerProxy.observe({value: observable.value, type: observable.type});
        }

        // 返回取消监听的方法.
        return () => {
            observerProxy.stopObserve();
        };
    };

    /**
     * 断开连接.
     */
    self.offConnect = () => {
        const preNodeInfos = self.getPreNodeInfos();
        const preNodeIdSet = new Set(preNodeInfos.map(n => n.id));
        self.observed.filter(o => !preNodeIdSet.has(o.nodeId)).forEach(o => o.disable());
        const nextNodes = getNextNodes();
        nextNodes.length > 0 && nextNodes.forEach(n => n.offConnect());
    };

    /**
     * 连接.
     */
    self.onConnect = () => {
        const preNodeInfos = self.getPreNodeInfos();
        const preNodeIdSet = new Set(preNodeInfos.map(n => n.id));
        self.observed.filter(o => preNodeIdSet.has(o.nodeId)).forEach(o => o.enable());
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
        self.observed.forEach(o => self.page.stopObserving(o.nodeId, o.observableId, o));
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
                self.observed.forEach(o => {
                    const node = self.page.getShapeById(o.nodeId);
                    if (!node && VIRTUAL_CONTEXT_NODE.id !== o.nodeId) {
                        throw new Error("节点[" + o.nodeId + "]不存在.");
                    }
                    if (!preNodeIdSet.has(o.nodeId)) {
                        throw new Error("节点[" + node.text + "]和节点[" + self.text + "]未连接.");
                    }
                });

                // 调用 form 本身的校验能力，校验所有字段
                self.validateForm().then(resolve).catch(reject);
            } catch (error) {
                reject({errorFields: [{errors: [error.message], name: "node-error"}]});
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
 * @param shape 图形.
 * @return {{}}
 * @constructor
 */
const ObserverProxy = (nodeId, observableId, observer, shape) => {
    const self = {};
    self.nodeId = nodeId;
    self.observableId = observableId;
    self.status = "enable";
    self.origin = observer;

    /**
     * 禁用Observer.
     */
    self.disable = () => {
        if (self.status === "disable") {
            return;
        }
        self.observe({value: null, type: null});
        self.status = "disable";
    };

    /**
     * 启动Observer.
     */
    self.enable = () => {
        if (self.status === "enable") {
            return;
        }
        self.status = "enable";
        const observable = shape.page.getObservable(self.nodeId, self.observableId);
        self.observe({value: observable.value, type: observable.type});
    };

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

    /**
     * 停止观察.
     * 删除shape中的observed，删除page中的监听.
     */
    self.stopObserve = () => {
        // stopObserve时，先恢复到空值，再停止监听。防止直接调用stopObserve时，监听方虽然已不再监听，但值并没有变化的问题。
        self.observe({value: null, type: null});
        const index = shape.observed.findIndex(o => o === self);
        shape.observed.splice(index, 1);
        shape.page.stopObserving(nodeId, observableId, self);
    };

    return self;
};