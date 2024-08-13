/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.security.http.upload.support;

import static com.huawei.fitframework.inspection.Validation.notNull;

import com.huawei.fit.http.entity.FileEntity;
import com.huawei.fit.security.http.FitSecurityException;
import com.huawei.fit.security.http.support.FileSizeException;
import com.huawei.fit.security.http.upload.FileUploadValidateConfig;
import com.huawei.fit.security.http.upload.FileUploadValidator;
import com.huawei.fitframework.util.StringUtils;

/**
 * 表示 {@link FileUploadValidator} 的文件大小校验功能实现。
 *
 * @author 何天放
 * @since 2024-07-12
 */
public final class FileSizeUploadValidator implements FileUploadValidator {
    /**
     * 表示 {@link FileUploadValidator} 的文件大小校验功能实现的实例。
     */
    public static final FileUploadValidator INSTANCE = new FileSizeUploadValidator();

    private FileSizeUploadValidator() {}

    @Override
    public void validate(FileEntity entity, FileUploadValidateConfig config) throws FitSecurityException {
        notNull(entity, "The file entity cannot be null.");
        notNull(config, "The config for file upload validate cannot be null.");
        if (config.fileSizeLimit() <= 0) {
            return;
        }
        long fileSize = entity.length();
        // 剩余的内存空间。
        long freeMemory = Runtime.getRuntime().maxMemory() - Runtime.getRuntime().totalMemory() + Runtime.getRuntime()
                .freeMemory();
        if (fileSize > config.fileSizeLimit()) {
            throw new FileSizeException(StringUtils.format(
                    "File size error, file is too big. [fileSize={0}, configuredFileSize={1}]",
                    fileSize,
                    config.fileSizeLimit()));
        }
        if (fileSize > freeMemory) {
            throw new FileSizeException(StringUtils.format(
                    "File size error, free memory is not enough. [fileSize={0}, freeMemory={1}]",
                    fileSize,
                    freeMemory));
        }
    }
}
