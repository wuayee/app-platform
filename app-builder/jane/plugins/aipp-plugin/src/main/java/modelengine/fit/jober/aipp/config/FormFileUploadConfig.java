/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fit.jober.aipp.config;

import modelengine.fitframework.annotation.AcceptConfigValues;
import modelengine.fitframework.annotation.Component;

/**
 * 文件上传配置参数
 *
 * @author 陈潇文
 * @since 2024-11-29
 */
@Component
@AcceptConfigValues("app-engine.file.upload")
public class FormFileUploadConfig {
    /**
     * 表单可上传的最大存储使用量占比。
     */
    private double maxStorageRatio;

    /**
     * 获取表单可上传的最大存储使用量占比。
     *
     * @return 表示表单可上传的最大存储使用量占比的 {@code double}。
     */
    public double getMaxStorageRatio() {
        return this.maxStorageRatio;
    }

    /**
     * 设置表单可上传的最大存储使用量占比。
     *
     * @param maxStorageRatio 表示表单可上传的最大存储使用量占比的 {@code double}。
     */
    public void setMaxStorageRatio(double maxStorageRatio) {
        this.maxStorageRatio = maxStorageRatio;
    }
}
