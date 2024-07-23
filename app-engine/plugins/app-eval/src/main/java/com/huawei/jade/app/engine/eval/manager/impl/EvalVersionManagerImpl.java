/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.eval.manager.impl;

import com.huawei.fitframework.annotation.Component;
import com.huawei.jade.app.engine.eval.manager.EvalDatasetVersionManager;

/**
 * 表示 {@link EvalDatasetVersionManager} 的默认实现。
 *
 * @author 易文渊
 * @since 2024-07-20
 */
@Component
public class EvalVersionManagerImpl implements EvalDatasetVersionManager {
    @Override
    public long applyVersion() {
        return 0;
    }
}