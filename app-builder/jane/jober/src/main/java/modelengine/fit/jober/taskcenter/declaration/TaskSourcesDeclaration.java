/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.taskcenter.declaration;

import lombok.Data;

import java.util.List;

/**
 * 为批量任务数据源提供声明。
 *
 * @author 梁济时
 * @since 2023-08-08
 */
@Data
public class TaskSourcesDeclaration {
    private String taskId;

    private List<SourceDeclaration> sources;
}
