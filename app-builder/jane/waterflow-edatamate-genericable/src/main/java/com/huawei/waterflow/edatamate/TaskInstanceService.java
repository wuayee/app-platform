/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.waterflow.edatamate;

import com.huawei.fitframework.annotation.Genericable;

import java.util.Map;

/**
 * 任务实例相关genericable
 *
 * @author 杨祥宇
 * @since 2024/2/28
 */
public interface TaskInstanceService {
    /**
     * 任务实例完成后操作
     *
     * @param params 执行需要的参数
     * key: [sourceType, config, dataSetPath]
     */
    @Genericable(id = "afc63686857d47cab4343ea1847f769f")
    default void instanceFinishedTask(Map<String, Object> params) {
    }
}
