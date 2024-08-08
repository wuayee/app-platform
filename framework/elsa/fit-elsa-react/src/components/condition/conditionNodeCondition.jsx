import {jadeNode} from "@/components/jadeNode.jsx";
import {DIRECTION} from "@fit-elsa/elsa-core";
import {SECTION_TYPE, VIRTUAL_CONTEXT_NODE} from "@/common/Consts.js";
import {conditionNodeDrawer} from "@/components/condition/conditionNodeDrawer.jsx";

/**
 * jadeStream中的条件节点.
 *
 * @override
 */
export const conditionNodeCondition = (id, x, y, width, height, parent, drawer) => {
    const self = jadeNode(id, x, y, width, height, parent, drawer ? drawer : conditionNodeDrawer);
    self.type = "conditionNodeCondition";
    self.text = "条件";
    self.width = 600;
    self.pointerEvents = "auto";
    self.componentName = "conditionComponent";
    delete self.flowMeta.jober;

    /**
     * 去除方向为E的连接点.
     *
     * @override
     */
    const initConnectors = self.initConnectors;
    self.initConnectors = () => {
        initConnectors.apply(self);
        self.connectors.remove(c => c.direction.key === DIRECTION.E.key);
    };

    /**
     * 获取用户自定义组件.
     *
     * @return {*}
     */
    self.getComponent = () => {
        return self.graph.plugins[self.componentName](self.flowMeta.conditionParams);
    };

    /**
     * @override
     */
    self.serializerJadeConfig = (jadeConfig) => {
        self.flowMeta.conditionParams = jadeConfig;
    };

    /**
     * 获取前置节点时去除系统上下文节点。
     *
     * @override
     */
    const getPreNodeInfos = self.getPreNodeInfos;
    self.getPreNodeInfos = () => {
        const preNodeInfos = getPreNodeInfos.apply(self);
        // 使用 filter 方法移除 id 等于 VIRTUAL_CONTEXT_NODE.id 的元素
        return preNodeInfos.filter(node => node.id !== VIRTUAL_CONTEXT_NODE.id);
    };

    /**
     * 将jadeConfig格式转换为试运行报告识别的格式
     *
     * @param data
     * @return {*}
     */
    const transformData = data => data.map(item => {
        return {
            logic: item.conditionRelation,
            conditions: item.conditions.map(condition => {
                const left = condition.value.find(v => v.name === 'left');
                const right = condition.value.find(v => v.name === 'right');

                const transformedCondition = {
                    left: {
                        key: left.value && left.value.join('.'),
                        type: left.type,
                        value: ""
                    },
                    operator: condition.condition
                };

                if (right && right.from === "Input") {
                    transformedCondition.right = {
                        key: '',
                        type: right.type,
                        value: right.value && right.value
                    };
                } else if (right && right.from === "Reference" && right.value.length > 0) {
                    transformedCondition.right = {
                        key: right.value && right.value.join('.'),
                        type: right.type,
                        value: ""
                    };
                }

                return transformedCondition;
            })
        };
    });

    /**
     * 条件节点默认的测试报告章节
     */
    self.getRunReportSections = () => {
        const branches = self.drawer.getLatestJadeConfig().branches;
        const sectionSource = self.input ? self.input.branches : transformData(branches);
        // 过滤掉else分支
        return sectionSource.filter(branch => !branch.conditions.some(condition => condition.condition === 'true')).map((branch, index) => {
            const no = (index + 1).toString();
            const name = "条件 " + no;
            return {no: no, name: name, type: SECTION_TYPE.CONDITION, data: branch}
        });
    };

    return self;
};