/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fel.plugin.huggingface.service;

import modelengine.fel.plugin.huggingface.entity.HuggingfaceModelEntity;

/**
 * 表示 Huggingface 模型服务。
 *
 * @author 何嘉斌
 * @since 2024-09-09
 */
public interface HuggingfaceModelService {
    /**
     * 创建 Huggingface 模型。
     *
     * @param entity 表示 Huggingface 模型数据的 {@link HuggingfaceModelEntity}。
     */
    void insert(HuggingfaceModelEntity entity);
}