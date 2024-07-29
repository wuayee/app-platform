/*
 *  Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.fitable;

import com.huawei.fit.jane.common.entity.OperationContext;
import com.huawei.fit.jober.aipp.dto.aipplog.AippInstLogDataDto;
import com.huawei.fit.jober.aipp.enums.AippInstLogType;
import com.huawei.fit.jober.aipp.service.AippLogService;
import com.huawei.fit.jober.aipp.service.AppInspirationService;
import com.huawei.fit.jober.aipp.util.JsonUtils;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.annotation.Fitable;
import com.huawei.fitframework.log.Logger;
import com.huawei.fitframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 灵感大全获取历史记录表单中的QA对
 *
 * @author 孙怡菲 s00664640
 * @since 2024-04-25
 */
@Component
public class GetQaFromLog implements AppInspirationService {
    private static final Logger log = Logger.get(GetQaFromLog.class);

    private final AippLogService aippLogService;

    public GetQaFromLog(AippLogService aippLogService) {
        this.aippLogService = aippLogService;
    }

    @Override
    @Fitable(id = "GetQAFromLog")
    public List<Map<String, Object>> getCustomizedLogs(Map<String, Object> params, String aippId,
            String appType, OperationContext context) {
        List<AippInstLogDataDto> logs = aippLogService.queryRecentLogsSinceResume(aippId, appType, context);
        List<Map<String, Object>> res = new ArrayList<>();
        for (AippInstLogDataDto log : logs) {
            List<Map<String, Object>> qaList = new ArrayList<>();
            log.getInstanceLogBodies()
                    .stream()
                    .filter(l -> StringUtils.equals(l.getLogType(), AippInstLogType.FORM.name()))
                    .forEach(logBody -> {
                        extractQA(logBody, qaList);
                    });
            res.addAll(qaList);
        }
        return res;
    }

    private void extractQA(AippInstLogDataDto.AippInstanceLogBody logBody, List<Map<String, Object>> qaList) {
        Map<String, String> data = JsonUtils.parseObject(logBody.getLogData(), Map.class);
        String formArgsStr = data.get("form_args");
        if (StringUtils.isEmpty(formArgsStr)) {
            return;
        }
        Map<String, Object> formArgs = JsonUtils.parseObject(formArgsStr, Map.class);
        if (!formArgs.containsKey("interviewResult")) {
            return;
        }
        if (formArgs.get("interviewResult") instanceof List) {
            qaList.addAll((List<Map<String, Object>>) formArgs.get("interviewResult"));
        }
    }
}
