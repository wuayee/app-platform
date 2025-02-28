/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.common.aop;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 对实体的操作类型
 *
 * @author 姚江
 * @since 2023-11-16 18:53
 */
@Getter
@AllArgsConstructor
public enum OperateEnum {
    /**
     * 创建
     */
    CREATED("创建"),

    /**
     * 更新
     */
    UPDATED("更新"),

    /**
     * 删除
     */
    DELETED("删除"),

    /**
     * 创建任务关联
     */
    RELADD("创建关联"),

    /**
     * 删除任务关联
     */
    RELDEL("删除关联");

    private final String description;
}
