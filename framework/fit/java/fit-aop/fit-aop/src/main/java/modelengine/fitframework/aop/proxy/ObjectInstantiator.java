/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.aop.proxy;

/**
 * 任意对象生成器。
 *
 * @param <T> 表示待实例化对象的类型的 {@link T}。
 * @author 季聿阶
 * @since 2022-05-10
 */
public interface ObjectInstantiator<T> {
    /**
     * 实例化一个对象。
     *
     * @return 表示待实例化的对象的 {@link T}。
     */
    T newInstance();
}
