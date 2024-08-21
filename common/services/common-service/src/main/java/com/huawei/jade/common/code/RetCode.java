/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.common.code;

/**
 * 表示错误码接口类型。
 *
 * @author 易文渊
 * @since 2024-07-18
 */
public interface RetCode {
    /**
     * 获取错误码。
     *
     * @return 表示状态码的 {@code int}。
     */
    int getCode();

    /**
     * 获取错误信息。
     *
     * @return 表示错误信息的 {@link String}。
     */
    String getMsg();

    /**
     * 将子系统返回码转换为系统返回码。
     *
     * @param subSystemId 表示子系统编号的 {@code int}。
     * @param subSystemCode 表示子系统返回码的 {@code int}。
     * @return 表示系统返回码的 {@code int}。
     */
    default int convertSubSystemCode(int subSystemId, int subSystemCode) {
        return subSystemId * 10000000 + subSystemCode;
    }

    /**
     * 将模块返回码转换为系统返回码。
     *
     * @param subSystemId 表示子系统编号的 {@code int}。
     * @param modelId 表示模块编号的 {@code int}。
     * @param modelCode 表示模块返回码的 {@code int}。
     * @return 表示系统返回码的 {@code int}。
     */
    default int convertModelCode(int subSystemId, int modelId, int modelCode) {
        return subSystemId * 10000000 + modelId * 100000 + modelCode;
    }

    /**
     * 将子模块返回码转换为系统返回码。
     *
     * @param subSystemId 表示子系统编号的 {@code int}。
     * @param modelId 表示模块编号的 {@code int}。
     * @param subModelId 表示子模块编号 {@code int}。
     * @param subModelCode 表示子模块返回码的 {@code int}。
     * @return 表示系统返回码的 {@code int}。
     */
    default int convertSubModelCode(int subSystemId, int modelId, int subModelId, int subModelCode) {
        return this.convertModelCode(subSystemId, modelId, subModelId * 1000 + subModelCode);
    }
}