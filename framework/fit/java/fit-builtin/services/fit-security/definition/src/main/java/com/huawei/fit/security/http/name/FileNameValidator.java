/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.security.http.name;

import com.huawei.fit.security.http.FitSecurityException;

/**
 * 表示文件名校验器。
 *
 * @author 何天放 h00679269
 * @since 2024-07-18
 */
public interface FileNameValidator {
    /**
     * 对文件名进行校验。
     *
     * @param processedFileName 表示经过预处理后的文件名的 {@link String}。
     * @param config 表示文件名校验配置的 {@link FileNameValidateConfig}。
     * @throws FitSecurityException 当文件名校验不通过时。
     */
    void validate(String processedFileName, FileNameValidateConfig config) throws FitSecurityException;
}
