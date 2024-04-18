/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.store;

/**
 * 表示工作流工具的接口。
 *
 * @author 王攀博
 * @since 2024-04-18
 */
public interface WorkflowTool extends Tool {
    /**
     * 判断当前工作流中是否存在人工干预节点。
     *
     * @return 如果当前工作流中存在人工干预节点，则返回 {@code true}，否则，返回 {@code false}。
     */
    boolean isManualIntervention();
}
