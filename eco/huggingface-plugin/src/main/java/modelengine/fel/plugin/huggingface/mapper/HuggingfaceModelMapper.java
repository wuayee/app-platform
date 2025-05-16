/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fel.plugin.huggingface.mapper;

import modelengine.fel.plugin.huggingface.dto.HuggingfaceModelQueryParam;
import modelengine.fel.plugin.huggingface.entity.HuggingfaceModelEntity;
import modelengine.fel.plugin.huggingface.po.HuggingfaceModelPo;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 表示 Huggingface 模型持久层接口。
 *
 * @author 何嘉斌
 * @author 邱晓霞
 * @since 2024-09-10
 */
@Mapper
public interface HuggingfaceModelMapper {
    /**
     * 创建 Huggingface 模型。
     *
     * @param po 表示 Huggingface 模型数据的 {@link HuggingfaceModelPo}。
     */
    void insert(HuggingfaceModelPo po);

    /**
     * 分页查询 Huggingface 任务模型实体。
     *
     * @param modelQueryParam 表示分页查询参数 {@link HuggingfaceModelQueryParam}。
     * @return 所有可用 Huggingface 任务模型实体类的 {@link List}{@code <}{@link HuggingfaceModelEntity}{@code >}。
     */
    List<HuggingfaceModelEntity> listModelPartialInfo(HuggingfaceModelQueryParam modelQueryParam);

    /**
     * 查询指定 Huggingface 任务模型数量。
     *
     * @param modelQueryParam 表示查询参数 {@link HuggingfaceModelQueryParam}。
     * @return 表示指定 Huggingface 任务模型数量的 {@code int}。
     */
    int countModel(HuggingfaceModelQueryParam modelQueryParam);
}