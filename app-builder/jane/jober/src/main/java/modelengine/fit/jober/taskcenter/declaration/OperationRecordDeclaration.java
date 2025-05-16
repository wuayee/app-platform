/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.taskcenter.declaration;

import lombok.Data;
import modelengine.fit.jane.task.util.UndefinableValue;

/**
 * 操作记录的声明
 *
 * @author 姚江
 * @since 2023-11-17 14:16
 */
@Data
public class OperationRecordDeclaration {
    private UndefinableValue<String> objectType;

    private UndefinableValue<String> objectId;

    private UndefinableValue<String> message;

    private UndefinableValue<String> operate;
}
