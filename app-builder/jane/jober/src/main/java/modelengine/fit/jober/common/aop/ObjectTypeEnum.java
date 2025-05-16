/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.common.aop;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 实体类型
 *
 * @author 姚江
 * @since 2023-11-16 18:54
 */
@Getter
@AllArgsConstructor
public enum ObjectTypeEnum {
    /**
     * 实例
     */
    INSTANCE("任务实例"),

    /**
     * 任务定义
     */
    TASK("任务定义"),

    /**
     * 任务类型
     */
    TASK_TYPE("任务类型"),

    /**
     * 来源
     */
    SOURCE("任务来源"),
    ;

    private final String objectTypeName;
}
