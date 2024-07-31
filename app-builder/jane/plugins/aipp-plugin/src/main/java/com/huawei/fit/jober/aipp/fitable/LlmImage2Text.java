/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.aipp.fitable;

import com.huawei.fit.jane.common.entity.OperationContext;
import com.huawei.fit.jane.meta.multiversion.MetaInstanceService;
import com.huawei.fit.jane.meta.multiversion.instance.InstanceDeclarationInfo;
import com.huawei.fit.jober.FlowableService;
import com.huawei.fit.jober.aipp.constants.AippConst;
import com.huawei.fit.jober.aipp.service.AippLogService;
import com.huawei.fit.jober.aipp.service.LLMService;
import com.huawei.fit.jober.aipp.util.AippFileUtils;
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

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * 调用大模型图生文接口
 *
 * @author s00664640
 * @since 2024/05/10
 */
@Component
public class LlmImage2Text implements FlowableService {
    private static final Logger log = Logger.get(LlmImage2Text.class);
    private static final String S3_URL = "s3Url";

    private final LLMService llmService;
    private final MetaInstanceService metaInstanceService;
    private final AippLogService aippLogService;

    public LlmImage2Text(LLMService llmService, MetaInstanceService metaInstanceService,
            AippLogService aippLogService) {
        this.llmService = llmService;
        this.metaInstanceService = metaInstanceService;
        this.aippLogService = aippLogService;
    }

    @Fitable("com.huawei.fit.jober.aipp.fitable.LLMImage2Text")
    @Override
    public List<Map<String, Object>> handleTask(List<Map<String, Object>> flowData) {
        Map<String, Object> businessData = DataUtils.getBusiness(flowData);
        log.debug("LLMImage2Text businessData {}", businessData);

        String imagePathStr = ObjectUtils.cast(businessData.get(AippConst.BS_IMAGE_PATH_KEY)) ;
        Map<String, Object> imagePathJson = JsonUtils.parseObject(imagePathStr);
        String imageS3Url = ObjectUtils.cast(imagePathJson.get(S3_URL));
        Validation.notNull(imageS3Url, "image path cannot be null");

        String msg = "首先我需要了解图片中的关键信息，我决定调用图片信息提取工具";
        this.aippLogService.insertMsgLog(msg, flowData);

        String prompt = DataUtils.getPromptFromFlowContext(flowData);
        String instId = ObjectUtils.cast(businessData.get(AippConst.BS_AIPP_INST_ID_KEY));
        File tmpImageFile = null;
        try {
            tmpImageFile = AippFileUtils.getFileFromS3(instId, imageS3Url, "img");
            String result = llmService.askModelWithImage(tmpImageFile, prompt);
            persistLlmResultLog(flowData, result);
            businessData.put(AippConst.BS_IMAGE_DESCRIPTION_KEY, result);

            // update instance
            OperationContext context = DataUtils.getOpContext(businessData);
            InstanceDeclarationInfo info = InstanceDeclarationInfo.custom()
                    .putInfo(AippConst.BS_IMAGE_DESCRIPTION_KEY, result)
                    .putInfo(AippConst.BS_IMAGE_PATH_KEY, imagePathStr)
                    .build();
            MetaInstanceUtils.persistInstance(metaInstanceService, info, businessData, context);
        } catch (IOException e) {
            throw new JobberException(ErrorCodes.UN_EXCEPTED_ERROR,
                    String.format(Locale.ROOT,
                            "askModelWithImage failed error=%s, stack: %s",
                            e.getMessage(),
                            Arrays.toString(e.getStackTrace())));
        } finally {
            // 删除临时图片文件
            AippFileUtils.deleteFile(tmpImageFile);
        }
        return flowData;
    }

    private void persistLlmResultLog(List<Map<String, Object>> flowData, String result) {
        if (result.isEmpty()) {
            String msg = "很抱歉！无法识别图片中的内容，您可以尝试换个图片";
            this.aippLogService.insertErrorLog(msg, flowData);
            throw new JobberException(ErrorCodes.UN_EXCEPTED_ERROR, "image text result is empty.");
        }
        this.aippLogService.insertMsgLog(result, flowData);
    }
}
