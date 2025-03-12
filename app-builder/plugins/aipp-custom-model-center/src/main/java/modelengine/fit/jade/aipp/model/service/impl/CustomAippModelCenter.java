/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/


package modelengine.fit.jade.aipp.model.service.impl;

import modelengine.fit.jade.aipp.model.dto.ModelAccessInfo;
import modelengine.fit.jade.aipp.model.dto.ModelListDto;
import modelengine.fit.jade.aipp.model.service.AippModelCenter;
import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Fit;

/**
 * 自定义的模型中心服务实现。
 *
 * @author songyongtan
 * @since 2025/3/6
 */
@Component("customAippModelCenter")
public class CustomAippModelCenter implements AippModelCenter {
    private final AippModelCenter defaultModelCenter;

    public CustomAippModelCenter(@Fit(alias = "defaultAippModelCenter") AippModelCenter defaultModelCenter) {
        this.defaultModelCenter = defaultModelCenter;
    }

    @Override
    public ModelListDto fetchModelList(String type, OperationContext context) {
        return defaultModelCenter.fetchModelList(type, null);
    }

    @Override
    public String getModelBaseUrl(String tag) {
        return defaultModelCenter.getModelBaseUrl(tag);
    }

    @Override
    public ModelAccessInfo getDefaultModel(String type) {
        return defaultModelCenter.getDefaultModel(type);
    }
}
