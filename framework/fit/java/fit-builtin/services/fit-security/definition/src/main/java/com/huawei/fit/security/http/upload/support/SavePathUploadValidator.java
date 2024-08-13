/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.security.http.upload.support;

import static com.huawei.fitframework.inspection.Validation.notNull;

import com.huawei.fit.http.entity.FileEntity;
import com.huawei.fit.security.http.FitSecurityException;
import com.huawei.fit.security.http.support.FileSavePathException;
import com.huawei.fit.security.http.upload.FileUploadValidateConfig;
import com.huawei.fit.security.http.upload.FileUploadValidator;
import com.huawei.fitframework.util.StringUtils;

import java.io.File;
import java.io.IOException;

/**
 * 表示 {@link FileUploadValidator} 的跨路径安全性校验功能实现。
 *
 * @author 何天放
 * @since 2024-07-12
 */
public final class SavePathUploadValidator implements FileUploadValidator {
    /**
     * 表示 {@link FileUploadValidator} 的跨路径安全性校验功能实现的实例。
     */
    public static final FileUploadValidator INSTANCE = new SavePathUploadValidator();

    private SavePathUploadValidator() {}

    @Override
    public void validate(FileEntity entity, FileUploadValidateConfig config) throws FitSecurityException {
        notNull(entity, "The file entity cannot be null.");
        notNull(config, "The config for file upload validate cannot be null.");
        String fileSavePath = config.fileSavePath();
        if (StringUtils.isBlank(fileSavePath)) {
            return;
        }
        String canonicalPathOfFile;
        try {
            canonicalPathOfFile = new File(fileSavePath, entity.filename()).getCanonicalPath();
        } catch (IOException ex) {
            throw new FileSavePathException("Cannot get canonical path of file.");
        }
        String canonicalPathOfSavePath;
        try {
            canonicalPathOfSavePath = new File(fileSavePath).getCanonicalPath();
        } catch (IOException ex) {
            throw new FileSavePathException("Cannot get canonical path of file save path.");
        }
        if (!canonicalPathOfFile.startsWith(canonicalPathOfSavePath)) {
            throw new FileSavePathException(StringUtils.format(
                    "Path of file is not start with path of save path. [canonicalPathOfFile={0}, "
                            + "canonicalPathOfSavePath={1}]",
                    canonicalPathOfFile,
                    canonicalPathOfSavePath));
        }
    }
}
