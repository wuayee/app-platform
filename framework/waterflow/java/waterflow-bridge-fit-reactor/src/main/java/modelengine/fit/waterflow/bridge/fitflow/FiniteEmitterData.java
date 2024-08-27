/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.bridge.fitflow;

/**
 * 有限流的data需要实现该接口，并提供以下方法
 *
 * @author 夏斐
 * @since 1.0
 */
public interface FiniteEmitterData {
    /**
     * 是否结束
     *
     * @return 是否是结束标识
     */
    boolean isEnd();

    /**
     * 是否错误
     *
     * @return 是否是错误标识
     */
    boolean isError();

    /**
     * 错误信息
     *
     * @return isError()为true的情况下，错误信息
     */
    String getErrorMessage();
}
