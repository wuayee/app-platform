/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober;

import com.huawei.fitframework.annotation.Genericable;

import java.util.List;
import java.util.Map;

/**
 * 流程实例异常 Genericable。
 *
 * @author 李哲峰
 * @since 2024-01-11
 */
public interface FlowExceptionService {
    /**
     * HANDLE_EXCEPTION_GENERICABLE
     */
    String HANDLE_EXCEPTION_GENERICABLE = "1b5ffv4ib16iui8ddizapuejgqtsjj59";

    /**
     * 异常回调实现
     *
     * @param nodeId 异常发生的节点Id
     * @param contexts 流程上下文
     * @param errorMessage 异常错误信息
     */
    @Genericable(id = HANDLE_EXCEPTION_GENERICABLE)
    void handleException(String nodeId, List<Map<String, Object>> contexts, String errorMessage);
}
