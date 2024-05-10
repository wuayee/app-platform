/*
 *  Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.service;

import com.huawei.fit.jane.common.entity.OperationContext;
import com.huawei.fitframework.annotation.Genericable;

import java.util.List;
import java.util.Map;

/**
 * 灵感大全Genericable
 *
 * @author 孙怡菲 s00664640
 * @since 2024-04-25
 */
public interface AppInspirationService {
    @Genericable(id="d01041a73e00ac46bedde08d02c6818e")
    List<Map<String, Object>> getCustomizedLogs(Map<String, Object> params, String aippId, String appType, OperationContext context);
}
