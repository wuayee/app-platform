/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.aipp.fitable;

import com.huawei.fit.jane.meta.multiversion.MetaInstanceService;
import com.huawei.fit.jane.meta.multiversion.instance.InstanceDeclarationInfo;
import com.huawei.fit.jober.FlowableService;
import com.huawei.fit.jober.aipp.common.Utils;
import com.huawei.fit.jober.aipp.constants.AippConst;
import com.huawei.fit.jober.aipp.service.AippLogService;
import com.huawei.fit.jober.aipp.service.OperatorService;
import com.huawei.fit.jober.common.ErrorCodes;
import com.huawei.fit.jober.common.exceptions.JobberException;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.annotation.Fitable;
import com.huawei.fitframework.log.Logger;

import java.io.File;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * pdf文件提取text并经大模型润色文字，提取关键信息
 */
@Component
public class LLMPdf2Text implements FlowableService {
    private static final Logger log = Logger.get(LLMPdf2Text.class);
    private final OperatorService operatorService;
    private final MetaInstanceService metaInstanceService;
    private final AippLogService aippLogService;

    public LLMPdf2Text(OperatorService operatorService, MetaInstanceService metaInstanceService,
            AippLogService aippLogService) {
        this.operatorService = operatorService;
        this.metaInstanceService = metaInstanceService;
        this.aippLogService = aippLogService;
    }

    @Fitable("com.huawei.fit.jober.aipp.fitable.LLMPdf2Text")
    @Override
    public List<Map<String, Object>> handleTask(List<Map<String, Object>> flowData) {
        Map<String, Object> businessData = Utils.getBusiness(flowData);
        log.debug("LLMPdf2Text businessData {}", businessData);

        String msg = "首先我需要了解文件中的关键信息，我决定调用pdf文档信息提取工具";
        Utils.persistAippMsgLog(aippLogService, msg, flowData);

        String fileName = Utils.getFilePath(businessData, AippConst.BS_PDF_PATH_KEY);
        File file = Paths.get(Utils.NAS_SHARE_DIR, fileName).toFile();
        String result = operatorService.fileExtractor(file, Optional.of(OperatorService.FileType.PDF));
        if (result.isEmpty()) {
            msg = "很抱歉！无法识别文件中的内容，您可以尝试换个文件";
            Utils.persistAippErrorLog(aippLogService, msg, flowData);
            throw new JobberException(ErrorCodes.UN_EXCEPTED_ERROR, "pdf2text result is empty.");
        }
        result = Utils.textLenLimit(result, Utils.getIntegerFromStr((String) businessData.get(AippConst.BS_TEXT_LIMIT_KEY)));
        businessData.put(AippConst.INST_PDF2TEXT_KEY, result);

        msg = "以下是文件中的关键信息：\n" + result;
        Utils.persistAippMsgLog(aippLogService, msg, flowData);

        InstanceDeclarationInfo info =
                InstanceDeclarationInfo.custom().putInfo(AippConst.INST_TEXT2TEXT_KEY, result).build();
        Utils.persistInstance(metaInstanceService, info, businessData, Utils.getOpContext(businessData));

        return flowData;
    }
}
