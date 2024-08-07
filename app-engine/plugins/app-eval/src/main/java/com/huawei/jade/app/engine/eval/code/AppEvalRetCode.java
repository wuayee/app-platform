/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.eval.code;

import static com.huawei.jade.common.model.ModelId.APP_EVAL_MODEL_ID;

import com.huawei.jade.common.code.RetCode;
import com.huawei.jade.common.model.ModelInfo;

/**
 * 应用评估模块返回码枚举，返回码最大值为 256。
 *
 * @author 易文渊
 * @since 2024-07-20
 */
public enum AppEvalRetCode implements RetCode, ModelInfo {
    /**
     * 评估数据 schema 校验失败，占位符分别代表评估内容和 schema。
     */
    EVAL_DATA_INVALID_ERROR(1, "The content `{0}` cannot match schema `{1}`, error: {2}"),

    /**
     * 评估数据已被删除，占位符代表评估数据id。
     */
    EVAL_DATA_DELETED_ERROR(2, "The data with id `{0}` is already deleted"),

    /**
     * 评估数据集删除时有数据插入，占位符代表评估数据id。
     */
    EVAL_DATASET_DELETION_ERROR(3,
            "New data inserted during dataset deletion. Please retry to delete dataset with id(s) `{0}`");

    private final int code;
    private final String msg;

    AppEvalRetCode(int code, String msg) {
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
        return 0x01;
    }
}