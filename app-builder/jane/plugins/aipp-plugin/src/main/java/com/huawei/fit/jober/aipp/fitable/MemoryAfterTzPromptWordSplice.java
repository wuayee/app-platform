/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.fitable;

import static com.huawei.fit.jober.aipp.service.impl.AippRunTimeServiceImpl.getLogMaps;

import com.huawei.fit.jane.common.entity.OperationContext;
import com.huawei.fit.jober.aipp.dto.aipplog.AippInstLogDataDto;
import com.huawei.fit.jober.aipp.service.AippLogService;
import com.huawei.fit.jober.aipp.service.AppLogService;

import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Fitable;

import java.util.List;
import java.util.Map;

/**
 * 获取经过天舟提示词拼接工具后的Memory.
 *
 * @author 晏钰坤
 * @since 2024/6/6
 */
@Component
public class MemoryAfterTzPromptWordSplice implements AppLogService {
    private final AippLogService aippLogService;

    public MemoryAfterTzPromptWordSplice(AippLogService aippLogService) {
        this.aippLogService = aippLogService;
    }

    @Override
    @Fitable(id = "com.huawei.fit.jober.aipp.fitable.MemoryAfterTzPromptWordSplice")
    public List<Map<String, Object>> getCustomizedLogs(Map<String, Object> params, String aippId, String aippType,
        OperationContext context) {
        List<AippInstLogDataDto> logs = this.aippLogService.queryAippRecentInstLogAfterSplice(aippId, aippType, 5,
            context);
        return getLogMaps(logs);
    }
}
