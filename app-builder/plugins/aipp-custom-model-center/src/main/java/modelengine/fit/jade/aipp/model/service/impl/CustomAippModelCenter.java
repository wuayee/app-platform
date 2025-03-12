/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/


package modelengine.fit.jade.aipp.model.service.impl;

import modelengine.fit.jade.aipp.model.dto.ModelAccessInfo;
import modelengine.fit.jade.aipp.model.dto.ModelListDto;
import modelengine.fit.jade.aipp.model.po.ModelAccessPo;
import modelengine.fit.jade.aipp.model.po.ModelPo;
import modelengine.fit.jade.aipp.model.repository.UserModelRepo;
import modelengine.fit.jade.aipp.model.service.AippModelCenter;
import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 自定义的模型中心服务实现。
 *
 * @author songyongtan
 * @since 2025/3/6
 */
@Component("customAippModelCenter")
public class CustomAippModelCenter implements AippModelCenter {
    private final UserModelRepo userModelRepo;

    private final AippModelCenter defaultModelCenter;

    /**
     * 构造方法。
     *
     * @param userModelRepo repo 层。
     * @param defaultModelCenter 默认实现，找不到时会使用默认的进行兜底。
     */
    public CustomAippModelCenter(UserModelRepo userModelRepo,
            @Fit(alias = "defaultAippModelCenter") AippModelCenter defaultModelCenter) {
        this.userModelRepo = userModelRepo;
        this.defaultModelCenter = defaultModelCenter;
    }

    @Override
    public ModelListDto fetchModelList(String type, OperationContext context) {
        List<ModelPo> modelList = this.userModelRepo.get(context.getOperator());
        if (CollectionUtils.isEmpty(modelList)) {
            return this.defaultModelCenter.fetchModelList(type, context);
        }
        List<ModelAccessInfo> modelDtoList = modelList.stream()
                .map(po -> ModelAccessInfo.builder()
                        .serviceName(po.getName())
                        .baseUrl(po.getBaseUrl())
                        .tag(po.getTag())
                        .build())
                .collect(Collectors.toList());
        return ModelListDto.builder().models(modelDtoList).total(modelDtoList.size()).build();
    }

    @Override
    public ModelAccessInfo getModelAccessInfo(String tag, String modelName, OperationContext context) {
        ModelAccessPo accessInfo = this.userModelRepo.getModelAccessInfo(context.getOperator(), tag, modelName);
        if (accessInfo == null) {
            return this.defaultModelCenter.getModelAccessInfo(tag, modelName, context);
        }
        return ModelAccessInfo.builder()
                .serviceName(accessInfo.getModelPO().getName())
                .baseUrl(accessInfo.getModelPO().getBaseUrl())
                .tag(accessInfo.getModelPO().getTag())
                .accessKey(accessInfo.getApiKey())
                .build();
    }

    @Override
    public ModelAccessInfo getDefaultModel(String type, OperationContext context) {
        ModelPo defaultModel = this.userModelRepo.getDefaultModel(context.getOperator());
        if (defaultModel == null) {
            return this.defaultModelCenter.getDefaultModel(type, context);
        }
        return ModelAccessInfo.builder()
                .serviceName(defaultModel.getName())
                .baseUrl(defaultModel.getBaseUrl())
                .tag(defaultModel.getTag())
                .build();
    }
}
