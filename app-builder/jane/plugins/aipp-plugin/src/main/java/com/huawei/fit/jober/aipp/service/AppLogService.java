/*
 *  Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.service;

import com.huawei.fit.jane.common.entity.OperationContext;
import com.huawei.fitframework.annotation.Genericable;

import java.util.List;
import java.util.Map;

/**
 * 开始节点历史记录选择Genericable
 *
 * @author 孙怡菲 s00664640
 * @since 2024-04-20
 */
public interface AppLogService {
    @Genericable(id = "68dc66a6185cf64c801e55c97fc500e4")
    List<Map<String, Object>> getCustomizedLogs(Map<String, Object> params, String aippId, String aippType,
            OperationContext context);
}
