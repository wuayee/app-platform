/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.bridge.fitflow;

/**
 * 用于构造 {@link FiniteEmitterData}
 *
 * @param <D> 原始参数类型
 * @param <T> 目标参数类型，必须实现{@link FiniteEmitterData}
 * @since 1.0
 */
public interface FiniteEmitterDataBuilder<D, T extends FiniteEmitterData> {
    /**
     * 构造一条数据
     *
     * @param data 原始数据
     * @return 目标数据
     */
    T data(D data);

    /**
     * 构造一个用于结束标识的数据
     *
     * @return 结束数据
     */
    T end();

    /**
     * 构造一个用于错误标识的数据
     *
     * @param message 错误信息
     * @return 错误数据
     */
    T error(String message);
}
