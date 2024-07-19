/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.fitable;

import com.huawei.fit.jane.meta.multiversion.MetaInstanceService;
import com.huawei.fit.jane.meta.multiversion.instance.InstanceDeclarationInfo;
import com.huawei.fit.jober.FlowableService;
import com.huawei.fit.jober.aipp.constants.AippConst;
import com.huawei.fit.jober.aipp.enums.FileExtensionEnum;
import com.huawei.fit.jober.aipp.service.AippLogService;
import com.huawei.fit.jober.aipp.service.OperatorService;
import com.huawei.fit.jober.aipp.util.AippFileUtils;
import com.huawei.fit.jober.aipp.util.AippStringUtils;
import com.huawei.fit.jober.aipp.util.DataUtils;
import com.huawei.fit.jober.aipp.util.MetaInstanceUtils;
import com.huawei.fit.jober.common.ErrorCodes;
import com.huawei.fit.jober.common.exceptions.JobberException;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.annotation.Fitable;
import com.huawei.fitframework.log.Logger;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

/**
 * 任意文件读取
 */
@Component
public class CompositeFileReader implements FlowableService {
    private static final Logger log = Logger.get(CompositeFileReader.class);
    private final OperatorService operatorService;
    private final MetaInstanceService metaInstanceService;
    private final AippLogService aippLogService;

    public CompositeFileReader(OperatorService operatorService, MetaInstanceService metaInstanceService,
            AippLogService aippLogService) {
        this.operatorService = operatorService;
        this.metaInstanceService = metaInstanceService;
        this.aippLogService = aippLogService;
    }

    @Fitable("com.huawei.fit.jober.aipp.fitable.CompositeFileReader")
    @Override
    public List<Map<String, Object>> handleTask(List<Map<String, Object>> flowData) {
        Map<String, Object> businessData = DataUtils.getBusiness(flowData);
        log.debug("CompositeFileReader business data {}", businessData);
        String fileName = DataUtils.getFilePath(businessData, AippConst.BS_FILE_PATH_KEY);
        Path path = Paths.get(AippFileUtils.NAS_SHARE_DIR, fileName);
        File file = path.toFile();
        String extractResult = operatorService.fileExtractor(
                file, FileExtensionEnum.findType(fileName));
        if (extractResult.isEmpty()) {
            String msg = "很抱歉！无法识别文件中的内容，您可以尝试换个文件";
            this.aippLogService.insertErrorLog(msg, flowData);
            throw new JobberException(ErrorCodes.UN_EXCEPTED_ERROR, "text result is empty.");
        }
        extractResult = AippStringUtils.textLenLimit(extractResult,
            AippStringUtils.getIntegerFromStr((String) businessData.get(AippConst.BS_TEXT_LIMIT_KEY)));
        businessData.put(AippConst.INST_FILE2TEXT_KEY, extractResult);

        InstanceDeclarationInfo info =
                InstanceDeclarationInfo.custom().putInfo(AippConst.INST_FILE2TEXT_KEY, extractResult).build();
        MetaInstanceUtils.persistInstance(
                metaInstanceService, info, businessData, DataUtils.getOpContext(businessData));
        return flowData;
    }
}
