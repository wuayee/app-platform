/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.schema.code;

import static com.huawei.jade.common.model.ModelId.APP_EVAL_MODEL_ID;

import com.huawei.jade.common.code.RetCode;
import com.huawei.jade.common.model.ModelInfo;

/**
 * 数据约束生成模块返回码枚举, 返回码最大值为 256。
 *
 * @author 兰宇晨
 * @since 2024-08-07
 */
public enum SchemaGeneratorRetCode implements RetCode, ModelInfo {
    /**
     * 上传的 json 无效。
     */
    JSON_INVALID_ERROR(1, "The json '{0}' is invalid to generate schema, error: {1}.");
    private final int code;
    private final String msg;

    SchemaGeneratorRetCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    @Override
    public int getCode() {
        return this.convertSubModelCode(this.getSubSystemId(), this.getModelId(), this.getSubModelId(), this.code);
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
        return APP_EVAL_MODEL_ID.getModelId();
    }

    @Override
    public int getSubModelId() {
        return 0x03;
    }
}

