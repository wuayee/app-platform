/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.aipp.fitable;

import com.huawei.fit.jober.FlowableService;
import com.huawei.fit.jober.aipp.common.JsonUtils;
import com.huawei.fit.jober.aipp.common.Utils;
import com.huawei.fit.jober.aipp.constants.AippConst;
import com.huawei.fit.jober.aipp.dto.xiaohai.FileDto;
import com.huawei.fit.jober.aipp.service.AippLogService;
import com.huawei.fit.jober.aipp.service.LLMService;
import com.huawei.fit.jober.aipp.service.OperatorService;
import com.huawei.fit.jober.common.ErrorCodes;
import com.huawei.fit.jober.common.exceptions.JobberException;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.annotation.Fitable;
import com.huawei.hllm.model.LlmModel;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Component
@Slf4j
public class GenerateWordDoc implements FlowableService {
    private final AippLogService aippLogService;
    private final OperatorService operatorService;
    private final LLMService llmService;

    public GenerateWordDoc(AippLogService aippLogService, OperatorService operatorService, LLMService llmService) {
        this.aippLogService = aippLogService;
        this.operatorService = operatorService;
        this.llmService = llmService;
    }

    private String generateFileUrl(String filePath, String fileName) throws UnsupportedEncodingException {
        // 由前端拼接域名等信息
        return String.format(Locale.ROOT,
                "/file?filePath=%s&fileName=%s",
                URLEncoder.encode(filePath, "UTF-8"),
                URLEncoder.encode(fileName, "UTF-8"));
    }

    private String getFileResult(String docFileName, String downloadDocUrl) {
        FileDto fileDto = new FileDto();
        fileDto.setFileUrl(downloadDocUrl);
        fileDto.setFileName(docFileName);
        return JsonUtils.toJsonString(Collections.singletonList(fileDto));
    }

    /**
     * 生成doc文件并提供url下载
     *
     * @param flowData 流程执行上下文数据
     * @return flowData
     */
    @Fitable("com.huawei.fit.jober.aipp.fitable.GenerateWordDoc")
    @Override
    public List<Map<String, Object>> handleTask(List<Map<String, Object>> flowData) {
        Map<String, Object> businessData = Utils.getBusiness(flowData);
        log.debug("GenerateWordDoc businessData {}", businessData);

        String msg = "根据上面的信息，我决定调用word生成工具为您生成文档";
        Utils.persistAippMsgLog(aippLogService, msg, flowData);

        String instId = (String) businessData.get(AippConst.BS_AIPP_INST_ID_KEY);
        String toDocText = (String) businessData.get(AippConst.BS_TO_DOC_TEXT);
        try {
            String fileName = generateFileName(toDocText);
            File docFile = operatorService.createDoc(instId, fileName, toDocText);
            String downloadDocUrl = generateFileUrl(docFile.getCanonicalPath(), docFile.getName());
            log.info("downloadDocUrl={}", downloadDocUrl);
            businessData.put(AippConst.BS_DOWNLOAD_DOC_FILE_URL, getFileResult(docFile.getName(), downloadDocUrl));
        } catch (IOException e) {
            throw new JobberException(ErrorCodes.UN_EXCEPTED_ERROR,
                    String.format(Locale.ROOT,
                            "GenerateWordDoc failed error=%s, stack: %s",
                            e.getMessage(),
                            Arrays.toString(e.getStackTrace())));
        }
        return flowData;
    }

    private String generateFileName(String text) throws IOException {
        String prompt =
                "请根据如下内容生成一个不超过20个字的标题，这个标题必须满足Linux文件名要求。\n" + Utils.textLenLimit(text,
                        Utils.MAX_TEXT_LEN);
        String fileName = llmService.askModelWithText(prompt, LlmModel.QWEN_72B);
        log.info("generateFileName={}", fileName);
        // 去除双引号 单引号 空格
        return fileName.replace("\"", "").replace("'", "").replace(" ", "");
    }
}
