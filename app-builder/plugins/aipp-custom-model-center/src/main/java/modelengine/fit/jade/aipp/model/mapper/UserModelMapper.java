/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.model.mapper;

import modelengine.fit.jade.aipp.model.po.UserModelPo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 表示用户模型关系信息持久层接口。
 *
 * @author lixin
 * @since 2025/3/11
 */
@Mapper
public interface UserModelMapper {
    /**
     * 根据用户标识获取用户模型关系列表。
     *
     * @param userId 表示用户标识。
     * @return 用户模型关系列表 {@link List}{@code <}{@link UserModelPo}{@code >}.
     */
    List<UserModelPo> listUserModels(String userId);

    /**
     * 根据用户标识获取默认用户模型关系。
     *
     * @param userId 表示用户标识。
     * @return 默认的用户模型关系。
     */
    UserModelPo getDefault(String userId);
}
