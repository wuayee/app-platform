/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.security.http.zipped.support;

import static com.huawei.fitframework.inspection.Validation.notBlank;
import static com.huawei.fitframework.inspection.Validation.notNull;

import com.huawei.fit.security.http.FitSecurityException;
import com.huawei.fit.security.http.support.ZippedFileException;
import com.huawei.fit.security.http.zipped.ZippedFileValidateConfig;
import com.huawei.fit.security.http.zipped.ZippedFileValidator;
import com.huawei.fitframework.util.FileUtils;
import com.huawei.fitframework.util.MapBuilder;
import com.huawei.fitframework.util.StringUtils;

import java.util.Map;

/**
 * 表示 {@link ZippedFileValidator} 的各类型校验器聚合实现。
 * <p>当前仅支持对于 zip 和 jar 类型的压缩文件进行校验，保留该聚合实现的目的是。</p>
 *
 * @author 何天放
 * @since 2024-07-20
 */
public final class AggregatedZippedFileValidator implements ZippedFileValidator {
    /**
     * 表示 {@link ZippedFileValidator} 的各类型校验器聚合实现的实例。
     */
    public static final ZippedFileValidator INSTANCE = new AggregatedZippedFileValidator();

    private static final String ZIP_FILE_EXTENSION_NAME = ".zip";
    private static final String JAR_FILE_EXTENSION_NAME = ".jar";
    private static final Map<String, ZippedFileValidator> validators = MapBuilder.<String, ZippedFileValidator>get()
            .put(ZIP_FILE_EXTENSION_NAME, ZipOrJarTypeFileValidator.INSTANCE)
            .put(JAR_FILE_EXTENSION_NAME, ZipOrJarTypeFileValidator.INSTANCE)
            .build();

    private AggregatedZippedFileValidator() {}

    @Override
    public void validate(String filePath, String fileName, ZippedFileValidateConfig config)
            throws FitSecurityException {
        notNull(filePath, "The file path cannot be null.");
        notBlank(fileName, "The file name cannot be blank.");
        notNull(config, "The config for zipped file validate cannot be null.");
        String extensionName = FileUtils.extension(fileName);
        if (!validators.containsKey(extensionName)) {
            throw new ZippedFileException(StringUtils.format(
                    "Cannot validate zipped file as this type. [fileExtensionName={0}]",
                    extensionName));
        }
    }
}
