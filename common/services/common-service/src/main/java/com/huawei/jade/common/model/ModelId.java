/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

/**
 * AppEngine 模块ID 枚举类。
 *
 * @author 易文渊
 * @since 2024-07-20
 */

package com.huawei.jade.common.model;

/**
 * 表示模块 ID 的枚举类。
 *
 * @author 兰宇晨
 * @since 2024-07-31
 */
public enum ModelId {
    /**
     * 应用评估模块 ID
     */
    APP_EVAL_MODEL_ID(8),

    /**
     * 插件模块 ID
     */
    STORE_MODEL_ID(9);


    private ModelId(int modelId) {
        this.modelId = modelId;
    }

    private int modelId;


    /**
     * 获取模块 ID。
     *
     * @return 表示模块 ID的 {@code int}。
     */
    public int getModelId() {
        return this.modelId;
    }
}
