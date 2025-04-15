/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fit.jober.aipp.validation.impl;

import modelengine.fit.jober.aipp.common.exception.AippErrCode;
import modelengine.fit.jober.aipp.common.exception.AippException;
import modelengine.fit.jober.aipp.domain.AppBuilderApp;
import modelengine.fit.jober.aipp.enums.AppState;
import modelengine.fit.jober.aipp.factory.AppBuilderAppFactory;
import modelengine.fit.jober.aipp.validation.AppUpdateValidator;
import modelengine.fitframework.annotation.Component;

/**
 * App 更新校验器
 *
 * @author 邬涨财
 * @since 2024-06-20
 */
@Component
public class AppUpdateValidatorImpl implements AppUpdateValidator {
    private final AppBuilderAppFactory factory;

    public AppUpdateValidatorImpl(AppBuilderAppFactory factory) {
        this.factory = factory;
    }

    @Override
    public void validate(String id) {
        AppBuilderApp app = this.factory.create(id);
        if (AppState.getAppState(app.getState()) == AppState.PUBLISHED) {
            throw new AippException(AippErrCode.APP_HAS_ALREADY);
        }
    }
}
