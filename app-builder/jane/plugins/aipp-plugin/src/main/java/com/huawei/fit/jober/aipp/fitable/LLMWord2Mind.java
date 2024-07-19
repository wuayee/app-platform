/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.aipp.fitable;

import com.huawei.fit.jane.meta.multiversion.MetaInstanceService;
import com.huawei.fit.jane.meta.multiversion.instance.InstanceDeclarationInfo;
import com.huawei.fit.jober.FlowableService;
import com.huawei.fit.jober.aipp.common.exception.AippJsonDecodeException;
import com.huawei.fit.jober.aipp.constants.AippConst;
import com.huawei.fit.jober.aipp.enums.LlmModelNameEnum;
import com.huawei.fit.jober.aipp.service.AippLogService;
import com.huawei.fit.jober.aipp.service.LLMService;
import com.huawei.fit.jober.aipp.service.OperatorService;
import com.huawei.fit.jober.aipp.util.AippFileUtils;
import com.huawei.fit.jober.aipp.util.AippStringUtils;
import com.huawei.fit.jober.aipp.util.DataUtils;
import com.huawei.fit.jober.aipp.util.LLMUtils;
import com.huawei.fit.jober.aipp.util.MetaInstanceUtils;
import com.huawei.fit.jober.common.ErrorCodes;
import com.huawei.fit.jober.common.exceptions.JobberException;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.annotation.Fit;
import com.huawei.fitframework.annotation.Fitable;
import com.huawei.fitframework.log.Logger;

import java.io.File;
import java.nio.file.Paths;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * word文档生成脑图json
 */
@Component
public class LLMWord2Mind implements FlowableService {

    private static final Logger log = Logger.get(LLMWord2Mind.class);
    private final LLMService llmService;
    private final MetaInstanceService metaInstanceService;
    private final OperatorService operatorService;
    private final AippLogService aippLogService;

    public LLMWord2Mind(@Fit LLMService llmService, @Fit MetaInstanceService metaInstanceService,
            @Fit OperatorService operatorService, AippLogService aippLogService) {
        this.llmService = llmService;
        this.metaInstanceService = metaInstanceService;
        this.operatorService = operatorService;
        this.aippLogService = aippLogService;
    }

    private static String getMindPrompt() {
        return "\n<%s>\n\nPlease parse catalogue delimited by <> to a json object.\n"
                + "EXAMPLE ONE\nInput: <eDataMate 23.0.0 系统需求分析说明书\n1 目的\n2 范围\n>"
                + "Output JSON: {\"name\": \"eDataMate 23.0.0 系统需求分析说明书\", \"children\": ["
                + "{\"name\": \"目的\", \"children\": []}, {\"name\": \"范围\", \"children\": []}]}\n\n"
                + "EXAMPLE TWO\nInput: <目 录\n1 功能性需求分析\n1.1 需求名称：模型管理\n>"
                + "Output JSON: {\"name\": \"目 录\", \"children\": ["
                + "{\"name\": \"功能性需求分析\", \"children\": [{\"name\": \"需求名称：模型管理\", \"children\": []}]}]}\n\n"
                + "EXAMPLE THREE\nInput: <修订记录\n1 目的\n \n2 范围\n>"
                + "Output JSON: {\"name\": \"修订记录\", \"children\": ["
                + "{\"name\": \"目的\", \"children\": []}, {\"name\": \"范围\", \"children\": []}]}\n\n"
                + "Reminder to ALWAYS respond with a valid json. Begin!\n--------\nOutput JSON: ";
    }

    @Fitable("com.huawei.fit.jober.aipp.fitable.LLMWord2Mind")
    @Override
    public List<Map<String, Object>> handleTask(List<Map<String, Object>> flowData) {
        Map<String, Object> businessData = DataUtils.getBusiness(flowData);
        log.debug("LLMWord2Mind business data {}", businessData);

        String msg = "首先我需要了解文件中的关键信息，我决定调用word文档信息提取工具";
        this.aippLogService.insertMsgLog(msg, flowData);

        String fileName = DataUtils.getFilePath(businessData, AippConst.BS_FILE_PATH_KEY);
        File file = Paths.get(AippFileUtils.NAS_SHARE_DIR, fileName).toFile();
        String outline = operatorService.outlineExtractor(file, OperatorService.FileType.WORD);
        if (outline.isEmpty()) {
            msg = "很抱歉！无法识别文件中的内容，您可以尝试换个文件";
            this.aippLogService.insertErrorLog(msg, flowData);
            throw new JobberException(ErrorCodes.UN_EXCEPTED_ERROR, "outline extract result is empty.");
        }
        outline = AippStringUtils.outlineLenLimit(outline);
        msg = "以下是文件中的关键信息：\n" + outline;
        this.aippLogService.insertMsgLog(msg, flowData);

        msg = "基于以上信息，我决定调用Elsa Transformer智能体，为您生成脑图";
        this.aippLogService.insertMsgLog(msg, flowData);

        String result = askModelGenerateMind(outline);
        businessData.put(AippConst.INST_WORD2MIND_KEY, result);

        InstanceDeclarationInfo info =
                InstanceDeclarationInfo.custom().putInfo(AippConst.INST_WORD2MIND_KEY, result).build();
        MetaInstanceUtils.persistInstance(
                metaInstanceService, info, businessData, DataUtils.getOpContext(businessData));

        return flowData;
    }

    private String askModelGenerateMind(String input) throws JobberException {
        final int maxTokens = 20000;
        String prompt = String.format(Locale.ROOT, getMindPrompt(), input);
        log.warn("debug generate mind {}", prompt);
        try {
            return LLMUtils.askModelForJson(llmService, prompt, maxTokens, LlmModelNameEnum.QWEN_72B);
        } catch (AippJsonDecodeException e) {
            log.error("generate mind json fail.", e);
        }
        throw new JobberException(ErrorCodes.UN_EXCEPTED_ERROR, "outline llm result is empty.");
    }
}
