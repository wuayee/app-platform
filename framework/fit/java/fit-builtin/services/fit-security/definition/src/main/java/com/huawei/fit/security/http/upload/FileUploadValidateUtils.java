/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.security.http.upload;

import static modelengine.fitframework.inspection.Validation.notNull;

import com.huawei.fit.http.entity.FileEntity;
import com.huawei.fit.security.http.FitSecurityException;
import com.huawei.fit.security.http.upload.support.AggregatedFileUploadValidator;

/**
 * 为文件上传校验提供工具方法。
 *
 * @author 何天放
 * @since 2024-07-12
 */
public final class FileUploadValidateUtils {
    private FileUploadValidateUtils() {}

    /**
     * 对文件进行校验。
     *
     * @param entity 表示文件的 {@link FileEntity}。
     * @param config 表示校验配置的 {@link FileUploadValidateConfig}。
     * @throws FitSecurityException 当文件上传校验未通过时。
     */
    public static void validate(FileEntity entity, FileUploadValidateConfig config) throws FitSecurityException {
        notNull(entity, "The file entity cannot be null.");
        notNull(config, "The config for file upload validate cannot be null.");
        AggregatedFileUploadValidator.INSTANCE.validate(entity, config);
    }

    /**
     * 对文件进行校验。
     *
     * @param entity 表示文件的 {@link FileEntity}。
     * @param config 表示校验配置的 {@link FileUploadValidateConfig}。
     * @param validator 表示用户自定义文件校验器的 {@link FileUploadValidator}。
     * @throws FitSecurityException 当文件上传校验未通过时。
     */
    public static void validate(FileEntity entity, FileUploadValidateConfig config, FileUploadValidator validator)
            throws FitSecurityException {
        notNull(entity, "The file entity cannot be null.");
        notNull(config, "The config for file upload validate cannot be null.");
        notNull(validator, "The file validator cannot be null.");
        AggregatedFileUploadValidator.INSTANCE.validate(entity, config);
        validator.validate(entity, config);
    }
}
