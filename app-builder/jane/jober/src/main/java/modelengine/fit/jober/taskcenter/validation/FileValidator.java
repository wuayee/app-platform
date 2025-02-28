/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.taskcenter.validation;

import modelengine.fit.jane.task.util.OperationContext;

/**
 * 为文件提供校验。
 *
 * @author 陈镕希
 * @since 2023-10-10
 */
public interface FileValidator {
    /**
     * 校验文件的长度。
     *
     * @param contentLength 表示文件的长度。
     * @param context context
     */
    void contentLength(int contentLength, OperationContext context);
}
