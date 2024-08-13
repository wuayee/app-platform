/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.common;

/**
 * ohscript调用fitable接口异常
 *
 * @author 杨祥宇
 * @since 2023/12/20
 */
public class OhscriptExecuteException extends JoberGenericableException {
    private final String genericableId;

    private final String fitableId;

    /**
     * ohscript调用fitable接口异常
     *
     * @param message message
     * @param cause cause
     * @param genericableId genericableId
     * @param fitableId fitableId
     */
    public OhscriptExecuteException(String message, Throwable cause, String genericableId, String fitableId) {
        super(message, cause);
        this.genericableId = genericableId;
        this.fitableId = fitableId;
    }

    public String getGenericableId() {
        return this.genericableId;
    }

    public String getFitableId() {
        return this.fitableId;
    }
}
