/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.common.model;

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
    int APP_ENGINE_ID = 13;

    /**
     * Fit 框架保留。
     */
    int FIT_FRAMEWORK_ID = 0x7f;
}