import {page, sleep, uuid} from "@fit-elsa/elsa-core";
import {NODE_STATUS, VIRTUAL_CONTEXT_NODE} from "@/common/Consts.js";

/**
 * jadeFlow的page.
 *
 * @param div dom元素.
 * @param graph 画布.
 * @param name 名称.
 * @param id 唯一标识.
 * @returns {(WorkerGlobalScope & Window) | Window}
 */
export const jadeFlowPage = (div, graph, name, id) => {
    const self = page(div, graph, name, id);
    self.type = "jadeFlowPage";
    self.serializedFields.batchAdd("x", "y", "scaleX", "scaleY");
    self.namespace = "jadeFlow";
    self.backgroundGrid = "point";
    self.backgroundGridSize = 16;
    self.backgroundGridMargin = 16;
    self.backColor = "#fbfbfc";
    self.focusBackColor = "#fbfbfc";
    self.gridColor = "#e1e1e3";
    self.disableContextMenu = true;
    self.moveAble = true;
    self.observableStore = ObservableStore();

    /**
     * @override
     */
    const onLoaded = self.onLoaded;
    self.onLoaded = () => {
        onLoaded.apply(self);
        self.shapes.forEach(s => s.onPageLoaded && s.onPageLoaded());

        const registerVirtualNodeInfo = () => {
            const virtualNodeInfoList = [
                {observableId: "instanceId", value: "instanceId", type: "String"},
                {observableId: "appId", value: "appId", type: "String"},
                {observableId: "memories", value: "memories", type: "Array"},
                {observableId: "useMemory", value: "useMemory", type: "Boolean"},
                {observableId: "userId", value: "userId", type: "String"},
                {observableId: "fileUrl", value: "fileUrl", type: "String"},
                {observableId: "chatId", value: "chatId", type: "String"},
            ];

            virtualNodeInfoList.forEach(({observableId, value, type}) => {
                self.registerObservable({
                    nodeId: VIRTUAL_CONTEXT_NODE.id,
                    observableId,
                    value,
                    type,
                    parentId: undefined,
                });
            });
        };
        // 上下文虚拟节点信息注册
        registerVirtualNodeInfo();
    };

    /**
     * 具有唯一性的图形以及线都无法拷贝.
     *
     * @override
     */
    const onCopy = self.onCopy;
    self.onCopy = (shapes) => {
        const copiableShapes = shapes.filter(s => !s.isUnique && !s.isTypeof("jadeEvent"));
        return onCopy.apply(self, [copiableShapes]);
    };

    /**
     * 注册可被监听的id.
     *
     * @param props 相关属性.
     */
    self.registerObservable = (props) => {
        self.observableStore.add(props);
    };

    /**
     * 删除可被监听的id.若不传observableId，则删除该节点所有可被监听的id.
     *
     * @param nodeId 节点id.
     * @param observableId 可被监听的id.
     */
    self.removeObservable = (nodeId, observableId = null) => {
        self.observableStore.remove(nodeId, observableId);
    };

    /**
     * 获取可被监听的树装列表.
     *
     * @param nodeId 节点id.
     * @return {*[]}
     */
    self.getObservableList = (nodeId) => {
        return self.observableStore.getObservableList(nodeId);
    };

    /**
     * 监听某个observable.
     *
     * @param nodeId 被监听节点的id.
     * @param observableId 待监听的id.
     * @param observer 监听器.
     */
    self.observeTo = (nodeId, observableId, observer) => {
        self.observableStore.addObserver(nodeId, observableId, observer);
    };

    /**
     * 停止监听某个observable.
     *
     * @param nodeId 被监听节点的id.
     * @param observableId 待监听的id.
     * @param observer 监听器.
     */
    self.stopObserving = (nodeId, observableId, observer) => {
        self.observableStore.removeObserver(nodeId, observableId, observer)
    };

    /**
     * 获取observable.
     *
     * @param nodeId 被监听节点的id.
     * @param observableId 待监听的id.
     * @return {*|null}
     */
    self.getObservable = (nodeId, observableId) => {
        return self.observableStore.getObservable(nodeId, observableId);
    };

    /**
     * 清空时，同时清空observableStore.
     *
     * @override
     */
    const clear = self.clear;
    self.clear = () => {
        clear.apply(self);
        self.observableStore.clear();
    };

    const initJadeId = (id) => {
        if (id === null || id === undefined || id === "") {
            // 变量为空，使用 uuid 创建
            id = "jade" + uuid();
        } else if (!id.startsWith("jade")) {
            // 如果 id 不以 "jade" 开头，则在前面添加 "jade"
            id = "jade" + id;
        }
        return id;
    };

    /**
     * 添加对图形创建的前后处理.
     *
     * @override
     */
    const createNew = self.createNew;
    self.createNew = (shapeType, x, y, id, properties, parent, ignoreLimit, data, metaData) => {
        const handleArgs = (args) => args.map(arg => arg === undefined ? null : arg);

        shapeCreationHandler.filter(v => v.type === "before")
                .forEach(v => {
                    const [type, posX, posY, props, prnt] = handleArgs([shapeType, x, y, properties, parent]);
                    v.handle(self, type, posX, posY, props, prnt);
                });
        const args = handleArgs([shapeType, x, y, id, properties, parent, ignoreLimit, data]);
        const shape = createNew.apply(self, args);
        shape.processMetaData && shape.processMetaData(metaData);
        shapeCreationHandler.filter(v => v.type === "after")
            .forEach(v => v.handle(self, shape));
        return shape;
    }

    /**
     * 生成节点的名称
     *
     * @param text 节点text
     * @param type 节点类型
     */
    self.generateNodeName = (text, type) => {
        const jadeNodes = self.shapes.filter(s => s.isTypeof("jadeNode"));
        // 找到所有节点text
        const textArray = jadeNodes.map(s => s.text);
        if (textArray.filter(t => t === text).length <= 1) {
            return text;
        }
        const separator = "_";
        if (jadeNodes.filter(s => s.type === type).length <= 1) {
            return text;
        }
        let index = 1;
        while (true) {
            // 不带下划线，直接拼接_1
            const lastSeparatorIndex = text.lastIndexOf(separator);
            const last = text.substring(lastSeparatorIndex + 1, text.length);
            // 如果是数字，把数字+1  如果不是数字，拼接_1
            if (lastSeparatorIndex !== -1 && !isNaN(parseInt(last))) {
                text = text.substring(0, lastSeparatorIndex) + separator + index;
            } else {
                text = text + separator + index;
            }
            if (!textArray.includes(text)) {
                return text;
            }
            index++;
        }
    };

    /**
     * 注册对图形创建的前后处理.
     *
     * @param handler 处理器.
     */
    const shapeCreationHandler = [];
    self.registerShapeCreationHandler = (handler) => {
        shapeCreationHandler.push(handler);
    };

    self.getMenuScript = () => [];

    // 注册处理器，一次只能有一个开始和结束节点.
    self.registerShapeCreationHandler({
        type: "before",
        handle: (page, shapeType) => {
            if (shapeType === "startNodeStart") {
                if (page.shapes.find(s => s.type === shapeType)) {
                    throw new Error("最多只能有一个开始或结束节点.");
                }
            }
        }
    });

    self.registerShapeCreationHandler({
        type: "after",
        handle: (page, shape) => {
            shape.text = self.generateNodeName(shape.text, shape.type);
        }
    });

    self.registerShapeCreationHandler({
        type: "after",
        handle: (page, shape) => {
            if (shape.type !== "jadeEvent") {
                shape.id = initJadeId(shape.id);
            }
        }
    });

    /**
     * 等待所有图形绘制完成.
     *
     * @return {Promise<void>} promise.
     */
    self.awaitShapesRendered = async () => {
        const shapeRenderedArray = self.shapes.filter(s => s.isTypeof("jadeNode")).map(s => {
            return {
                id: s.id,
                rendered: false
            };
        });
        const listener = (e) => shapeRenderedArray.find(s => s.id === e.id).rendered = true;
        self.addEventListener("shape_rendered", listener);
        while (!shapeRenderedArray.every(s => s.rendered)) {
            await sleep(50);
        }
        self.removeEventListener("shape_rendered", listener);
    };

    /**
     * 重置当前页中节点状态
     *
     * @param nodes 节点
     */
    self.resetRunStatus = nodes => {
        nodes.forEach(n => {
            n.setStatus({runStatus: NODE_STATUS.DEFAULT, disabled: false});
            n.moveable = true;
            delete n.output;
            delete n.input;
            delete n.cost;
        });
        graph.activePage.isRunning = false;
    };

    /**
     * 返回停止运行流程测试方法
     *
     * @return 停止运行流程测试方法
     */
    self.stopRun = nodes => {``
        nodes.forEach(n => {
            // 修改属性会导致dirties事件，并且dirties事件是异步的，因此在触发时，isRunning已是false状态.
            // 所以这里需要使用ignoreChange使其不触发dirties事件.
            n.ignoreChange(() => {
                n.moveable = true;
                n.emphasized = false;
            });
            n.setStatus({disabled: false});
        });
        graph.activePage.isRunning = false;
    };

    /**
     * 判断引用是否处于disabled状态.
     *
     * @param shapeStatus 图形的状态集合.
     * @return {*} true/false.
     */
    self.isShapeReferenceDisabled = (shapeStatus) => {
        return shapeStatus.disabled;
    };

    /**
     * 节点是否可修改.
     *
     * @param shape 图形对象.
     * @return {*|boolean} true/false.
     */
    self.isShapeModifiable = (shape) => {
        return shape.runnable !== false;
    };

    /**
     * 校验流程.
     *
     * @return {Promise<void>} Promise 校验结果
     */
    self.validate = async () => {
        const nodes = graph.activePage.shapes.filter(s => s.isTypeof("jadeNode"));
        if (nodes.length < 3) {
            return Promise.reject("流程校验失败，至少需要三个节点");
        }
        const validationPromises = nodes.map(s => s.validate().catch(error => error));
        const results = await Promise.all(validationPromises);
        // 获取所有校验失败的信息
        const errors = results.filter(result => result.errorFields);
        if (errors.length > 0) {
            return Promise.reject(errors);
        }
        // 可选：.then()中可以获取校验的所有节点信息 Promise.resolve(results.filter(result => !errors.includes(result)))
        return Promise.resolve();
    };

    return self;
};

/**
 * 存储Observable.
 *
 * @return {{}}
 * @constructor
 */
const ObservableStore = () => {
    const self = {};
    self.store = new Map();

    /**
     * 添加.
     *
     * @param props 相关属性.
     */
    self.add = (props) => {
        const {nodeId, observableId, value, type, parentId, selectable} = props;
        const observableMap = getOrCreate(self.store, nodeId, () => new Map());
        const observable = getOrCreate(observableMap, observableId, () => {
            return {
                observableId, value: null, type: null, observers: [], parentId, selectable: selectable
            }
        });
        observable.value = value;
        observable.type = type;
        observable.parentId = parentId;
        observable.selectable = selectable;
        if (observable.observers.length > 0) {
            observable.observers.forEach(observe => observe.observe({value: value, type: type}));
        }
    };

    /**
     * 删除.
     *
     * @param nodeId 节点id.
     * @param observableId 可被监听的id.
     */
    self.remove = (nodeId, observableId = null) => {
        if (observableId) {
            const observableMap = self.store.get(nodeId);
            if (observableMap) {
                if (observableId) {
                    const observable = observableMap.get(observableId);
                    observableMap.delete(observableId);
                    observable && observable.observers.forEach(o => o.stopObserve());
                    if (observableMap.size === 0) {
                        self.store.delete(nodeId);
                    }
                }
            }
        } else {
            self.store.delete(nodeId);
        }
    };

    /**
     * 添加监听器.
     *
     * @param nodeId 被监听节点的id.
     * @param observableId 待监听的id.
     * @param observer 监听器.
     */
    self.addObserver = (nodeId, observableId, observer) => {
        const observableMap = getOrCreate(self.store, nodeId, () => new Map());
        const observable = getOrCreate(observableMap, observableId, () => {
            return {
                observableId, value: null, observers: []
            }
        });
        observable.observers.push(observer);
    };

    /**
     * 删除监听器.
     *
     * @param nodeId 被监听节点的id.
     * @param observableId 待监听的id.
     * @param observer 监听器.
     */
    self.removeObserver = (nodeId, observableId, observer) => {
        const observableMap = self.store.get(nodeId);
        if (!observableMap) {
            return;
        }
        const observable = observableMap.get(observableId);
        if (!observable) {
            return
        }
        const index = observable.observers.findIndex(o => o === observer);
        if (index !== -1) {
            observable.observers.splice(index, 1);
        }
    };

    const getOrCreate = (map, key, supplier) => {
        let value = map.get(key);
        if (!value) {
            value = supplier();
            map.set(key, value);
        }
        return value;
    };

    /**
     * 获取可被监听的列表.
     *
     * @param nodeId 节点id.
     * @return {*[]}
     */
    self.getObservableList = (nodeId) => {
        const observableMap = self.store.get(nodeId);
        if (!observableMap) {
            return [];
        }

        return Array.from(observableMap.values()).map(o => {
            return {
                nodeId,
                observableId: o.observableId,
                parentId: o.parentId,
                value: o.value,
                type: o.type,
                selectable: o.selectable
            };
        });
    };

    /**
     * 获取单个observable.
     *
     * @param nodeId 被监听节点的id.
     * @param observableId 待监听的id.
     * @return {*|null}
     */
    self.getObservable = (nodeId, observableId) => {
        const observableMap = self.store.get(nodeId);
        return observableMap ? observableMap.get(observableId) : null;
    };

    /**
     * 清空.
     */
    self.clear = () => {
        self.store.clear();
    };

    return self;
};