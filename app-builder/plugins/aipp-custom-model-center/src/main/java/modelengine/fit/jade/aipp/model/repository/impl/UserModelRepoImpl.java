/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.model.repository.impl;

import modelengine.fit.jade.aipp.model.mapper.ModelMapper;
import modelengine.fit.jade.aipp.model.mapper.UserModelMapper;
import modelengine.fit.jade.aipp.model.po.ModelAccessPo;
import modelengine.fit.jade.aipp.model.po.ModelPo;
import modelengine.fit.jade.aipp.model.po.UserModelPo;
import modelengine.fit.jade.aipp.model.repository.UserModelRepo;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 表示用户模型信息的持久化层的接口 {@link UserModelRepo} 的实现。
 *
 * @author lixin
 * @since 2025/3/11
 */
@Component
public class UserModelRepoImpl implements UserModelRepo {
    private final ModelMapper modelMapper;
    private final UserModelMapper userModelMapper;

    public UserModelRepoImpl(ModelMapper modelMapper, UserModelMapper userModelMapper) {
        this.modelMapper = modelMapper;
        this.userModelMapper = userModelMapper;
    }

    @Override
    public List<ModelPo> get(String userId) {
        return this.getModelPos(this.userModelMapper.listUserModels(userId));
    }

    private List<ModelPo> getModelPos(List<UserModelPo> userModelPos) {
        if (CollectionUtils.isEmpty(userModelPos)) {
            return Collections.emptyList();
        }
        return this.modelMapper.listModels(userModelPos.stream()
                .map(UserModelPo::getModelId)
                .collect(Collectors.toList()));
    }

    @Override
    public ModelAccessPo getModelAccessInfo(String userId, String tag, String name) {
        List<UserModelPo> userModelPos = this.userModelMapper.listUserModels(userId);
        ModelPo modelPo = this.getModelPos(userModelPos)
                .stream()
                .filter(mp -> Objects.equals(mp.getTag(), tag) && Objects.equals(mp.getName(), name))
                .findFirst()
                .orElse(null);
        if (modelPo == null) {
            return null;
        }
        String apiKey = userModelPos.stream()
                .filter(um -> Objects.equals(um.getModelId(), modelPo.getModelId()))
                .map(UserModelPo::getApiKey)
                .findFirst()
                .orElse(null);
        return ModelAccessPo.builder().modelPO(modelPo).apiKey(apiKey).build();
    }

    @Override
    public ModelPo getDefaultModel(String userId) {
        UserModelPo defaultModel = this.userModelMapper.getDefault(userId);
        if (defaultModel == null) {
            return null;
        }
        return this.modelMapper.get(defaultModel.getModelId());
    }
}
