/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2025-2025. All rights reserved.
 */

package modelengine.fit.jade.aipp.model.service.impl;

import modelengine.fit.jade.aipp.model.dto.ModelListDto;
import modelengine.fit.jade.aipp.model.service.AippModelCenter;
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
    public ModelListDto fetchModelList() {
        return defaultModelCenter.fetchModelList();
    }

    @Override
    public String getModelBaseUrl(String tag) {
        return defaultModelCenter.getModelBaseUrl(tag);
    }
}
