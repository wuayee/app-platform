/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.store.service;

import com.huawei.fitframework.annotation.Genericable;
import com.huawei.jade.store.entity.query.ModelQuery;
import com.huawei.jade.store.entity.query.TaskQuery;
import com.huawei.jade.store.entity.transfer.ModelData;
import com.huawei.jade.store.entity.transfer.TaskData;

import java.util.List;

/**
 * 模型的服务接口类。
 *
 * @author 鲁为 l00839724
 * @since 2024-06-07
 */
public interface HuggingFaceModelService {
    /**
     * 根据动态条件准确查询模型列表。
     *
     * @param modelQuery 表示动态查询条件的 {@link TaskQuery}
     * @return 表示工具列表的 {@link List}{@code <}{@link TaskData}{@code >}。
     */
    @Genericable(id = "com.huawei.jade.store.model.getModels.byTaskId")
    List<ModelData> getModels(ModelQuery modelQuery);
}
