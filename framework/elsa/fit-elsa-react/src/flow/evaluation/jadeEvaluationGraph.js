import {jadeEvaluationPage} from "@/flow/evaluation/jadeEvaluationPage.js";
import {
    evaluationAlgorithmsComponent
} from "@/components/evaluation/evaluationAlgorithms/evaluationAlgorithmsComponent.jsx";
import {
    evaluationAlgorithmsNodeState
} from "@/components/evaluation/evaluationAlgorithms/evaluationAlgorithmsNodeState.jsx";
import {evaluationStartComponent} from "@/components/evaluation/evaluationStart/evaluationStartComponent.jsx";
import {evaluationStartNodeStart} from "@/components/evaluation/evaluationStart/evaluationStartNodeStart.jsx";
import {evaluationTestSetNodeState} from "@/components/evaluation/evaluationTestset/evaluationTestSetNodeState.jsx";
import {evaluationTestSetComponent} from "@/components/evaluation/evaluationTestset/evaluationTestSetComponent.jsx";
import {evaluationEndNodeEnd} from "@/components/evaluation/evaluationEnd/evaluationEndNodeEnd.jsx";
import {evaluationEndComponent} from "@/components/evaluation/evaluationEnd/evaluationEndComponent.jsx";
import {jadeFlowGraph} from "@/flow/jadeFlowGraph.js";

/**
 * jadeFlow的专用画布.
 *
 * @param div dom元素.
 * @param title 名称.
 */
export const jadeEvaluationGraph = (div, title) => {
    const self = jadeFlowGraph(div, title);
    self.type = "jadeEvaluationGraph";
    self.pageType = "jadeEvaluationPage";

    /**
     * 导入flow相关依赖.
     *
     * @override
     */
    const initialize = self.initialize;
    self.initialize = async () => {
        self.registerPlugin("jadeEvaluationPage", jadeEvaluationPage);
        self.registerPlugin("evaluationStartComponent", evaluationStartComponent);
        self.registerPlugin("evaluationStartNodeStart", evaluationStartNodeStart);
        self.registerPlugin("evaluationAlgorithmsComponent", evaluationAlgorithmsComponent);
        self.registerPlugin("evaluationAlgorithmsNodeState", evaluationAlgorithmsNodeState);
        self.registerPlugin("evaluationTestSetComponent", evaluationTestSetComponent);
        self.registerPlugin("evaluationTestSetNodeState", evaluationTestSetNodeState);
        self.registerPlugin("evaluationEndComponent", evaluationEndComponent);
        self.registerPlugin("evaluationEndNodeEnd", evaluationEndNodeEnd);
        return initialize.apply(self);
    };

    /**
     * 评估.
     *
     * @param graphData 画布数据.
     * @param isPublished 是否已发布.
     * @return {Promise<void>} Promise.
     */
    self.evaluate = async (graphData, isPublished) => {
        const pageData = self.getPageData(0);
        normalizeData(pageData, isPublished);
        await self.edit(0, self.div, pageData.id);
        self.activePage.normalize(graphData, isPublished);
    };

    const normalizeData = (pageData, isPublished) => {
        if (isPublished) {
            pageData.shapes.forEach(shapeData => {
                shapeData.moveable = false;
                shapeData.selectable = false;
                shapeData.deletable = false;
                shapeData.disabled = true;
                shapeData.published = true;
            });
        } else {
            const start = pageData.shapes.find(s => s.type === "evaluationStartNodeStart");
            if (!start) {
                pageData.shapes.forEach(shapeData => {
                    shapeData.deletable = false;
                    shapeData.runnable = false;
                    shapeData.disabled = true;
                    if (shapeData.type === "conditionNodeCondition") {
                        shapeData.flowMeta.conditionParams.branches.forEach(b => b.runnable = false);
                    }
                });
            }
        }
    };

    return self;
};