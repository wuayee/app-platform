/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fel.plugin.huggingface.service;

import modelengine.fel.plugin.huggingface.dto.HuggingfaceTaskInfo;

import java.util.List;

/**
 * 表示 Huggingface 任务查询服务。
 *
 * @author 邱晓霞
 * @since 2024-09-09
 */
public interface HuggingfaceTaskQueryService {
    /**
     * 查询可用任务列表数据。
     *
     * @return 表示可用任务列表的 {@link List}{@code <}{@link HuggingfaceTaskInfo}{@code >}。
     */
    List<HuggingfaceTaskInfo> listAvailableTasks();
}