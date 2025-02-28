/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.taskcenter.declaration;

import lombok.Data;
import modelengine.fit.jane.task.util.UndefinableValue;

import java.util.List;

/**
 * 表示租户的声明。
 *
 * @author 陈镕希
 * @since 2023-09-28
 */
@Data
public class TenantDeclaration {
    private UndefinableValue<String> name;

    private UndefinableValue<String> description;

    private UndefinableValue<String> avatarId;

    private UndefinableValue<List<String>> tags;
}
