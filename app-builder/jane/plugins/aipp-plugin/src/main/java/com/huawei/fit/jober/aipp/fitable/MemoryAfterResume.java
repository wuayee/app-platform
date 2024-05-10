/*
 *  Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.fitable;

import static com.huawei.fit.jober.aipp.service.impl.AippRunTimeServiceImpl.getLogMaps;

import com.huawei.fit.jane.common.entity.OperationContext;
import com.huawei.fit.jober.aipp.dto.aipplog.AippInstLogDataDto;
import com.huawei.fit.jober.aipp.service.AippLogService;
import com.huawei.fit.jober.aipp.service.AppLogService;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.annotation.Fitable;

import java.util.List;
import java.util.Map;

/**
 * 获取简历之后的memory
 *
 * @author 孙怡菲 s00664640
 * @since 2024-04-21
 */
@Component
public class MemoryAfterResume implements AppLogService {
    private final AippLogService aippLogService;

    public MemoryAfterResume(AippLogService aippLogService) {
        this.aippLogService = aippLogService;
    }

    @Fitable(id = "MemoryAfterResume")
    @Override
    public List<Map<String, Object>> getCustomizedLogs(Map<String, Object> params, String aippId, String aippType,
            OperationContext context) {
        List<AippInstLogDataDto> logs = this.aippLogService.queryRecentLogsSinceResume(aippId, aippType, context);
        return getLogMaps(logs);
    }
}
