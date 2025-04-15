/*
 *  Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fit.jober.aipp.service;

import modelengine.fit.jane.common.entity.OperationContext;

import modelengine.fitframework.annotation.Genericable;

import java.util.List;
import java.util.Map;

/**
 * 开始节点历史记录选择Genericable
 *
 * @author 孙怡菲
 * @since 2024-04-20
 */
public interface AppLogService {
    /**
     * 获取自定义日志
     *
     * @param params 参数
     * @param aippId 应用Id
     * @param aippType 应用类型
     * @param context 操作上下文
     * @return 返回自定义日志列表
     * @author 孙怡菲
     * @since 2024-04-20
     */
    @Genericable(id = "68dc66a6185cf64c801e55c97fc500e4")
    List<Map<String, Object>> getCustomizedLogs(Map<String, Object> params, String aippId, String aippType,
            OperationContext context);
}
