/**
 * 创建处理器.
 *
 * @param shape 图形对象.
 * @return {{}} 处理器对象.
 */
export const createProcessor = (shape) => {
    if (shape.isTypeof("jadeEvent")) {
        return eventProcessor(shape);
    } else if (shape.isTypeof("evaluationNode")) {
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
    self.runnable = shape.runnable;

    /**
     * 检查runnable状态.
     *
     * @param runnableFlow runnable流.
     */
    self.checkRunnable = (runnableFlow) => {
        self.runnable = runnableFlow.has(shape.id);
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

        onShapeRunnableSwitch(shape, self.runnable, flowMeta);
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
    self.runnable = shape.runnable;

    /**
     * 检查runnable状态.
     *
     * @param runnableFlow runnable流.
     */
    self.checkRunnable = (runnableFlow) => {
        const prevRunnable = self.runnable;
        self.runnable = runnableFlow.has(shape.id);
        self.isUpdated = prevRunnable !== self.runnable;
    };

    /**
     * runnable状态发生变化时的处理逻辑.
     *
     * @param page 页面对象.
     */
    self.process = (page) => {
        // 图形runnable由true切换为false，需要保存最新的flowMeta数据.
        if (!self.runnable) {
            // 这里必须深拷贝，否则会导致进入流程-修改引用-出流程-再进流程，修改的引用失效.
            page.latestFlowMetas[shape.id] = JSON.parse(JSON.stringify(shape.getFlowMeta()));
        }
        const flowMeta = self.runnable ? page.latestFlowMetas[shape.id] : page.flowMetas[shape.id];
        onShapeRunnableSwitch(shape, self.runnable, flowMeta);
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
    self.runnable = shape.runnable;

    /**
     * 检查runnable状态.
     *
     * @param runnableFlow runnable流.
     */
    self.checkRunnable = (runnableFlow) => {
        const prevRunnable = self.runnable;
        self.runnable = runnableFlow.has(shape.fromShape) && runnableFlow.has(shape.toShape);
        self.isUpdated = prevRunnable !== self.runnable;
    };

    /**
     * 空实现.
     */
    self.process = () => {
        shape.runnable = self.runnable;
    };

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
    self.runnable = shape.runnable;

    /**
     * 检查runnable状态.
     *
     * @param runnableFlow runnable流.
     */
    self.checkRunnable = (runnableFlow) => {
        const prevRunnable = self.runnable;
        shape.runnable = runnableFlow.has(shape.id);
        self.isUpdated = prevRunnable !== self.runnable;
    };

    /**
     * 空实现.
     */
    self.process = () => {
        shape.setStatus({runnable: self.runnable});
    };

    return self;
};

const onShapeRunnableSwitch = (s, runnable, flowMeta) => {
    s.setStatus({runnable});
    if (flowMeta) {
        s.cleanObserved();
        s.setFlowMeta(flowMeta);
    } else {
        // flowMeta不存在，说明是第一次从非runnable状态切换到runnable状态，此时disable所有reference即可.
        s.disableObserved();
    }
};