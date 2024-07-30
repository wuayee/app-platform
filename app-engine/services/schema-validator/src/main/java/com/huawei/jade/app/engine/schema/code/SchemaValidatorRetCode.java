/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.schema.code;

import static com.huawei.jade.common.model.ModelId.APP_EVAL_MODEL_ID;

import com.huawei.jade.common.code.RetCode;
import com.huawei.jade.common.model.ModelInfo;

/**
 * Schema 校验模块返回码枚举, 返回码最大值为 256。
 *
 * @author 兰宇晨
 * @since 2024-07-29
 */
public enum SchemaValidatorRetCode implements RetCode, ModelInfo {
    /**
     * 评估数据 Schema 无效，占位符代表评估数据 schema。
     */
    VALIDATE_SCHEMA_INVALID_ERROR(1, "The schema '{0}' is invalid"),

    /**
     * 校验评估数据无效，占位符代表评估数据和 Schema。
     */
    VALIDATE_CONTENT_INVALID_ERROR(2, "The content '{0}' cannot match schema '{1}', error: {2}");

    private final int code;
    private final String msg;

    SchemaValidatorRetCode(int code, String msg) {
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
        return 0x02;
    }
}

