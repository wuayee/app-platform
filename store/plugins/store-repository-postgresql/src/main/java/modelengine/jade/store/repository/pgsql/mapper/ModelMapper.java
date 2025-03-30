/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.store.repository.pgsql.mapper;

import modelengine.jade.store.entity.query.ModelQuery;
import modelengine.jade.store.repository.pgsql.entity.ModelDo;

import java.util.List;

/**
 * 表示用于 MyBatis 持久层引用的 Model 接口。
 *
 * @author 鲁为
 * @since 2024-06-07
 */
public interface ModelMapper {
    /**
     * 根据任务唯一标识分页查询模型。
     *
     * @param modelQuery 表示模型查询类的 {@link String}。
     * @return 所有任务的实体类的实例的 {@link List}{@code <}{@link ModelDo}{@code >}。
     */
    List<ModelDo> getModels(ModelQuery modelQuery);

    /**
     * 根据任务唯一标识获取模型的总数。
     *
     * @param taskName 表示任务唯一标识名的  {@link String}。
     * @return 表示模型总数的 {@code int}。
     */
    int getCount(String taskName);
}
