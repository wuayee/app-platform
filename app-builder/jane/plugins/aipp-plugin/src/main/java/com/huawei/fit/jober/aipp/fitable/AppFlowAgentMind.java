package com.huawei.fit.jober.aipp.fitable;

import com.huawei.fit.jober.FlowableService;
import com.huawei.fit.jober.aipp.common.Utils;
import com.huawei.fit.jober.aipp.constants.AippConst;
import com.huawei.fit.jober.aipp.enums.LlmModelNameEnum;
import com.huawei.fit.jober.aipp.fitable.agent.AippFlowAgent;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.annotation.Fitable;
import com.huawei.fitframework.annotation.Value;
import com.huawei.fitframework.inspection.Validation;
import com.huawei.fitframework.log.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class AppFlowAgentMind implements FlowableService {
    private static final Logger log = Logger.get(AppFlowAgentMind.class);
    private final AippFlowAgent agent;
    private final String agentAippId;

    public AppFlowAgentMind(AippFlowAgent agent,
            @Value("${aipp.agent_mind_aipp}") String agentAippId) {
        this.agent = agent;
        this.agentAippId = agentAippId;
    }

    /**
     * 启动流程实例：解析数据 + 生成脑图
     *
     * @param flowData 流程执行上下文数据
     * @return
     */
    @Fitable("com.huawei.fit.jober.aipp.fitable.AppFlowAgentMind")
    @Override
    public List<Map<String, Object>> handleTask(List<Map<String, Object>> flowData) {
        log.info("handle AppFlowAgentMind");
        Map<String, Object> businessData = Utils.getBusiness(flowData);
        String recommendDoc = (String) businessData.get(AippConst.INST_RECOMMEND_DOC_KEY);
        Validation.notNull(recommendDoc, "recommendDoc info cannot be null");

        Map<String, Object> agentParam = new HashMap<String, Object>() {{
            put(AippConst.BS_MODEL_PROMPT_KEY, recommendDoc);
            put(AippConst.BS_AGENT_ID_KEY, agentAippId);
            put(AippConst.BS_MODEL_NAME_KEY, LlmModelNameEnum.XIAOHAI.getValue());
            put(AippConst.BS_AGENT_RESULT_LINK_KEY, AippConst.INST_MIND_DATA_KEY);
            put(AippConst.BS_AGENT_INST_URL_LINK_KEY, AippConst.INST_MIND_URL_KEY);
        }};
        businessData.put(AippConst.BS_AGENT_PARAM_KEY, agentParam);

        return agent.handleTask(flowData);
    }
}
