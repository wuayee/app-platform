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
                    s.disabled = true;
                });
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
            s.runnable = false;
            s.deletable = false;
            s.disabled = true;

            // 条件节点需要将所有的条件也设置为runnable = false.
            if (s.isTypeof("conditionNodeCondition")) {
                s.flowMeta.conditionParams.branches.forEach(b => b.runnable = false);
            }

            if (s.flowMeta) {
                self.flowMetas[s.id] = JSON.parse(JSON.stringify(s.flowMeta));
            }

            minX = Math.min(minX, s.x);
            maxX = Math.max(maxX, s.x + s.width);
            maxY = Math.max(maxY, s.y + s.height);
        });

        // 创建开始结束节点.
        self.createNew({shapeType: EVALUATION_START_NODE, x: minX, y: maxY + DISTANCE});
        self.createNew({shapeType: EVALUATION_END_NODE, x: maxX, y: maxY + DISTANCE});
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

    return self;
};

/* 创建处理器 */
const createProcessor = (shape) => {
    if (shape.isTypeof("jadeEvent")) {
        return eventProcessor(shape);
    } else if (shape.isTypeof(EVALUATION_NODE)) {
        return evaluationProcessor(shape);
    } else if (shape.isTypeof("conditionNodeCondition")) {
        return conditionProcessor(shape);
    } else {
        return normalProcessor(shape);
    }
};

/**
 * 条件节点处理器.
 *
 * @param shape 图形节点.
 * @return {{}}
 */
const conditionProcessor = (shape) => {
    const self = {};
    self.isUpdated = false;
    const updatedBranches = [];

    /**
     * 检查runnable状态.
     *
     * @param runnableFlow runnable流.
     */
    self.checkRunnable = (runnableFlow) => {
        shape.runnable = runnableFlow.has(shape.id);
        shape.getFlowMeta().conditionParams.branches.forEach((b, index) => {
            const events = shape.getEventsByBranchId(b.id);
            const prevRunnable = b.runnable;
            const runnable = events.length > 0 && events.some(e => runnableFlow.has(e.toShape));
            if (prevRunnable !== runnable) {
                self.isUpdated = true;
                updatedBranches.push({id: b.id, index: index, runnable});
            }
        });
    };

    /**
     * runnable状态发生变化时的处理逻辑.
     *
     * @param page 页面对象.
     */
    self.process = (page) => {
        const flowMeta = shape.getFlowMeta();
        flowMeta.conditionParams.branches = flowMeta.conditionParams.branches.map((b, index) => {
            const updatedBranch = updatedBranches.find(ub => ub.index === index);
            if (updatedBranch) {
                if (updatedBranch.runnable) {
                    const branch = page.latestFlowMetas[shape.id].conditionParams.branches[index];
                    branch.runnable = updatedBranch.runnable;
                    return branch;
                } else {
                    page.latestFlowMetas[shape.id].conditionParams.branches[index] = b;
                    return {...page.flowMetas[shape.id].conditionParams.branches[index]};
                }
            } else {
                return b;
            }
        });

        onShapeRunnableSwitch(shape, flowMeta);
    };

    return self;
};

/**
 * 普通节点处理器.
 *
 * @param shape 图形节点.
 * @return {{}}
 */
const normalProcessor = (shape) => {
    const self = {};
    self.isUpdated = false;

    /**
     * 检查runnable状态.
     *
     * @param runnableFlow runnable流.
     */
    self.checkRunnable = (runnableFlow) => {
        const prevRunnable = shape.runnable;
        shape.runnable = runnableFlow.has(shape.id);
        self.isUpdated = prevRunnable !== shape.runnable;
    };

    /**
     * runnable状态发生变化时的处理逻辑.
     *
     * @param page 页面对象.
     */
    self.process = (page) => {
        // 图形runnable由true切换为false，需要保存最新的flowMeta数据.
        if (!shape.runnable) {
            // 这里必须深拷贝，否则会导致进入流程-修改引用-出流程-再进流程，修改的引用失效.
            page.latestFlowMetas[shape.id] = JSON.parse(JSON.stringify(shape.getFlowMeta()));
        }
        const flowMeta = shape.runnable ? page.latestFlowMetas[shape.id] : page.flowMetas[shape.id];
        onShapeRunnableSwitch(shape, flowMeta);
    };

    return self;
};

/**
 * 线处理器.
 *
 * @param shape 图形节点.
 * @return {{}}
 */
const eventProcessor = (shape) => {
    const self = {};
    self.isUpdated = false;

    /**
     * 检查runnable状态.
     *
     * @param runnableFlow runnable流.
     */
    self.checkRunnable = (runnableFlow) => {
        shape.runnable = runnableFlow.has(shape.fromShape) && runnableFlow.has(shape.toShape);
    };

    /**
     * 空实现.
     */
    self.process = () => {};

    return self;
};

/**
 * 评估节点处理器.
 *
 * @param shape 图形节点.
 * @return {{}}
 */
const evaluationProcessor = (shape) => {
    const self = {};
    self.isUpdated = false;

    /**
     * 检查runnable状态.
     *
     * @param runnableFlow runnable流.
     */
    self.checkRunnable = (runnableFlow) => {
        shape.runnable = runnableFlow.has(shape.id);
    };

    /**
     * 空实现.
     */
    self.process = () => {};

    return self;
};

const onShapeRunnableSwitch = (s, flowMeta) => {
    s.disabled = !s.runnable;
    s.drawer.setDisabled(s.disabled);
    if (flowMeta) {
        s.cleanObserved();
        s.setFlowMeta(flowMeta);
    } else {
        // flowMeta不存在，说明是第一次从非runnable状态切换到runnable状态，此时disable所有reference即可.
        s.disableObserved();
    }
};