/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober;

import com.huawei.fit.jober.entity.File;
import com.huawei.fit.jober.entity.FileDeclaration;
import com.huawei.fit.jober.entity.OperationContext;
import com.huawei.fitframework.annotation.Genericable;

/**
 * 文件存储服务Genericable。
 *
 * @author 梁子涵
 * @since 2024-01-04
 */
public interface FileService {
    /**
     * 上传文件。
     *
     * @param fileId fileId
     * @param declaration 表示文件的声明的 {@link FileDeclaration}。
     * @param context 表示操作上下文的 {@link OperationContext}。
     * @return 表示新创建的文件的 {@link File}。
     */
    @Genericable(id = "b263a5e8bd9a6268494817df33a25ce2")
    File upload(String fileId, FileDeclaration declaration, OperationContext context);

    /**
     * 下载文件。
     *
     * @param objectKey 表示桶内完整文件路径的 {@link String}。
     * @param context 表示操作上下文的 {@link OperationContext}。
     * @return 表示下载的文件的 {@link File}。
     */
    @Genericable(id = "59c9b4528d3a72c881ef5d0b61eb174a")
    File download(String objectKey, OperationContext context);
}
