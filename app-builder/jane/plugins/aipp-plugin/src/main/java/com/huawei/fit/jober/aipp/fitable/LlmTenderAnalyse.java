/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.aipp.fitable;

import com.huawei.fit.jober.FlowableService;
import com.huawei.fit.jober.aipp.constants.AippConst;
import com.huawei.fit.jober.aipp.dto.xiaohai.FileDto;
import com.huawei.fit.jober.aipp.enums.LlmModelNameEnum;
import com.huawei.fit.jober.aipp.service.AippLogService;
import com.huawei.fit.jober.aipp.service.LLMService;
import com.huawei.fit.jober.aipp.util.DataUtils;
import com.huawei.fit.jober.aipp.util.JsonUtils;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.annotation.Fitable;
import com.huawei.fitframework.annotation.Value;
import com.huawei.fitframework.log.Logger;
import com.huawei.fitframework.util.ObjectUtils;

import java.io.IOException;
import java.util.ArrayList;
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
public class LlmTenderAnalyse implements FlowableService {
    private static final Logger log = Logger.get(LlmTenderAnalyse.class);

    private final LLMService llmService;
    private final AippLogService aippLogService;
    private final String tenderUrl;

    public LlmTenderAnalyse(LLMService llmService, AippLogService aippLogService,
            @Value("${tender.url}") String tenderUrl) {
        this.llmService = llmService;
        this.aippLogService = aippLogService;
        this.tenderUrl = tenderUrl;
    }

    /**
     * 根据文本生成ppt json
     *
     * @param flowData 流程执行上下文数据
     * @return flowData
     */
    @Fitable("com.huawei.fit.jober.aipp.fitable.LLMTenderAnalyse")
    @Override
    public List<Map<String, Object>> handleTask(List<Map<String, Object>> flowData) {
        Map<String, Object> businessData = DataUtils.getBusiness(flowData);
        log.debug("LLMTenderAnalyse businessData {}", businessData);

        int windowSize = 1000;
        String text = ObjectUtils.cast(businessData.get(AippConst.INST_PDF2TEXT_KEY));
        String content = segmentation(text, windowSize);

        // 大模型关键信息提取
        String msg = "根据用户需求，我决定调用关键信息提取工具";
        this.aippLogService.insertMsgLog(msg, flowData);
        String prompt = "我希望你充当关键信息提取工具。我会用中文输入一段文本，同时告诉你几个关键词。"
                + "你会在文本中选择包含这几个关键词的一段话作为输出。"
                + "\n\n双引号内为该段文本:\n\"{text}\"\n\n三引号内的内容为关键词，"
                + "关键词间使用','隔开\n'''{控制器,硬盘,冗余,磁盘}'''\n\n"
                + "我不希望你回复除提取内容外的任何内容，请仅回复提取到的那段话。同时保留原文格式。\n"
                + "输出中删除包含页码与公司信息的内容。";
        prompt = prompt.replace("{text}", content).replace("★", "");
        String result = "";
        try {
            result = llmService.askModelWithText(prompt, 20000, 0.0, LlmModelNameEnum.QWEN_72B);
        } catch (IOException e) {
            log.error("ask model failed.", e);
        }

        // 提取信息展示
        msg = "以下是提取到的关键信息：\n" + result.replace("\"", "");
        this.aippLogService.insertMsgLog(msg, flowData);

        // 产品推荐流程
        msg = "根据信息中提到的>=16控，推荐如下产品系列：\nOceanStor 5310 \nOceanStor 5510 \nOceanStor Dorado 5300\n"
                + "OceanStor Dorado 5500";
        this.aippLogService.insertMsgLog(msg, flowData);
        msg =
                "考虑到需求中要求企业级热插拔 SAS 硬盘，单盘容量≥2.4T，未要求全闪存盘，推荐:\nOceanStor 5310\nOceanStor 5510";
        this.aippLogService.insertMsgLog(msg, flowData);
        msg =
                "同时考虑到需求中要求支持RAID 0、RAID 1、RAID 10、RAID50、RAID 5、RAID6，"
                        + " 推荐:\nOceanStor 5310 V5\nOceanStor 5510 V5";
        this.aippLogService.insertMsgLog(msg, flowData);
        msg = "其它需求为产品具体配置，不影响产品选型，综合推荐产品OceanStor 5310 V5";
        this.aippLogService.insertMsgLog(msg, flowData);
        msg = "针对产品及用户配置需求，生成配置表如下";
        this.aippLogService.insertMsgLog(msg, flowData);

        // 文件下载
        FileDto fileDto = new FileDto();
        String fileName = "tenderAnalyseResult.xlsx";
        String fileUrl = String.format(Locale.ROOT, "/file?fileUrl=%s&fileName=%s", this.tenderUrl, fileName);
        fileDto.setFileUrl(fileUrl);
        fileDto.setFileName(fileName);

        List<FileDto> fileDtos = new ArrayList<>();
        fileDtos.add(fileDto);
        result = JsonUtils.toJsonString(fileDtos);
        businessData.put("tenderAnalyseResult", result);

        return flowData;
    }

    /**
     * 处理大段文本获取包含关键字段落
     *
     * @param text 超长原文本
     * @param windowSize 窗口大小
     * @return 包含关键词段落
     */
    private String segmentation(String text, int windowSize) {
        String content = "";
        String keyword = "控制器";
        int startIdx = 0;
        int endIdx = windowSize;
        while (endIdx <= text.length()) {
            content = text.substring(startIdx, endIdx);
            if (content.contains(keyword)) {
                break;
            }
            startIdx += windowSize;
            endIdx += windowSize;
        }
        return content;
    }
}
