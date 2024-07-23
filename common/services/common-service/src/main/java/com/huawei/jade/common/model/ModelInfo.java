/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.common.model;

/**
 * 表示模块信息的接口。
 *
 * @author 易文渊
 * @since 2024-07-20
 */
public interface ModelInfo {
    /**
     * 公共返回码保留。
     */
    int COMMON_ID = 0;

    /**
     * AppEngine 使用。
     */
    int APP_ENGINE_ID = 1;

    /**
     * Fit 框架保留。
     */
    int FIT_FRAMEWORK_ID = 0x7f;

    /**
     * 获取子系统编号。
     *
     * @return 表示子系统编号的 {@code int}。
     */
    int getSubSystemId();

    /**
     * 获取模块编号。
     *
     * @return 表示模块编号的 {@code int}。
     */
    int getModelId();

    /**
     * 获取子模块编号。
     *
     * @return 表示子模块编号的 {@code int}。
     */
    int getSubModelId();
}