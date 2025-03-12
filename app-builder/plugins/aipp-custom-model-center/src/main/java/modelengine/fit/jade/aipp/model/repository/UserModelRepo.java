/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.model.repository;

import modelengine.fit.jade.aipp.model.po.ModelAccessPo;
import modelengine.fit.jade.aipp.model.po.ModelPo;

import java.util.List;

/**
 * 表示用户模型信息的持久化层的接口。
 *
 * @author lixin
 * @since 2025/3/11
 */
public interface UserModelRepo {
    /**
     * 根据用户标识来查询该用户可用的模型列表。
     *
     * @param userId 表示用户标识。
     * @return 表示该用户可用的模型列表 {@link List}{@code <}{@link ModelPo}{@code >}。
     */
    List<ModelPo> get(String userId);

    /**
     * 查询特定的模型访问信息。
     *
     * @param userId 表示用户标识。
     * @param tag 表示模型标签。
     * @param name 表示模型名称。
     * @return 模型访问信息 {@link ModelAccessPo}。
     */
    ModelAccessPo getModelAccessInfo(String userId, String tag, String name);

    /**
     * 获取一个用户的默认模型。
     *
     * @param userId 表示用户标识。
     * @return 模型信息 {@link ModelPo}.
     */
    ModelPo getDefaultModel(String userId);
}
