/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober;

import com.huawei.fitframework.annotation.Genericable;

import java.util.Map;

/**
 * 工具流相关接口
 *
 * @author l00498867
 * @since 2024/4/24
 */
public interface WaterFlowService {
    /**
     * 调用工具流统一入口
     *
     * @param tenantId 租户id
     * @param aippId 应用id
     * @param version 应用版本
     * @param inputParams 启动参数列表
     * @return 工具流实例id
     */
    @Genericable(id = "07b51bd246594c159d403164369ce1db")
    String invoke(String tenantId, String aippId, String version, Map<String, Object> inputParams);
}
