/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.security.http.name.support;

import static com.huawei.fitframework.inspection.Validation.notNull;

import com.huawei.fit.security.http.FitSecurityException;
import com.huawei.fit.security.http.name.FileNameValidateConfig;
import com.huawei.fit.security.http.name.FileNameValidator;
import com.huawei.fit.security.http.support.FileNameException;
import com.huawei.fitframework.util.FileUtils;
import com.huawei.fitframework.util.StringUtils;

import java.util.List;

/**
 * 表示 {@link FileNameValidator} 的默认实现。
 *
 * @author 何天放
 * @since 2024-07-18
 */
public final class DefaultFileNameValidator implements FileNameValidator {
    /**
     * 表示 {@link FileNameValidator} 默认实现的实例。
     */
    public static final FileNameValidator INSTANCE = new DefaultFileNameValidator();

    private DefaultFileNameValidator() {}

    @Override
    public void validate(String processedFileName, FileNameValidateConfig config) throws FitSecurityException {
        notNull(processedFileName, "The processed file name cannot be null.");
        notNull(config, "The config for file name validate cannot be null.");
        if (processedFileName.isEmpty()) {
            throw new FileNameException("The file name is blank.");
        }
        if (config.blackList() != null && this.containsBlackList(processedFileName, config.blackList())) {
            throw new FileNameException("The file name contains illegal string.");
        }
        if (!StringUtils.isBlank(config.fileNameFormat()) && !processedFileName.matches(config.fileNameFormat())) {
            throw new FileNameException("The file name does not match the format.");
        }
        String extensionName = FileUtils.extension(processedFileName);
        boolean extensionNameCheckResult = this.checkFileExtensionName(extensionName, config);
        if (!extensionNameCheckResult) {
            throw new FileNameException(StringUtils.format("The file extension name is illegal. [extensionName={0}]",
                    extensionName));
        }
    }

    private boolean checkFileExtensionName(String fileExtensionName, FileNameValidateConfig config) {
        if (config.extensionNameWhiteList() == null || config.extensionNameWhiteList().isEmpty()) {
            return true;
        }
        return config.extensionNameWhiteList().stream().anyMatch(fileExtensionName::equals);
    }

    private boolean containsBlackList(String fileName, List<String> blackList) {
        if (blackList == null) {
            return false;
        }
        return blackList.stream().anyMatch(fileName::contains);
    }
}
