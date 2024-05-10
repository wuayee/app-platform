/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.common;

/**
 * ohscript调用fitable接口异常
 *
 * @author y00679285
 * @since 2023/12/20
 */
public class OhscriptExecuteException extends JoberGenericableException {
    private final String genericableId;

    private final String fitableId;

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
