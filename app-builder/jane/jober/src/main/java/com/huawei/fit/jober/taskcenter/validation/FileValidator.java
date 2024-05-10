/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.validation;

import com.huawei.fit.jane.task.util.OperationContext;

/**
 * 为文件提供校验。
 *
 * @author 陈镕希 c00572808
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
