import {jadeNode} from "@/components/jadeNode.jsx";
import {DIRECTION} from "@fit-elsa/elsa-core";
import "./style.css";
import {SECTION_TYPE} from "@/common/Consts.js";
import {startNodeDrawer} from "@/components/start/startNodeDrawer.jsx";

/**
 * jadeStream中的流程启动节点.
 *
 * @override
 */
export const startNodeStart = (id, x, y, width, height, parent, drawer) => {
    const self = jadeNode(id, x, y, width, height, parent, drawer ? drawer : startNodeDrawer);
    self.type = "startNodeStart";
    self.text = "开始";
    self.pointerEvents = "auto";
    self.componentName = "startComponent";
    self.deletable = false;
    self.isUnique = true;
    delete self.flowMeta.jober;

    /**
     * 设置方向为W方向不出现连接点
     *
     * @override
     */
    const initConnectors = self.initConnectors;
    self.initConnectors = () => {
        initConnectors.apply(self);
        self.connectors.remove(c => c.direction.key === DIRECTION.W.key);
    };

    /**
     * 获取用户自定义组件.
     *
     * @return {*}
     */
    self.getComponent = () => {
        return self.graph.plugins[self.componentName](self.flowMeta.inputParams);
    };

    /**
     * @override
     */
    self.serializerJadeConfig = () => {
        self.flowMeta.inputParams = self.getLatestJadeConfig();
    };

    /**
     * 获取试运行入参
     */
    self.getRunInputParams = () => {
        return self.getLatestJadeConfig().find(config => config.name === "input").value;
    };

    /**
     * 开始节点的测试报告章节
     */
    self.getRunReportSections = () => {
        return [{
            no: "1",
            name: "输入",
            type: SECTION_TYPE.DEFAULT,
            data: self.getOutputData(self.input)
        }];
    };

    return self;
};