/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober;

import com.huawei.fitframework.annotation.Genericable;

import java.util.List;
import java.util.Map;

/**
 * 流程实例智能表单 Genericable。
 *
 * @author l00862071
 * @since 2023-12-13
 */
public interface FlowSmartFormService {
    /**
     * 智能表单处理
     *
     * @param contexts 流程上下文信息
     * @param sheetId 表单Id
     */
    @Genericable(id = "htctmizg0mydwnt2ttbbp8jlgo2e9e0w")
    void handleSmartForm(List<Map<String, Object>> contexts, String sheetId);
}
