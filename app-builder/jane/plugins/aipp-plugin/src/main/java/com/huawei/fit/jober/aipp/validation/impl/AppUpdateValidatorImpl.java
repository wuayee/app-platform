/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.validation.impl;

import com.huawei.fit.jober.aipp.common.exception.AippErrCode;
import com.huawei.fit.jober.aipp.common.exception.AippException;
import com.huawei.fit.jober.aipp.domain.AppBuilderApp;
import com.huawei.fit.jober.aipp.enums.AppState;
import com.huawei.fit.jober.aipp.factory.AppBuilderAppFactory;
import com.huawei.fit.jober.aipp.validation.AppUpdateValidator;
import com.huawei.fitframework.annotation.Component;

/**
 * App 更新校验器
 *
 * @author 邬涨财 w00575064
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
