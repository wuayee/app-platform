/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.store.repository.pgsql.mapper;

import com.huawei.jade.store.entity.query.ModelQuery;
import com.huawei.jade.store.repository.pgsql.entity.ModelDo;

import java.util.List;

/**
 * 表示用于 MyBatis 持久层引用的 Model 接口。
 *
 * @author 鲁为 l00839724
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
     * 获取模型的总数。
     *
     * @return 表示模型总数的 {@code int}。
     */
    int getCount(String taskName);
}
