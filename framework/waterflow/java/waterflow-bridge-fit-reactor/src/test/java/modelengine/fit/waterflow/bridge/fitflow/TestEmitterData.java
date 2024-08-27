/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.bridge.fitflow;

import lombok.Data;

/**
 * 用于测试的EmitterData
 *
 * @author 夏斐
 * @since 1.0
 */
@Data
public class TestEmitterData<T> implements FiniteEmitterData {
    private T data;

    private boolean isEnd;

    private boolean isError;

    private String errorMessage;

    public TestEmitterData(T data, boolean isEnd, boolean isError, String message) {
        this.data = data;
        this.isEnd = isEnd;
        this.isError = isError;
        this.errorMessage = message;
    }
}
