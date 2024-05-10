/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.aipp.fitable;

import com.huawei.fit.jane.common.entity.OperationContext;
import com.huawei.fit.jane.meta.multiversion.MetaInstanceService;
import com.huawei.fit.jane.meta.multiversion.instance.InstanceDeclarationInfo;
import com.huawei.fit.jober.FlowableService;
import com.huawei.fit.jober.aipp.common.Utils;
import com.huawei.fit.jober.aipp.constants.AippConst;
import com.huawei.fit.jober.aipp.enums.LlmModelNameEnum;
import com.huawei.fit.jober.aipp.service.AippLogService;
import com.huawei.fit.jober.aipp.service.DistributedMapService;
import com.huawei.fit.jober.aipp.service.LLMService;
import com.huawei.fit.jober.common.ErrorCodes;
import com.huawei.fit.jober.common.exceptions.JobberException;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.annotation.Fit;
import com.huawei.fitframework.annotation.Fitable;
import com.huawei.fitframework.annotation.Value;
import com.huawei.fitframework.inspection.Validation;
import com.huawei.fitframework.log.Logger;
import com.huawei.hllm.model.LlmModel;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 根据大模型类型，调用不同的模型的问答接口
 */
@Component
public class LLMText2Text implements FlowableService {
    private static final int ASK_MODEL_MAX_RETRY_TIMES = 2;
    private static final Logger log = Logger.get(LLMText2Text.class);
    private final LLMService llmService;
    private final MetaInstanceService metaInstanceService;
    private final AippLogService aippLogService;
    private final DistributedMapService mapService;
    private final int hllmReadTimeout;

    public LLMText2Text(@Fit LLMService llmService, @Fit MetaInstanceService metaInstanceService,
            @Fit AippLogService aippLogService, @Fit DistributedMapService mapService,
            @Value("${hllm.client.read-timeout}") int hllmReadTimeout) {
        this.llmService = llmService;
        this.metaInstanceService = metaInstanceService;
        this.aippLogService = aippLogService;
        this.mapService = mapService;
        this.hllmReadTimeout = hllmReadTimeout;
    }

    @Fitable("com.huawei.fit.jober.aipp.fitable.LLMText2Text")
    @Override
    public List<Map<String, Object>> handleTask(List<Map<String, Object>> flowData) {
        Map<String, Object> businessData = Utils.getBusiness(flowData);
        log.debug("LLMText2Text businessData {}", businessData);

        String msg = "根据信息，我决定调用大模型文生文工具";
        Utils.persistAippMsgLog(aippLogService, msg, flowData);

        OperationContext context = Utils.getOpContext(businessData);
        String prompt = Utils.getPromptFromFlowContext(flowData);
        Validation.notBlank(prompt, "prompt cannot be null");

        String modelName = (String) businessData.get(AippConst.BS_MODEL_NAME_KEY);
        LlmModelNameEnum model = LlmModelNameEnum.getLlmModelName(modelName);
        boolean logEnable = Utils.checkLogEnable(Utils.getContextData(flowData));
        String instId = (String) businessData.get(AippConst.BS_AIPP_INST_ID_KEY);
        String result = askModelRetryable(model, context.getW3Account(), prompt, logEnable, instId);
        Validation.notBlank(result,
                () -> new JobberException(ErrorCodes.UN_EXCEPTED_ERROR, "llm result cannot be blank"));

        msg = "以下是生成的文本信息：\n" + result;
        Utils.persistAippMsgLog(aippLogService, msg, flowData);
        if (logEnable) {
            mapService.remove(instId, AippConst.INST_TEXT2TEXT_KEY);
        }

        // add result
        businessData.put(AippConst.INST_TEXT2TEXT_KEY, result);
        InstanceDeclarationInfo info =
                InstanceDeclarationInfo.custom().putInfo(AippConst.INST_TEXT2TEXT_KEY, result).build();
        Utils.persistInstance(metaInstanceService, info, businessData, context);

        return flowData;
    }

    private String askModelRetryable(LlmModelNameEnum model, String w3Id, String prompt, boolean logEnable,
            String instanceId) {
        long retryCnt = 0L;
        while (true) {
            try {
                if (LlmModelNameEnum.XIAOHAI.getValue().equals(model.getValue())) {
                    return llmService.askXiaoHaiKnowledge(w3Id, prompt);
                }
                LlmModel llmModel = LlmModel.valueOf(model.getValue());
                if (!logEnable) {
                    return llmService.askModelWithText(prompt, llmModel);
                }
                boolean waitRet = llmService.askModelStreaming(prompt,
                        Utils.MAX_TEXT_LEN,
                        llmModel,
                        instanceId,
                        AippConst.INST_TEXT2TEXT_KEY).await(hllmReadTimeout, TimeUnit.MILLISECONDS);
                if (!waitRet) {
                    log.error("llm model timeout");
                    throw new IOException("llm model timeout");
                }
                return (String) mapService.get(instanceId, AippConst.INST_TEXT2TEXT_KEY);
            } catch (IOException e) {
                log.error("ask model fail.", e);
                ++retryCnt;
                if (retryCnt > ASK_MODEL_MAX_RETRY_TIMES) {
                    return null;
                }
                Utils.sleep(retryCnt * 2);
            }
        }
    }
}
