/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.model.service.impl;

import modelengine.fit.jade.aipp.model.dto.ModelAccessInfo;
import modelengine.fit.jade.aipp.model.dto.ModelListDto;
import modelengine.fit.jade.aipp.model.service.AippModelCenter;
import modelengine.fit.jade.aipp.model.service.AippModelCenterExtension;
import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Value;
import modelengine.fitframework.inspection.Validation;
import modelengine.fitframework.ioc.BeanContainer;
import modelengine.fitframework.ioc.BeanFactory;

import java.util.Objects;

/**
 * {@link AippModelCenter} 的代理实现。
 *
 * @author songyongtan
 * @since 2025-03-17
 */
@Component
public class AippModelCenterProxy implements AippModelCenter {
    private final AippModelCenterExtension target;

    /**
     * 构造函数。
     *
     * @param container 表示 bean 容器的 {@link BeanContainer}。
     * @param modelCenterName 表示注入 bean 名称的 {@link String}。
     */
    public AippModelCenterProxy(BeanContainer container, @Value("${model-center}") String modelCenterName) {
        this.target = container.all(AippModelCenterExtension.class)
                .stream()
                .filter(beanFactory -> Objects.equals(beanFactory.metadata().name(), modelCenterName))
                .map(BeanFactory::<AippModelCenterExtension>get)
                .findFirst()
                .orElse(null);
        Validation.notNull(this.target, String.format("Aipp model center (%s) not found.", modelCenterName));
    }

    @Override
    public ModelListDto fetchModelList(String type, String scene, OperationContext context) {
        return this.target.fetchModelList(type, scene, context);
    }

    @Override
    public ModelAccessInfo getModelAccessInfo(String tag, String modelName, OperationContext context) {
        return this.target.getModelAccessInfo(tag, modelName, context);
    }

    @Override
    public ModelAccessInfo getDefaultModel(String type, OperationContext context) {
        return this.target.getDefaultModel(type, context);
    }
}
