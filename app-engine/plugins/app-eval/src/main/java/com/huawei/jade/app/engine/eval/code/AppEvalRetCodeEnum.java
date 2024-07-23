/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.eval.code;

import com.huawei.jade.common.code.RetCode;
import com.huawei.jade.common.model.ModelInfo;

/**
 * 应用评估模块返回码枚举，返回码最大值为 65535。
 *
 * @author 易文渊
 * @since 2024-07-20
 */
public enum AppEvalRetCodeEnum implements RetCode, ModelInfo {
    /**
     * 评估数据 schema 校验失败，占位符分别代表评估内容和 schema。
     */
    EVAL_DATA_INVALID_ERROR(1, "The content `{0}` cannot match schema `{1}`");

    private final int code;
    private final String msg;

    AppEvalRetCodeEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    @Override
    public int getCode() {
        return this.convertModelCode(this.getSubSystemId(), this.getModelId(), this.code);
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
        return 0x08;
    }

    @Override
    public int getSubModelId() {
        throw new UnsupportedOperationException("Not define sub model id.");
    }
}