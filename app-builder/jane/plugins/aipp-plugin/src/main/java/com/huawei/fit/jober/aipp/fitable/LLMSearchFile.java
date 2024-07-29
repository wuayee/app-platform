/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.aipp.fitable;

import com.huawei.fit.jane.common.entity.OperationContext;
import com.huawei.fit.jane.meta.multiversion.MetaInstanceService;
import com.huawei.fit.jane.meta.multiversion.instance.InstanceDeclarationInfo;
import com.huawei.fit.jober.FlowableService;
import com.huawei.fit.jober.aipp.constants.AippConst;
import com.huawei.fit.jober.aipp.dto.xiaohai.FileDto;
import com.huawei.fit.jober.aipp.enums.LlmModelNameEnum;
import com.huawei.fit.jober.aipp.service.AippLogService;
import com.huawei.fit.jober.aipp.service.LLMService;
import com.huawei.fit.jober.aipp.util.DataUtils;
import com.huawei.fit.jober.aipp.util.JsonUtils;
import com.huawei.fit.jober.aipp.util.MetaInstanceUtils;
import com.huawei.fit.jober.common.ErrorCodes;
import com.huawei.fit.jober.common.exceptions.JobberException;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.annotation.Fitable;
import com.huawei.fitframework.inspection.Validation;
import com.huawei.fitframework.log.Logger;
import com.huawei.fitframework.util.ObjectUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * 调用小海查询文件接口
 *
 * @author s00664640
 * @since 2024/05/10
 */
@Component
public class LLMSearchFile implements FlowableService {
    private static final Logger log = Logger.get(LLMSearchFile.class);

    private final LLMService llmService;
    private final MetaInstanceService metaInstanceService;
    private final AippLogService aippLogService;

    public LLMSearchFile(LLMService llmService, MetaInstanceService metaInstanceService,
            AippLogService aippLogService) {
        this.llmService = llmService;
        this.metaInstanceService = metaInstanceService;
        this.aippLogService = aippLogService;
    }

    private void validationResult(List<FileDto> fileDtos, List<Map<String, Object>> flowData) {
        if (!fileDtos.isEmpty()) {
            return;
        }
        String msg = "很抱歉！根据已知信息，无法匹配到相关的数存知识，您可以尝试换个内容";
        this.aippLogService.insertErrorLog(msg, flowData);

        throw new JobberException(ErrorCodes.UN_EXCEPTED_ERROR, "search result is empty.");
    }

    @Fitable("com.huawei.fit.jober.aipp.fitable.LLMSearchFile")
    @Override
    public List<Map<String, Object>> handleTask(List<Map<String, Object>> flowData) {
        Map<String, Object> businessData = DataUtils.getBusiness(flowData);
        log.debug("LLMSearchFile businessData {}", businessData);

        try {
            OperationContext context = DataUtils.getOpContext(businessData);
            String prompt = DataUtils.getPromptFromFlowContext(flowData);
            Validation.notBlank(prompt, "prompt cannot be null");

            String modelName = ObjectUtils.cast(businessData.get(AippConst.BS_MODEL_NAME_KEY));
            LlmModelNameEnum model = LlmModelNameEnum.getLlmModelName(modelName);
            if (!LlmModelNameEnum.XIAOHAI.getValue().equals(model.getValue())) {
                log.warn("invalid model({}) for LLMSearchFile; using default model({})",
                        model.getValue(),
                        LlmModelNameEnum.XIAOHAI.getValue());
            }
            List<FileDto> fileDtos = llmService.askXiaoHaiFile(context.getW3Account(), prompt);
            validationResult(fileDtos, flowData);
            String result = JsonUtils.toJsonString(fileDtos);
            businessData.put(AippConst.INST_RECOMMEND_DOC_KEY, result);

            InstanceDeclarationInfo info =
                    InstanceDeclarationInfo.custom().putInfo(AippConst.INST_RECOMMEND_DOC_KEY, result).build();
            MetaInstanceUtils.persistInstance(metaInstanceService, info, businessData, context);
        } catch (IOException e) {
            throw new JobberException(ErrorCodes.UN_EXCEPTED_ERROR,
                    String.format(Locale.ROOT,
                            "LLMSearchFile error:%s, stack:%s",
                            e.getMessage(),
                            Arrays.toString(e.getStackTrace())));
        }

        return flowData;
    }
}
