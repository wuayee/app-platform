import {jadeFlowPage} from "@/flow/jadeFlowPage.js";

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
    self.evaluationChain = [];

    /**
     * 归一化，使评估页面在可使用之前处于正常状态.
     * 1、如果是发布过，那么图形基本处于readOnly状态，只能查看
     * 2、如果是未发布过
     *  2.1、如果没有【评估开始】和【评估结束】节点，那么要默认创建出两个节点，
     *       此时说明是第一次进入，需要将所有的非评估节点设置为不可运行状态(runnable = false)
     *  2.2、如果存在【评估开始】和【评估结束】节点，那么说明不是第一次进入，此时应该不需要做任何处理.
     *
     * @param isPublished 是否发布过.
     */
    self.normalize = (isPublished) => {
        if (isPublished) {
            self.shapes.forEach(s => {
                s.ignoreChange(() => {
                    s.moveable = false;
                    s.selectable = false;
                    s.deletable = false;
                });
                s.drawer.setDisabled && s.drawer.setDisabled(true);
                s.invalidateAlone();
            });
        } else {
            const startNode = self.getStartNode();
            !startNode && firstEvaluate();
        }
    };

    const firstEvaluate = () => {
        let minX = self.shapes[0].x;
        let maxX = self.shapes[0].x + self.shapes[0].width;
        let maxY = self.shapes[0].y + self.shapes[0].height;
        self.shapes.forEach(s => {
            s.runnable = false;
            s.deletable = false;

            // event不用设置disabled.
            s.drawer.setDisabled && s.drawer.setDisabled(true);

            // 条件节点需要将所有的条件也设置为runnable = false.
            if (s.isTypeof("conditionNodeCondition")) {
                s.flowMeta.conditionParams.branches.forEach(b => b.runnable = false);
            }

            minX = Math.min(minX, s.x);
            maxX = Math.max(maxX, s.x + s.width);
            maxY = Math.max(maxY, s.y + s.height);
        });
        self.createShape(EVALUATION_START_NODE, minX, maxY + DISTANCE);
        self.createShape(EVALUATION_END_NODE, maxX, maxY + DISTANCE);
        self.reset();
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
        const evaluationFlow = getEvaluationFow();
        self.shapes.filter(s => s.isTypeof(EVALUATION_NODE)).forEach(s => {
            const preStatus = s.runnable;
            if (s.isTypeof("jadeEvent")) {
                s.runnable = evaluationFlow.has(s.fromShape) && evaluationFlow.has(s.toShape);
            } else if (s.isTypeof("conditionNodeCondition")) {
                s.runnable = evaluationFlow.has(s);
                s.flowMeta.conditionParams.branches.forEach(b => {
                    const event = s.getEventByBranchId(b);
                    b.runnable = event && evaluationFlow.has(event.toShape);
                });
            } else {
                s.runnable = evaluationFlow.has(s);
            }
            if (preStatus !== s.runnable) {
                s.invalidateAlone();
            }
        });
    };

    /*
     * 从开始节点开始向后遍历
     * 若后继节点都是普通节点，那么后继节点继续往后遍历
     * 若后继节点既存在普通节点，也存在评估节点，那么过滤掉普通节点，评估节点继续往后遍历
     * 直到遍历到评估节点为止，这条链路上的所有节点，都是runnable节点，线也都是runnable状态
     * 排除最后没有到评估节点链路中的节点.
     */
    const getEvaluationFow = () => {
        const startNode = self.getStartNode();
        const evaluationChain = new Set();
        evaluationChain.add(startNode);
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
            evaluationChain.add(n);
            if (!traverse(n, evaluationChain)) {
                evaluationChain.delete(n);
                return false;
            }
            return true;
        };

        // 当前节点是评估节点
        // 当前节点不是评估节点，但是nextNodes都是普通节点.
        // 上述两种情况，nextNodes都需要遍历.
        if (node.isTypeof(EVALUATION_NODE) || nextNodes.every(n => !n.isTypeof(EVALUATION_NODE))) {
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

    return self;
}