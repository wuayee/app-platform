/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.taskcenter.domain;

/**
 * 表示任务数据源的类型。
 *
 * @author 陈镕希
 * @since 2023-08-07
 */
public enum SourceType {
    /**
     * 表示三方系统推送。
     */
    THIRD_PARTY_PUSH,

    /**
     * 表示定时任务获取。
     */
    SCHEDULE,

    /**
     * 表示调用刷新。
     */
    REFRESH_IN_TIME,
}
