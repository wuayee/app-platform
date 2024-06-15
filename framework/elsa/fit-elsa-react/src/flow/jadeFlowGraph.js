import {defaultGraph} from "@fit-elsa/elsa-core";
import {jadeFlowPage} from "@/flow/jadeFlowPage.js";
import {jadeEvent} from "@/components/jadeEvent.jsx";
import {taskNode} from "@/components/testDemo/taskNode.jsx";
import {endNodeEnd} from "@/components/end/endNodeEnd.jsx";
import {endComponent} from "@/components/end/endComponent.jsx";
import {retrievalNodeState} from "@/components/retrieval/retrievalNodeState.jsx";
import {retrievalComponent} from "@/components/retrieval/retrievalComponent.jsx";
import {listener1Node} from "@/components/listenerDemo/listener1Node.jsx";
import {listener1Component} from "@/components/listenerDemo/listener1Component.jsx";
import {listener2Node} from "@/components/listenerDemo/listener2Node.jsx";
import {listener2Component} from "@/components/listenerDemo/listener2Component.jsx";
import {listener3Node} from "@/components/listenerDemo/listener3Node.jsx";
import {listener3Component} from "@/components/listenerDemo/listener3Component.jsx";
import {jadeInputTreeNode} from "@/components/listenerDemo/jadeInputTreeNode.jsx";
import {jadeInputTreeComponent} from "@/components/listenerDemo/jadeInputTreeComponent.jsx";
import {testNode} from "@/components/replaceDemo/testNode.jsx";
import {testComponent} from "@/components/replaceDemo/testComponent.jsx";
import {replaceComponent} from "@/components/replaceDemo/replaceComponent.jsx";
import {startNodeStart} from "@/components/start/startNodeStart.jsx";
import {startComponent} from "@/components/start/startComponent.jsx";
import {llmNodeState} from "@/components/llm/llmNodeState.jsx";
import {llmComponent} from "@/components/llm/llmComponent.jsx";
import {manualCheckNodeState} from "@/components/manualCheck/manualCheckNodeState.jsx";
import {manualCheckComponent} from "@/components/manualCheck/manualCheckComponent.jsx";
import {fitInvokeNodeState} from "@/components/fitInvokeNode/fitInvokeNodeState.jsx";
import {fitInvokeComponent} from "@/components/fitInvokeNode/fitInvokeComponent.jsx";
import {toolInvokeComponent} from "@/components/toolInvokeNode/toolInvokeComponent.jsx";
import {toolInvokeNodeState} from "@/components/toolInvokeNode/toolInvokeNodeState.jsx";
import {conditionNodeCondition} from "@/components/condition/conditionNodeCondition.jsx";
import {conditionComponent} from "@/components/condition/conditionComponent.jsx";
import {huggingFaceNodeState} from "@/components/huggingFace/huggingFaceNodeState.jsx";
import {huggingFaceComponent} from "@/components/huggingFace/huggingFaceComponent.jsx";

/**
 * jadeFlow的专用画布.
 *
 * @param div dom元素.
 * @param title 名称.
 */
export const jadeFlowGraph = (div, title) => {
    const self = defaultGraph(div, title);
    self.type = "jadeFlowGraph";
    self.pageType = "jadeFlowPage";
    self.enableText = false;
    self.flowMeta = {
        "exceptionFitables": ["com.huawei.fit.jober.aipp.fitable.AippFlowExceptionHandler"],
        "enableOutputScope": true
    };
    self.setting.borderColor = "#047bfc";
    self.setting.focusBorderColor = "#047bfc";
    self.setting.mouseInBorderColor = "#047bfc";

    /**
     * 序列化新增flowMeta字段.
     *
     * @override
     */
    const serialize = self.serialize;
    self.serialize = () => {
        const serialized = serialize.apply(self);
        serialized.flowMeta = self.flowMeta;
        return serialized;
    };

    /**
     * 导入flow相关依赖.
     *
     * @override
     */
    const initialize = self.initialize;
    self.initialize = async () => {
        self.registerPlugin("jadeFlowPage", jadeFlowPage);
        self.registerPlugin("jadeEvent", jadeEvent);
        self.registerPlugin("taskNode", taskNode);
        self.registerPlugin("endNodeEnd", endNodeEnd);
        self.registerPlugin("endComponent", endComponent);
        self.registerPlugin("retrievalNodeState", retrievalNodeState);
        self.registerPlugin("retrievalComponent", retrievalComponent);
        self.registerPlugin("listener1Node", listener1Node);
        self.registerPlugin("listener1Component", listener1Component);
        self.registerPlugin("listener2Node", listener2Node);
        self.registerPlugin("listener2Component", listener2Component);
        self.registerPlugin("listener3Node", listener3Node);
        self.registerPlugin("listener3Component", listener3Component);
        self.registerPlugin("jadeInputTreeNode", jadeInputTreeNode);
        self.registerPlugin("jadeInputTreeComponent", jadeInputTreeComponent);
        self.registerPlugin("testNode", testNode);
        self.registerPlugin("testComponent", testComponent);
        self.registerPlugin("replaceComponent", replaceComponent);
        self.registerPlugin("startNodeStart", startNodeStart);
        self.registerPlugin("startComponent", startComponent);
        self.registerPlugin("llmNodeState", llmNodeState);
        self.registerPlugin("llmComponent", llmComponent);
        self.registerPlugin("manualCheckNodeState", manualCheckNodeState);
        self.registerPlugin("manualCheckComponent", manualCheckComponent);
        self.registerPlugin("fitInvokeNodeState", fitInvokeNodeState);
        self.registerPlugin("toolInvokeNodeState", toolInvokeNodeState);
        self.registerPlugin("fitInvokeComponent", fitInvokeComponent);
        self.registerPlugin("toolInvokeComponent", toolInvokeComponent);
        self.registerPlugin("conditionNodeCondition", conditionNodeCondition);
        self.registerPlugin("conditionComponent", conditionComponent);
        self.registerPlugin("huggingFaceNodeState", huggingFaceNodeState);
        self.registerPlugin("huggingFaceComponent", huggingFaceComponent);
        return initialize.apply(self);
    };

    /**
     * 注册plugin.
     *
     * @param pluginName 插件名称.
     * @param plugin 组件.
     * @param namespace 命名空间.
     */
    self.registerPlugin = (pluginName, plugin, namespace = null) => {
        if (namespace) {
            self.plugins[`${namespace}.${pluginName}`] = plugin;
        } else {
            self.plugins[pluginName] = plugin;
        }
    };

    /**
     * 数据发生变化时触发.
     *
     * @param data 数据.
     * @param dirtyAction 数据变化的action.
     */
    const dirtied = self.dirtied;
    self.dirtied = (data, dirtyAction) => {
        dirtied.call(self, data, dirtyAction);
        self.onChangeCallback && self.onChangeCallback();
    };

    /**
     * 获取对应节点的配置.
     *
     * @param nodeType 节点类型.
     * @return {*} 配置信息.
     */
    self.getConfig = (nodeType) => {
        return self.configs?.find(config => config.node === nodeType);
    };

    return self;
};