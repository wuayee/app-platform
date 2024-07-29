/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.security.http.name;

import static com.huawei.fitframework.inspection.Validation.notNull;

import com.huawei.fit.security.http.FitSecurityException;
import com.huawei.fit.security.http.name.support.DefaultFileNameValidator;

/**
 * 为文件名校验提供工具方法。
 *
 * @author 何天放 h00679269
 * @since 2024-07-18
 */
public final class FileNameValidateUtils {
    private FileNameValidateUtils() {}

    /**
     * 对文件名进行校验。
     *
     * @param processedFileName 表示经过预处理后的文件名的 {@link String}。
     * @param config 表示文件名校验配置的 {@link FileNameValidateConfig}。
     * @throws FitSecurityException 当文件名校验未通过时。
     */
    public static void validate(String processedFileName, FileNameValidateConfig config) throws FitSecurityException {
        notNull(processedFileName, "The processed file name cannot be null.");
        notNull(config, "The config for file name validate cannot be null.");
        DefaultFileNameValidator.INSTANCE.validate(processedFileName, config);
    }
}
