import {jadeNode} from "@/components/jadeNode.jsx";
import "./style.css";
import {codeComponent} from "@/components/code/codeComponent.jsx";
import {SECTION_TYPE} from "@/common/Consts.js";
import {codeNodeDrawer} from "@/components/code/CodeNodeDrawer.jsx";

/**
 * 代码节点shape
 *
 * @override
 */
export const codeNodeState = (id, x, y, width, height, parent, drawer) => {
    const self = jadeNode(id, x, y, width, height, parent, drawer ? drawer : codeNodeDrawer);
    self.type = "codeNodeState";
    self.width = 360;
    self.componentName = "codeComponent";
    self.text = "code"
    self.width = 368;
    self.flowMeta.jober.type = 'STORE_JOBER';
    const toolEntity = {
        uniqueName: "",
        params: [],
        return: {
            type: ""
        }
    };
    const template = {
        inputParams: [],
        outputParams: []
    };

    /**
     * @override
     */
    const serializerJadeConfig = self.serializerJadeConfig;
    self.serializerJadeConfig = (jadeConfig) => {
        serializerJadeConfig.apply(self, [jadeConfig]);
        const newConfig = {...template};
        newConfig.outputParams = self.flowMeta.jober.converter.entity.outputParams;
        newConfig.inputParams = self.flowMeta.jober.converter.entity.inputParams;
        self.flowMeta.jober.converter.entity = newConfig;
        self.flowMeta.jober.entity.return.type = "object";
        self.flowMeta.jober.entity.params = self.flowMeta.jober.converter.entity.inputParams.map(property => {
            return {name: property.name}
        });
    };

    /**
     * @override
     */
    self.processMetaData = (metaData) => {
        self.flowMeta.jober.entity = toolEntity;
        self.flowMeta.jober.entity.uniqueName = metaData.uniqueName;
    };

    /**
     * 获取code节点测试报告章节
     */
    self.getRunReportSections = () => {
        // 这里的data是每个节点的每个章节需要展示的数据，比如工具节点展示为输入、输出的数据
        return [{
            no: "1",
            name: "输入",
            type: SECTION_TYPE.DEFAULT,
            data: self.input ? self.input.args : {}
        }, {
            no: "2",
            name: "输出",
            type: SECTION_TYPE.DEFAULT,
            data: self.getOutputData(self.output)
        }];
    };

    return self;
}