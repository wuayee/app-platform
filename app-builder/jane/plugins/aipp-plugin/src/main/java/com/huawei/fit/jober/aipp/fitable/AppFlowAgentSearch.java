/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.fitable;

import com.huawei.fit.jober.FlowableService;
import com.huawei.fit.jober.aipp.constants.AippConst;
import com.huawei.fit.jober.aipp.enums.LlmModelNameEnum;
import com.huawei.fit.jober.aipp.fitable.agent.AippFlowAgent;
import com.huawei.fit.jober.aipp.service.AippLogService;
import com.huawei.fit.jober.aipp.util.DataUtils;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.annotation.Fitable;
import com.huawei.fitframework.annotation.Value;
import com.huawei.fitframework.inspection.Validation;
import com.huawei.fitframework.log.Logger;
import com.huawei.fitframework.util.ObjectUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 流程服务接口实现类： 流程服务接口的具体实现类，提供流程服务的具体实现方法
 *
 * @author s00664640
 * @since 2024-05-10
 */
@Component
public class AppFlowAgentSearch implements FlowableService {
    private static final Logger log = Logger.get(AppFlowAgentSearch.class);

    private final AippFlowAgent agent;
    private final String agentAippId;
    private final AippLogService aippLogService;

    public AppFlowAgentSearch(AippFlowAgent agent, @Value("${aipp.agent_search_aipp}") String agentAippId,
            AippLogService aippLogService) {
        this.agent = agent;
        this.agentAippId = agentAippId;
        this.aippLogService = aippLogService;
    }

    private String getPrompt(String text) {
        String basePrompt =
                "你是一个存储产品采购分析员，请根据以下文本内容，分析出对方的采购内容，要求输出内容简洁，文本内容如下：";
        return basePrompt + text;
    }

    /**
     * 启动流程实例： 解析并识别采购信息、检索小海数存知识
     *
     * @param flowData 流程执行上下文数据
     * @return 流程执行上下文数据
     */
    @Fitable("com.huawei.fit.jober.aipp.fitable.AppFlowAgentSearch")
    @Override
    public List<Map<String, Object>> handleTask(List<Map<String, Object>> flowData) {
        log.info("handle AppFlowAgentSearch");

        Map<String, Object> businessData = DataUtils.getBusiness(flowData);
        String prompt = ObjectUtils.cast(businessData.get(AippConst.BS_MODEL_PROMPT_KEY));
        Validation.notNull(prompt, "prompt cannot be null");

        String msg = "基于以上信息，我决定调用智能推荐产品智能体，为您检索匹配产品";
        this.aippLogService.insertMsgLog(msg, flowData);

        Map<String, Object> agentParam = new HashMap<String, Object>() {{
            put(AippConst.BS_MODEL_PROMPT_KEY, prompt);
            put(AippConst.BS_AGENT_ID_KEY, agentAippId);
            put(AippConst.BS_MODEL_NAME_KEY, LlmModelNameEnum.XIAOHAI.getValue());
            put(AippConst.BS_AGENT_RESULT_LINK_KEY, AippConst.INST_RECOMMEND_DOC_KEY);
        }};
        businessData.put(AippConst.BS_AGENT_PARAM_KEY, agentParam);

        return agent.handleTask(flowData);
    }
}
