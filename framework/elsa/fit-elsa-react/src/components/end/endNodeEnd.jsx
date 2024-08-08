import {jadeNode} from "@/components/jadeNode.jsx";
import "./style.css";
import {DIRECTION} from "@fit-elsa/elsa-core";
import {SECTION_TYPE} from "@/common/Consts.js";
import {endNodeDrawer} from "@/components/end/endNodeDrawer.jsx";

/**
 * 结束节点shape
 *
 @override
 */
export const endNodeEnd = (id, x, y, width, height, parent, drawer) => {
    const self = jadeNode(id, x, y, width, height, parent, drawer ? drawer : endNodeDrawer);
    self.type = "endNodeEnd";
    self.text = "结束";
    self.componentName = "endComponent";
    self.flowMeta = {
        "triggerMode": "auto",
        "callback": {
            "type": "general_callback",
            "name": "通知回调",
            "fitables": ["com.huawei.fit.jober.aipp.fitable.AippFlowEndCallback"],
            "converter": {
                "type": "mapping_converter"
            },
        }
    };

    /**
     * @override
     */
    const remove = self.remove;
    self.remove = (source) => {
        // 保证页面最少一个结束节点
        let beforeCount = self.page.shapes.filter(s => s.type === "endNodeEnd").length;
        if (beforeCount <= 1 && self.type === "endNodeEnd") {
            return [];
        }
        const removed = remove.apply(self, [source]);
        const curCount = self.page.shapes.filter(s => s.type === "endNodeEnd").length;
        // 当从两个结束节点删除为一个的时候，需要通知最后一个结束节点刷新
        if (curCount === 1) {
            self.page.triggerEvent({
                type: "TOOL_MENU_CHANGE",
                value: [1]
            });
        }
        return removed;
    };

    /**
     * 设置E方向没有连接点
     *
     * @override
     */
    const initConnectors = self.initConnectors;
    self.initConnectors = () => {
        initConnectors.apply(self);
        self.connectors.remove(c => c.direction.key === DIRECTION.E.key);
    };

    /**
     * 序列化组件信息
     *
     * @override
     */
    self.serializerJadeConfig = (jadeConfig) => {
        self.flowMeta.callback.converter.entity = jadeConfig;
    };

    /**
     * 获取用户自定义组件.
     *
     * @override
     */
    self.getComponent = () => {
        return self.graph.plugins[self.componentName](self.flowMeta.callback.converter.entity);
    };

    /**
     * 获取组件自定义entity对象
     *
     * @override
     */
    self.getEntity = () => {
        return self.flowMeta.callback.converter.entity;
    };

    /**
     * 结束节点的测试报告章节
     */
    self.getRunReportSections = () => {
        return [{
            no: "1",
            name: "输出",
            type: SECTION_TYPE.DEFAULT,
            data: self.getOutputData(self.input)
        }];
    };

    /**
     * @override
     */
    const created = self.created;
    self.created = () => {
        created.apply(self);
        const endNodes = self.page.shapes.filter(s => s.type === self.type);
        // 当从一个结束节点变为两个结束节点的时候，需要通知结束节点的header刷新
        if (endNodes.length === 2) {
            self.page.triggerEvent({
                type: "TOOL_MENU_CHANGE",
                value: [self.page.shapes.filter(s => s.type === "endNodeEnd").length]
            });
        }
    };

    return self;
}