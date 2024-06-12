import {page, uuid} from "@fit-elsa/elsa-core";
import {VIRTUAL_CONTEXT_NODE} from "@/common/Consts.js";

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
            self.registerObservable(VIRTUAL_CONTEXT_NODE.id, "instanceId", "instanceId", "String", undefined);
            self.registerObservable(VIRTUAL_CONTEXT_NODE.id, "appId", "appId", "String", undefined);
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
     * @param nodeId 节点id.
     * @param observableId 可被监听的id.
     * @param value 当前的值.
     * @param type 值的类型.
     * @param parentId 父组件id.
     */
    self.registerObservable = (nodeId, observableId, value, type, parentId) => {
        self.observableStore.add(nodeId, observableId, value, type, parentId);
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
    self.createNew = (shapeType, x, y, id, properties, parent, ignoreLimit, data) => {
        shapeCreationHandler.filter(v => v.type === "before")
                .forEach(v => v.handle(self, shapeType, x, y, properties, parent));
        const shape = createNew.apply(self, [shapeType, x, y, id, properties, parent, ignoreLimit, data]);
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
        if (!textArray.find(t => t === text)) {
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
     * @param nodeId 节点id.
     * @param observableId 可被监听的id.
     * @param value 当前的值.
     * @param type 值的类型.
     * @param parentId 父组件id.
     */
    self.add = (nodeId, observableId, value, type, parentId) => {
        const observableMap = getOrCreate(self.store, nodeId, () => new Map());
        const observable = getOrCreate(observableMap, observableId, () => {
            return {
                observableId, value: null, type: null, observers: [], parentId
            }
        });
        observable.value = value;
        observable.type = type;
        observable.parentId = parentId;
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
                    observableMap.delete(observableId);
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
            return {nodeId, observableId: o.observableId, parentId: o.parentId, value: o.value, type: o.type};
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