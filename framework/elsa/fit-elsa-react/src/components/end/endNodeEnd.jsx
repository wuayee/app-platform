import {jadeNode} from "@/components/jadeNode.jsx";
import "./style.css";
import {Button} from "antd";
import {DIRECTION} from "@fit-elsa/elsa-core";
import {SECTION_TYPE} from "@/common/Consts.js";
import {endNodeDrawer} from "@/components/end/endNodeDrawer.jsx";

/**
 * 结束节点shape
 *
 @override
 */
export const endNodeEnd = (id, x, y, width, height, parent, drawer) => {
    const self = jadeNode(id, x, y, width, height, parent, drawer);
    self.type = "endNodeEnd";
    self.backColor = 'white';
    self.pointerEvents = "auto";
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
    const getToolMenus = self.getToolMenus;
    self.getToolMenus = () => {
        if (self.page.shapes.filter(s => s.type === self.type).length === 1) {
            return [{
                key: '1', label: "复制", action: () => {
                    self.duplicate();
                }
            }, {
                key: '2', label: "重命名", action: (setEdit) => {
                    setEdit(true);
                }
            }];
        }
        return getToolMenus.apply(self);
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
    self.serializerJadeConfig = () => {
        const jadeConfig = self.getLatestJadeConfig();
        const mode = jadeConfig.inputParams ? "variables" : "manualCheck";
        if (mode === "variables") {
            self.flowMeta.callback.converter.entity = jadeConfig;
            self.flowMeta.task = {};
        } else {
            self.flowMeta.callback.converter.entity = {};
            self.flowMeta.task = jadeConfig;
        }
    };

    /**
     * 获取用户自定义组件.
     *
     * @override
     */
    self.getComponent = () => {
        if (!self.flowMeta.callback.converter.entity) {
            return self.graph.plugins[self.componentName](undefined);
        }
        const jadeConfig = self.flowMeta.callback.converter.entity.inputParams ? self.flowMeta.callback.converter.entity : self.flowMeta.task;
        return self.graph.plugins[self.componentName](jadeConfig);
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
     * @override
     */
    self.getHeaderComponent = (disabled) => {
        return (<EndNodeHeader shape={self} disabled={disabled}/>);
    }

    self.getHeaderIcon = () => {
        return (<>
            <Button disabled={true} className="jade-node-custom-header-icon">
                <EndIcon/>
            </Button>
        </>);
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