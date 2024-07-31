/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.eval.manager.impl;

import com.huawei.fitframework.annotation.Component;
import com.huawei.jade.app.engine.eval.manager.EvalDatasetVersionManager;
import com.huawei.jade.app.engine.uid.UidGenerator;

/**
 * 表示 {@link EvalDatasetVersionManager} 的默认实现。
 *
 * @author 易文渊
 * @author 何嘉斌
 * @since 2024-07-20
 */
@Component
public class EvalVersionManagerImpl implements EvalDatasetVersionManager {
    private final UidGenerator versionGenerator;

    /**
     * 表示评估数据集版本管理器实现的构建器。
     *
     * @param generator 表示版本生成器持久层接口。
     */
    public EvalVersionManagerImpl(UidGenerator generator) {
        this.versionGenerator = generator;
    }

    @Override
    public long applyVersion() {
        return this.versionGenerator.getUid();
    }
}