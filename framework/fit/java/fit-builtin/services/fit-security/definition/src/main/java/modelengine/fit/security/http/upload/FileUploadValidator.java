/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fit.security.http.upload;

import modelengine.fit.http.entity.FileEntity;
import modelengine.fit.security.http.FitSecurityException;

/**
 * 表示文件上传校验器。
 *
 * @author 何天放
 * @since 2024-07-11
 */
public interface FileUploadValidator {
    /**
     * 对文件进行校验。
     *
     * @param entity 表示文件的 {@link FileEntity}。
     * @param config 表示校验配置的 {@link FileUploadValidateConfig}。
     * @throws FitSecurityException 当文件上传校验未通过时。
     */
    void validate(FileEntity entity, FileUploadValidateConfig config) throws FitSecurityException;
}
