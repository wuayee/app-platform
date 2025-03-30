/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

/**
 * AppEngine 模块ID 枚举类。
 *
 * @author 易文渊
 * @since 2024-07-20
 */

package modelengine.jade.common.model;

/**
 * 表示模块 ID 的枚举类。
 *
 * @author 兰宇晨
 * @since 2024-07-31
 */
public enum ModelId {
    /**
     * 知识库模块唯一标识。
     */
    KNOWLEDGE_MODEL_ID(7),

    /**
     * 应用评估模块唯一标识。
     */
    APP_EVAL_MODEL_ID(8),

    /**
     * 插件模块唯一标识。
     */
    STORE_MODEL_ID(9),

    /**
     * 应用编排模块唯一标识。
     */
    APP_BUILDER_MODEL_ID(10);


    ModelId(int modelId) {
        this.modelId = modelId;
    }

    private final int modelId;


    /**
     * 获取模块唯一标识。
     *
     * @return 表示模块唯一标识的 {@code int}。
     */
    public int getModelId() {
        return this.modelId;
    }
}
