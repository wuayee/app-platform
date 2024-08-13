/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.security.http.upload.support;

import static com.huawei.fitframework.inspection.Validation.notNull;

import com.huawei.fit.http.entity.FileEntity;
import com.huawei.fit.security.http.FitSecurityException;
import com.huawei.fit.security.http.name.FileNameValidateUtils;
import com.huawei.fit.security.http.upload.FileUploadValidateConfig;
import com.huawei.fit.security.http.upload.FileUploadValidator;

import java.util.Arrays;
import java.util.List;

/**
 * 表示 {@link FileUploadValidator} 的各校验功能聚合实现。
 *
 * @author 何天放
 * @since 2024-07-12
 */
public final class AggregatedFileUploadValidator implements FileUploadValidator {
    /**
     * 表示 {@link FileUploadValidator} 的各校验功能聚合实现的实例。
     */
    public static final FileUploadValidator INSTANCE = new AggregatedFileUploadValidator();

    private static final List<FileUploadValidator> validators = Arrays.asList(FileNameUploadValidatorAdapter.INSTANCE,
            FileSizeUploadValidator.INSTANCE,
            SavePathUploadValidator.INSTANCE,
            UploadPathValidator.INSTANCE);

    private AggregatedFileUploadValidator() {}

    @Override
    public void validate(FileEntity entity, FileUploadValidateConfig config) throws FitSecurityException {
        notNull(entity, "The file entity cannot be null.");
        notNull(config, "The config for file upload validate cannot be null.");
        for (FileUploadValidator validator : validators) {
            validator.validate(entity, config);
        }
    }

    private static final class FileNameUploadValidatorAdapter implements FileUploadValidator {
        static final FileUploadValidator INSTANCE = new FileNameUploadValidatorAdapter();

        private FileNameUploadValidatorAdapter() {}

        @Override
        public void validate(FileEntity entity, FileUploadValidateConfig config) throws FitSecurityException {
            FileNameValidateUtils.validate(entity.filename(), config.fileNameValidateConfig());
        }
    }
}
