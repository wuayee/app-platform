/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.common;

import com.huawei.fitframework.inspection.Validation;
import com.huawei.fitframework.log.Logger;
import com.huawei.fitframework.util.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

/**
 * 文件操作工具
 *
 * @author l00611472
 * @since 2024/1/22
 */
public class AippFileUtils {
    private static final Logger log = Logger.get(AippFileUtils.class);

    /**
     * 在本地临时目录 Utils.NAS_SHARE_DIR 创建子目录及文件
     *
     * @param dirName 子目录名称
     * @param fileName s3url
     * @return 临时文件
     */
    public static File createFile(String dirName, String fileName) throws IOException {
        Validation.notBlank(dirName, "dirName cant be blank.");
        Validation.notBlank(fileName, "fileName cant be blank.");

        File dir = Paths.get(Utils.NAS_SHARE_DIR, dirName).toFile();
        if (!dir.exists()) {
            if(!dir.mkdir()) {
                throw new IOException(dir.getCanonicalPath() + " created failed.");
            }
        }
        File docFile = Paths.get(dir.getCanonicalPath(), fileName).toFile();
        if (!docFile.exists()) {
            if (!docFile.createNewFile()) {
                log.warn(docFile.getCanonicalPath() + " already exist.");
            }
        }
        return docFile;
    }

    /**
     * 删除文件
     *
     * @param file 文件句柄
     */
    public static void deleteFile(File file) {
        try {
            FileUtils.delete(file);
        } catch (IllegalStateException e) {
            log.warn("delete file {} failed, error = {}", file.getName(), e.getMessage());
        }
    }
}
