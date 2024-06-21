import {jadeFlowGraph} from "./jadeFlowGraph.js";
import {NODE_STATUS} from "@/common/Consts.js";

/**
 * react流程代码，对外暴露接口，以便对流程进行操作以及获取数据.
 */
const jadeFlowAgent = (graph) => {
    const self = {};
    self.graph = graph;

    /**
     * 添加图形.
     *
     * @param shapeType 图形类型.
     * @param properties 初始化属性.
     */
    self.want = (shapeType, properties) => {
        graph.activePage.want(shapeType, properties);
    };

    /**
     * 导入用户自定义组件.
     *
     * @param importStatement import语句.
     * @return {*}
     */
    self.import = (importStatement) => {
        return self.graph.staticImport(importStatement);
    };

    /**
     * 序列化数据.
     *
     * @returns {*} 流程的全量序列化数据.
     */
    self.serialize = () => {
        graph.activePage.serialize();
        return graph.serialize();
    };

    /**
     * 获取流程试运行入参元数据
     */
    self.getFlowRunInputMetaData = () => {
        return graph.activePage.shapes.find(s => s.type === "startNodeStart").getRunInputParams();
    };

    /**
     * 运行流程.
     *
     * @return {{stop: stop, refresh: refresh, reset: reset}} 通过refresh刷新流程状态，通过reset重置流程，通过stop结束流程.
     */
    self.run = () => {
        const nodes = graph.activePage.shapes.filter(s => s.isTypeof("jadeNode"));
        nodes.forEach(n => {
            n.setRunStatus(NODE_STATUS.UN_RUNNING);
            n.moveable = false;
            n.drawer.setDisabled(true);
        });
        return {
            // 刷新流程节点状态.
            refresh: (dataList) => {
                nodes.forEach(node => {
                    const data = dataList.find(d => d.nodeId === node.id);
                    if (data) {
                        node.setRunStatus(data.status);
                        node.setRunReportSections(data);
                    } else {
                        const preNodes = node.getDirectPreNodeIds();
                        if (preNodes.every(preNode => _isPreNodeFinished(preNode, dataList))) {
                            node.setRunStatus(NODE_STATUS.RUNNING);
                        }
                    }
                });
            },
            // 重置流程节点装填.
            reset: () => {
                nodes.forEach(n => {
                    n.setRunStatus(NODE_STATUS.DEFAULT);
                    n.moveable = true;
                    n.drawer.setDisabled(false);
                    delete n.output;
                    delete n.input;
                    delete n.cost;
                });
            },
            // 结束运行.
            stop: () => {
                nodes.forEach(n => {
                    n.moveable = true;
                    n.emphasized = false;
                    n.drawer.setDisabled(false);
                });
            }
        };
    };

    const _isPreNodeFinished = (preNode, dataList) => {
        if (preNode.type === "conditionNodeCondition") {
            return false;
        }
        const data = dataList.find(d => d.nodeId === preNode.id);
        return data && data.status === NODE_STATUS.SUCCESS;
    };

    /**
     * 获取可创建的节点列表.
     */
    self.getAvailableNodes = () => {
        return [
            {type: "retrievalNodeState", name: "数据检索"},
            {type: "llmNodeState", name: "大模型"},
            {type: "manualCheckNodeState", name: "人工检查"},
            {type: "fitInvokeNodeState", name: "FIT调用"}
        ];
    };

    /**
     * 创建节点.
     *
     * @param type 节点类型.
     * @param e 鼠标事件.
     * @param metaData 元数据
     */
    self.createNode = (type, e, metaData) => {
        console.log("call createNode...");
        const position = graph.activePage.calculatePosition(e);
        const shape = graph.activePage.createNew(type, position.x, position.y);
        shape.processMetaData(metaData);
    };

    /**
     * 创建工具节点
     *
     * @param e 鼠标事件.
     * @param schemaData schema元数据
     */
    self.createTool = (e, schemaData) => {
        const position = graph.activePage.calculatePosition(e);
        self.createToolByPosition(position, schemaData);
    };

    /**
     * 创建节点.
     *
     * @param type 节点类型.
     * @param position 坐标.
     * @param metaData 元数据
     */
    self.createNodeByPosition = (type, position, metaData) => {
        console.log("call createNodeByPosition...");
        const shape = graph.activePage.createNew(type, position.x, position.y);
        shape.processMetaData(metaData);
    };

    /**
     * 创建工具节点
     *
     * @param position 坐标.
     * @param schemaData schema元数据
     */
    self.createToolByPosition = (position, schemaData) => {
        const shape = graph.activePage.createNew("toolInvokeNodeState", position.x, position.y);
        shape.processMetaData(schemaData);
    };

    /**
     * 画布发生变化时触发.
     *
     * @param callback 回调函数.
     */
    self.onChange = (callback) => {
        graph.onChangeCallback = callback;
    };

    /**
     * 获取所有节点的配置数据.
     * 返回格式: {
     *     "nodeId": [] // 这里的数组是每个节点的配置数据.
     * }
     *
     * @return {*} 返回配置数据.
     */
    self.getNodeConfigs = () => {
        return graph.activePage.shapes.filter(s => s.isTypeof("jadeNode")).map(s => {
            return {
                [s.id]: s.getLatestJadeConfig()
            };
        });
    };

    /**
     * 校验所有节点数据是否合法.
     *
     * @return Promise 校验结果
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

    /**
     * 当需要触发模型选择时的回调.
     *
     * @param callback 回调函数.
     */
    self.onModelSelect = (callback) => {
        graph.activePage.addEventListener("SELECT_MODEL", (onModelSelected) => {
            callback(onModelSelected);
        });
    };

    return self;
};

/**
 * Aipp流程对外接口.
 */
export const JadeFlow = (() => {
    const self = {};

    /**
     * 新建流程.
     *
     * @param div 待渲染的dom元素.
     * @param configs 传入的其他参数列表.
     */
    self.new = async (div, configs) => {
        const g = jadeFlowGraph(div, "jadeFlow");
        g.configs = configs;
        g.collaboration.mute = true;
        await g.initialize();
        const page = g.addPage("newFlowPage");

        // 新建的默认创建出start、end和一个连线
        const start = page.createShape("startNodeStart", 100, 100);
        const end = page.createShape("endNodeEnd", start.x + start.width + 200, 100);
        const jadeEvent = page.createNew("jadeEvent", 0, 0);
        page.reset();

        // reset完成之后进行connect操作.
        jadeEvent.connect(start.id, "E", end.id, "W");

        page.fillScreen();
        return jadeFlowAgent(g);
    };

    /**
     * 编辑流程.
     *
     * @param div 待渲染的dom元素.
     * @param flowConfigData 流程元数据.
     * @param configs 传入的其他参数列表.
     * @param importStatements 传入的需要加载的语句.
     */
    self.edit = async (div, flowConfigData, configs, importStatements = []) => {
        const g = jadeFlowGraph(div, "jadeFlow");
        g.configs = configs;
        g.collaboration.mute = true;
        for (let i = 0; i < importStatements.length; i++) {
            await g.dynamicImportStatement(importStatements[i]);
        }
        await g.initialize();
        g.deSerialize(flowConfigData);
        const pageData = g.getPageData(0);
        await g.edit(0, div, pageData.id);
        return jadeFlowAgent(g);
    };

    return self;
})();