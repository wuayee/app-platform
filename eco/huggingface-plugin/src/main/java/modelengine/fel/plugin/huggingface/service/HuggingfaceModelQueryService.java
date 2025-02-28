/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fel.plugin.huggingface.service;

import modelengine.jade.common.vo.PageVo;

import modelengine.fel.plugin.huggingface.dto.HuggingfaceModelQueryParam;
import modelengine.fel.plugin.huggingface.entity.HuggingfaceModelEntity;

/**
 * 表示 Huggingface 模型查询服务。
 *
 * @author 邱晓霞
 * @since 2024-09-10
 */
public interface HuggingfaceModelQueryService {
    /**
     * 分页查询 Huggingface 任务的模型元数据。
     *
     * @param modelQueryParam 表示分页查询参数 {@link HuggingfaceModelQueryParam}。
     * @return 表示包含 Huggingface 模型 ORM 对象分页数据对象的 {@link PageVo}{@code <}{@link HuggingfaceModelEntity}{@code >}。
     */
    PageVo<HuggingfaceModelEntity> listModelInfoQuery(HuggingfaceModelQueryParam modelQueryParam);
}
