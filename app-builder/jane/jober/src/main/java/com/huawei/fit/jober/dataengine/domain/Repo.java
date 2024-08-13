/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.dataengine.domain;

/**
 * 所有Repo的基类，提供共同方法的声明。
 *
 * @author 晏钰坤
 * @since 2023/7/3
 */
public interface Repo<T> {
    /**
     * 保存一个Entity。
     *
     * @param entity 需要保存的Entity {@link T}。
     * @return 保存后的Entity {@link T}。
     */
    T save(T entity);

    /**
     * 根据Id查找对应的Entity。
     *
     * @param id 该Entity唯一标识 {@link String}。
     * @return 唯一标识对应Entity对象 {@link T}。
     */
    T find(String id);

    /**
     * 删除一个Entity。
     *
     * @param entity 需要删除的Entity {@link T}。
     */
    void delete(T entity);
}
