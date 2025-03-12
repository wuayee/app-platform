/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.model.repository.impl;

import modelengine.fit.jade.aipp.model.po.ModelAccessPo;
import modelengine.fit.jade.aipp.model.po.ModelPo;
import modelengine.fit.jade.aipp.model.repository.UserModelRepo;
import modelengine.fitframework.annotation.Component;

import java.util.List;


/**
 * 表示用户模型信息的持久化层的接口 {@link UserModelRepo} 的实现。
 *
 * @author lixin
 * @since 2025/3/11
 */
@Component
public class UserModelRepoImpl implements UserModelRepo {
    @Override
    public List<ModelPo> get(String userId) {
        return null;
    }

    @Override
    public ModelAccessPo getModelAccessInfo(String userId, String tag, String name) {
        return null;
    }

    @Override
    public ModelPo getDefaultModel(String userId) {
        return null;
    }
}
