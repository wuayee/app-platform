/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fel.plugin.huggingface.mapper;

import modelengine.fel.plugin.huggingface.entity.HuggingfaceTaskEntity;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 表示 Huggingface 任务持久层接口。
 *
 * @author 何嘉斌
 * @author 邱晓霞
 * @since 2024-09-09
 */
@Mapper
public interface HuggingfaceTaskMapper {
    /**
     * 增加 Huggingface 任务的支持模型数量。
     *
     * @param taskId 表示 Huggingface 任务唯一标识的 {@link Long}。
     */
    void increaseModelCount(Long taskId);

    /**
     * 查询所有可用的 Huggingface 任务。
     *
     * @return 所有 Huggingface 任务的实体类的实例的 {@link List}{@code <}{@link HuggingfaceTaskEntity}{@code >}。
     */
    List<HuggingfaceTaskEntity> listAvailableTasks();
}