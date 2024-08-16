/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.store.tool.parser.code;

import static com.huawei.jade.common.model.ModelId.STORE_MODEL_ID;

import com.huawei.jade.common.code.RetCode;
import com.huawei.jade.common.model.ModelInfo;

/**
 * 插件部署错误码
 *
 * @author 张雪彬
 * @since 2024-08-07
 */
public enum PluginDeployRetCode implements RetCode, ModelInfo {
    /**
     * 插件上传校验失败，未上传文件
     */
    NO_FILE_UPLOADED_ERROR(130901001, "No file entity found in the received file."),

    /**
     * 插件上传校验失败，非zip压缩包
     */
    UPLOADED_FILE_FORMAT_ERROR(130901002, "The uploaded file must be a zip file."),

    /**
     * 上传文件中没有插件
     */
    NO_PLUGIN_FOUND_ERROR(130901003, "No plugin found in the uploaded file."),

    /**
     * 上传文件中缺少文件，占位符代表缺少的文件名称
     */
    FILE_MISSING_ERROR(130901004, "Missing {0} in the uploaded file."),

    /**
     * 插件唯一性校验失败
     */
    PLUGIN_UNIQUE_CHECK_ERROR(130901005, "Failed to verify the file uniqueness."),

    /**
     * json 文件解析失败
     */
    JSON_PARSE_ERROR(130901006, "Failed to convert json data. msg: {0}"),

    /**
     * 插件不存在
     */
    PLUGIN_NOT_EXISTS(130901007, "Plugin id {0} not exists"),

    /**
     * 插件完整性校验失败
     */
    PLUGIN_COMPLETENESS_CHECK_ERROR(130901008, "Failed to verify the file completeness.");

    private final int code;

    private final String msg;

    PluginDeployRetCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    @Override
    public int getCode() {
        return this.code;
    }

    @Override
    public String getMsg() {
        return this.msg;
    }

    @Override
    public int getSubSystemId() {
        return APP_ENGINE_ID;
    }

    @Override
    public int getModelId() {
        return STORE_MODEL_ID.getModelId();
    }

    @Override
    public int getSubModelId() {
        return 0x01;
    }
}
