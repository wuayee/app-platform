/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.aipp.fitable;

import com.huawei.fit.jane.common.entity.OperationContext;
import com.huawei.fit.jober.FlowableService;
import com.huawei.fit.jober.aipp.common.exception.AippJsonDecodeException;
import com.huawei.fit.jober.aipp.constants.AippConst;
import com.huawei.fit.jober.aipp.dto.PptJsonDto;
import com.huawei.fit.jober.aipp.enums.LlmModelNameEnum;
import com.huawei.fit.jober.aipp.service.AippLogService;
import com.huawei.fit.jober.aipp.service.LLMService;
import com.huawei.fit.jober.aipp.util.DataUtils;
import com.huawei.fit.jober.aipp.util.JsonUtils;
import com.huawei.fit.jober.aipp.util.LLMUtils;
import com.huawei.fit.jober.common.ErrorCodes;
import com.huawei.fit.jober.common.exceptions.JobberException;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.annotation.Fitable;
import com.huawei.fitframework.log.Logger;
import com.huawei.fitframework.util.ObjectUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * 标书文件由大模型分析并推荐产品，给出详细解释
 *
 * @author s00664640
 * @since 2024/05/10
 */
@Component
public class LlmText2PptJson implements FlowableService {
    private static final Logger log = Logger.get(LlmText2PptJson.class);
    private static final String TEXT2PPT_PROMPT = "\nPerform the following actions:\n"
            + "1. - Use chinese summarize the following text delimited by <> limit in 50 words.\n"
            + "2. - Splits the content of the input into multiple paragraphs based on semantics.\n"
            + "Finally output a json object.\n" + "\n" + "EXAMPLE:\n" + "Input: <dorado双活>\n" + "Output:\n"
            + "{\"title\":\"Dorado双活解决方案\",\"pages\":[{\"title\":\"介绍\","
            + "\"content\":\"Dorado双活解决方案旨在提供高可用性和连续的数据访问。\"},{\"title\":\"优势\","
            + "\"content\":\"该方案可以确保在数据中心故障时，业务连续性不会受到影响。\"}]}" + "--------\n"
            + "Reminder to ALWAYS respond with a valid json. Begin!\n" + "input: <%s>\n" + "Output:\n";

    private final LLMService llmService;
    private final AippLogService aippLogService;

    public LlmText2PptJson(LLMService llmService, AippLogService aippLogService) {
        this.llmService = llmService;
        this.aippLogService = aippLogService;
    }

    /**
     * 根据文本生成ppt json
     *
     * @param flowData 流程执行上下文数据
     * @return flowData
     */
    @Override
    @Fitable("com.huawei.fit.jober.aipp.fitable.LLMText2PptJson")
    public List<Map<String, Object>> handleTask(List<Map<String, Object>> flowData) {
        Map<String, Object> businessData = DataUtils.getBusiness(flowData);
        log.debug("LLMText2PptJson businessData {}", businessData);

        String msg = "首先，我需要根据你提交的信息，选择一个合适的报告模板，并生成报告内容";
        this.aippLogService.insertMsgLog(msg, flowData);

        OperationContext context = DataUtils.getOpContext(businessData);
        String operator = context.getOperator();
        String text = ObjectUtils.cast(businessData.get(AippConst.BS_TEXT_GENERATE_PPT_JSON_KEY));
        PptJsonDto pptJsonDto = text2pptJson(text, operator);
        this.aippLogService.insertMsgLog(formatPptContentToMsg(pptJsonDto), flowData);

        String result = JsonUtils.toJsonString(pptJsonDto);

        msg = "基于以上信息，我决定调用ppt生成工具\n正在为您生成述职报告";
        this.aippLogService.insertMsgLog(msg, flowData);
        businessData.put(AippConst.BS_PPT_JSON_RESULT, result);
        return flowData;
    }

    private PptJsonDto text2pptJson(String text, String operator) throws JobberException {
        final int maxToken = 20000;
        String question = String.format(Locale.ROOT, TEXT2PPT_PROMPT, text);
        try {
            String answer = LLMUtils.askModelForJson(llmService, question, maxToken, LlmModelNameEnum.QWEN_72B);
            PptJsonDto dto = JsonUtils.parseObject(answer, PptJsonDto.class);
            dto.setAuthor(operator);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            dto.setDate(dateFormat.format(new Date()));

            return dto;
        } catch (AippJsonDecodeException e) {
            log.error("llm service failed", e);
        }
        throw new JobberException(ErrorCodes.UN_EXCEPTED_ERROR, "llm service failed");
    }

    private String formatPptContentToMsg(PptJsonDto dto) {
        StringBuilder sBuilder = new StringBuilder("以下是生成的报告内容");
        for (int i = 0; i < dto.getPages().size(); ++i) {
            String slide = String.format(Locale.ROOT,
                    "\n \nslide %d\n标题：%s\n内容：%s",
                    i + 1,
                    dto.getPages().get(i).getTitle(),
                    dto.getPages().get(i).getContent());
            sBuilder.append(slide);
        }
        return sBuilder.toString();
    }
}
