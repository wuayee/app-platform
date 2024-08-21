import {CopyPasteHelpers, node} from "@fit-elsa/elsa-core";
import {v4 as uuidv4} from "uuid";
import {NODE_STATUS, SECTION_TYPE, SOURCE_PLATFORM, VIRTUAL_CONTEXT_NODE} from "@/common/Consts.js";
import React from "react";
import {jadeNodeDrawer} from "@/components/base/jadeNodeDrawer.jsx";
import {overrideMethods} from "@/components/base/overrides.js";
import {referenceDecorate} from "@/components/base/references.js";
import {addDetections} from "@/components/base/detections.js";

/**
 * jadeStream中的流程编排节点.
 *
 * @override
 */
export const jadeNode = (id, x, y, width, height, parent, drawer) => {
    const self = node(id, x, y, width, height, parent, false, drawer ? drawer : jadeNodeDrawer);
    self.type = "jadeNode";
    self.serializedFields.batchAdd(
        "toolConfigs",
        "componentName",
        "flowMeta",
        "outlineWidth",
        "outlineColor",
        "sourcePlatform",
        "runnable",
        "disabled"
    );
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
        },
        "joberFilter": {
            "type": "MINIMUM_SIZE_FILTER",
            "threshold": 1
        }
    };
    self.sourcePlatform = SOURCE_PLATFORM.OFFICIAL;
    self.observed = [];
    self.runnable = true;
    self.disabled = false;

    overrideMethods(self);
    referenceDecorate(self);
    addDetections(self);

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
        self.output = {};
        self.input = {};
        if (data.parameters[0]) {
            self.output = JSON.parse(data.parameters[0].output);
            self.input = JSON.parse(data.parameters[0].input);
        }
        self.errorMsg = data.errorMsg;
        self.cost = data.runCost;
    };

    /**
     * 处理传递的元数据
     *
     * @param metaData 元数据信息
     */
    self.processMetaData = (metaData) => {
        if (metaData && metaData.name) {
            self.text = metaData.name;
        }
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
        const preNodeInfos = []; // 存储前方节点信息的数组
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
                // 如果找不到当前形状对象，则返回
                return;
            }

            // 将当前形状的名称和获取到的info添加到formerNodes中
            preNodeInfos.push({
                id: currentShapeId,
                node: currentShape,
                name: currentShape.text,
                runnable: currentShape.runnable,
                observableList: currentShape.page.getObservableList(currentShapeId)
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
        preNodeInfos.push({
            id: VIRTUAL_CONTEXT_NODE.id,
            name: VIRTUAL_CONTEXT_NODE.name,
            node: {runnable: true},
            runnable: true,
            observableList: self.page.getObservableList(VIRTUAL_CONTEXT_NODE.id)
        });
        preNodeInfos.shift();
        return preNodeInfos;
    };

    /**
     * 获取可引用的前置节点信息列表.
     *
     * @return {*[]} 前置节点信息列表.
     */
    self.getPreReferenceNodeInfos = () => {
        return self.getPreNodeInfos().filter(s => s.runnable === self.runnable);
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
     * 复制节点.
     */
    self.duplicate = () => {
        const shapes = JSON.stringify([self.serialize()]);
        CopyPasteHelpers.pasteShapes(shapes, "", self.page);
    };

    /**
     * 序列化jade配置.
     *
     * @param jadeConfig 配置.
     */
    self.serializerJadeConfig = (jadeConfig) => {
        self.flowMeta.jober.converter.entity = jadeConfig;
    };

    /*
     * 更新粘贴出来的图形的id
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
     * 断开连接.
     */
    self.offConnect = () => {
        const preNodes = self.getPreNodeInfos();
        const visited = new Set();
        visitNext(self, visited, preNodes, (n, pNodes) => {
            n.observed.filter(o => !n.isReferenceAvailable(pNodes, o))
                .forEach(o => o.disable());
        });
    };

    /**
     * 连接.
     */
    self.onConnect = () => {
        const preNodes = self.getPreNodeInfos();
        const visited = new Set();
        visitNext(self, visited, preNodes, (n, pNodes) => {
            n.observed.filter(o => n.isReferenceAvailable(pNodes, o)).forEach(o => o.enable());
        });
    };

    // 访问后续节点.
    const visitNext = (node, visited, preNodes, action) => {
        if (visited.has(node)) {
            return;
        }
        action(node, preNodes);
        visited.add(node);
        preNodes.push({id: node.id, node: node, runnable: node.runnable});
        node.getNextNodes().forEach(n => visitNext(n, visited, preNodes, action));
        preNodes.pop();
    };

    /**
     * 获取下一批节点.
     */
    self.getNextNodes = () => {
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
                const preNodeInfos = self.getPreReferenceNodeInfos();
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

    /**
     * 设置元数据.
     *
     * @param flowMeta 元数据信息.
     */
    self.setFlowMeta = (flowMeta) => {
        self.drawer.dispatch({
            type: "system_update",
            changes: [{key: "inputParams", value: flowMeta?.jober?.converter?.entity?.inputParams}]
        });
    };

    /**
     * 获取flowMeta.
     *
     * @return {*} 元数据.
     */
    self.getFlowMeta = () => {
        const jadeConfig = self.drawer.getLatestJadeConfig();
        jadeConfig && self.serializerJadeConfig(jadeConfig);
        return self.flowMeta;
    };

    return self;
};