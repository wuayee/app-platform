/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.store.service;

import modelengine.fitframework.annotation.Genericable;
import modelengine.jade.store.entity.query.ModelQuery;
import modelengine.jade.store.entity.query.TaskQuery;
import modelengine.jade.store.entity.transfer.ModelData;
import modelengine.jade.store.entity.transfer.TaskData;

import java.util.List;

/**
 * 模型的服务接口类。
 *
 * @author 鲁为
 * @since 2024-06-07
 */
public interface HuggingFaceModelService {
    /**
     * 根据动态条件准确查询模型列表。
     *
     * @param modelQuery 表示动态查询条件的 {@link TaskQuery}
     * @return 表示工具列表的 {@link List}{@code <}{@link TaskData}{@code >}。
     */
    @Genericable(id = "modelengine.jade.store.model.getModels.byTaskId")
    List<ModelData> getModels(ModelQuery modelQuery);

    /**
     * 根据任务唯一标识获取模型的总数。
     *
     * @param taskName 表示任务唯一标识名的  {@link String}。
     * @return 表示模型总数的 {@code int}。
     */
    @Genericable(id = "modelengine.jade.store.model.getCount")
    int getCount(String taskName);
}
