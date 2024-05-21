import {jadeFlowGraph} from "./jadeFlowGraph.js";

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
        // todo 不需要重新添加页面
        // g.addPage("newFlowPage");
        g.deSerialize(flowConfigData);
        const pageData = g.getPageData(0);
        await g.edit(0, div, pageData.id);
        return jadeFlowAgent(g);
    };

    return self;
})();