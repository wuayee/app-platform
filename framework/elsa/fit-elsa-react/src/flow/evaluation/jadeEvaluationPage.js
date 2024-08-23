import {jadeFlowPage} from "@/flow/jadeFlowPage.js";
import {NODE_STATUS} from "@";
import {createProcessor} from "@/flow/evaluation/runableProcessors.js";

const DISTANCE = 20;
const EVALUATION_START_NODE = "evaluationStartNodeStart";
const EVALUATION_END_NODE = "evaluationEndNodeEnd";
const EVALUATION_NODE = "evaluationNode";

/**
 * 评估页面.
 *
 * @param div dom元素.
 * @param graph 画布.
 * @param name 名称.
 * @param id 唯一标识.
 * @returns {*} 页面对象.
 */
export const jadeEvaluationPage = (div, graph, name, id) => {
    const self = jadeFlowPage(div, graph, name, id);
    self.type = "jadeEvaluationPage";
    self.serializedFields.batchAdd("flowMetas");
    self.evaluationChain = [];
    self.latestFlowMetas = {};

    /**
     * 归一化，使评估页面在可使用之前处于正常状态.
     * 1、如果是发布过，那么图形基本处于readOnly状态，只能查看
     * 2、如果是未发布过
     *  2.1、如果没有【评估开始】和【评估结束】节点，那么要默认创建出两个节点，
     *       此时说明是第一次进入，需要将所有的非评估节点设置为不可运行状态(runnable = false)
     *  2.2、如果存在【评估开始】和【评估结束】节点，那么说明不是第一次进入，此时应该不需要做任何处理.
     *
     * @param graphData 画布数据.
     * @param isPublished 是否发布过.
     */
    self.normalize = (graphData, isPublished) => {
        if (isPublished) {
            self.shapes.forEach(s => {
                s.ignoreChange(() => {
                    s.moveable = false;
                    s.selectable = false;
                    s.deletable = false;
                });
                s.setStatus({disabled: true, published: true});
                s.invalidateAlone();
            });
        } else {
            const startNode = self.getStartNode();
            !startNode && firstEvaluate();
            self.shapes.filter(s => !s.isTypeof(EVALUATION_NODE)).filter(s => s.flowMeta).forEach(s => {
                self.latestFlowMetas[s.id] = JSON.parse(JSON.stringify(s.flowMeta));
            });
        }
    };

    const firstEvaluate = () => {
        self.flowMetas = {};

        let minX = self.shapes[0].x;
        let maxX = self.shapes[0].x + self.shapes[0].width;
        let maxY = self.shapes[0].y + self.shapes[0].height;
        self.shapes.forEach(s => {
            if (s.flowMeta) {
                self.flowMetas[s.id] = JSON.parse(JSON.stringify(s.flowMeta));
            }
            minX = Math.min(minX, s.x);
            maxX = Math.max(maxX, s.x + s.width);
            maxY = Math.max(maxY, s.y + s.height);
        });

        // 创建开始结束节点.
        self.createNew(EVALUATION_START_NODE, minX, maxY + DISTANCE);
        self.createNew(EVALUATION_END_NODE, maxX, maxY + DISTANCE);
    };

    /**
     * 获取开始节点.
     *
     * @return {*}
     */
    self.getStartNode = () => {
        return self.shapes.find(s => s.type === EVALUATION_START_NODE);
    };

    /**
     * 当有图形被连接上时触发.
     */
    self.onShapeConnect = () => {
        self.runnableFlow = getEvaluationFlow();

        // 先修改所有节点的runnable状态，并返回runnable发生了变化的节点，再处理变化的节点
        const processors = self.shapes.map(s => {
            const processor = createProcessor(s);
            processor.checkRunnable(self.runnableFlow);
            return processor;
        });
        processors.filter(p => p.isUpdated).forEach(p => p.process(self));
    };

    /*
     * 从开始节点开始向后遍历
     * 若后继节点都是普通节点，那么后继节点继续往后遍历
     * 若后继节点既存在普通节点，也存在评估节点，那么过滤掉普通节点，评估节点继续往后遍历
     * 直到遍历到评估结束节点为止，这条链路上的所有节点，都是runnable节点，线也都是runnable状态
     * 排除最后没有到评估节点链路中的节点.
     */
    const getEvaluationFlow = () => {
        const startNode = self.getStartNode();
        const evaluationChain = new Set();
        evaluationChain.add(startNode.id);
        if (!traverse(startNode, evaluationChain)) {
            evaluationChain.clear();
        }
        return evaluationChain;
    };

    const traverse = (node, evaluationChain) => {
        if (node.type === EVALUATION_END_NODE) {
            return true;
        }

        // 既不是结束节点，并且也没有后继节点，那么说明当前链路不是评估链路.
        const nextNodes = node.getNextNodes();
        if (nextNodes.length === 0) {
            return false;
        }

        const checkNode = (n) => {
            evaluationChain.add(n.id);
            if (!traverse(n, evaluationChain)) {
                evaluationChain.delete(n.id);
                return false;
            }
            return true;
        };

        // 当前节点是评估节点
        // 当前节点不是评估节点，但是nextNodes都是普通节点.
        // 当前节点是条件节点
        // 上述三种情况，nextNodes都需要遍历.
        if (node.isTypeof(EVALUATION_NODE)
            || node.isTypeof("conditionNodeCondition")
            || nextNodes.every(n => !n.isTypeof(EVALUATION_NODE))) {
            return nextNodes.map(n => checkNode(n)).reduce((acc, v) => acc || v, false);
        } else {
            // 当前节点不是评估节点，nextNodes存在评估节点，只遍历评估节点.
            return nextNodes.filter(n => n.isTypeof(EVALUATION_NODE))
                .map(n => checkNode(n))
                .reduce((acc, v) => acc || v, false);
        }
    };

    /**
     * 和onShapeConnect保持一致.
     */
    self.onShapeOffConnect = () => {
        self.onShapeConnect();
    };

    /**
     * 重置当前页中节点状态
     *
     * @param nodes 节点
     */
    self.resetRunStatus = nodes => {
        nodes.forEach(n => {
            n.moveable = true;
            n.setStatus({runStatus: NODE_STATUS.DEFAULT, disabled: !n.isTypeof(EVALUATION_NODE)});
            delete n.output;
            delete n.input;
            delete n.cost;
        });
        graph.activePage.isRunning = false;
    };

    /**
     * 返回停止运行流程测试方法
     *
     */
    self.stopRun = nodes => {
        nodes.forEach(n => {
            // 修改属性会导致dirties事件，并且dirties事件是异步的，因此在触发时，isRunning已是false状态.
            // 所以这里需要使用ignoreChange使其不触发dirties事件.
            n.ignoreChange(() => {
                n.moveable = true;
                n.emphasized = false;
            });
            n.setStatus({disabled: !n.isTypeof(EVALUATION_NODE)});
        });
        graph.activePage.isRunning = false;
    };

    /**
     * @override
     */
    self.isShapeReferenceDisabled = (shapeStatus) => {
        return shapeStatus.published ||
            (shapeStatus.runStatus === NODE_STATUS.RUNNING || shapeStatus.runStatus === NODE_STATUS.UN_RUNNING) ||
            (!shapeStatus.runnable && shapeStatus.disabled);
    };

    /**
     * @override
     */
    self.isShapeModifiable = (shape) => {
        return shape.isTypeof(EVALUATION_NODE) || shape.runnable !== false;
    };

    /**
     * @override
     */
    const validate = self.validate;
    self.validate = async () => {
        const runnableFlow = getEvaluationFlow();
        if (runnableFlow.size === 0) {
            return Promise.reject("评估流程不存在.");
        }
        return await validate.apply(self);
    };

    return self;
};