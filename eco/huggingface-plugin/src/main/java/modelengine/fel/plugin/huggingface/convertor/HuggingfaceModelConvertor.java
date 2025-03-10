/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fel.plugin.huggingface.convertor;

import modelengine.fel.plugin.huggingface.entity.HuggingfaceModelEntity;
import modelengine.fel.plugin.huggingface.po.HuggingfaceModelPo;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * 定义 Huggingface 模型的转换器接口。
 *
 * @author 何嘉斌
 * @since 2024-09-09
 */
@Mapper
public interface HuggingfaceModelConvertor {
    /**
     * 获取 HuggingfaceModelConvertor 的实现。
     */
    HuggingfaceModelConvertor INSTANCE = Mappers.getMapper(HuggingfaceModelConvertor.class);

    /**
     * 将 {@link HuggingfaceModelEntity} 转化为 {@link HuggingfaceModelPo}。
     *
     * @param entity 表示待转换的 {@link HuggingfaceModelEntity}。
     * @return 转换完成的 {@link HuggingfaceModelPo}。
     */
    HuggingfaceModelPo entityToPo(HuggingfaceModelEntity entity);
}