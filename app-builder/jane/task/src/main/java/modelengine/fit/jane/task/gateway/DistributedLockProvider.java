/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jane.task.gateway;

import java.util.concurrent.locks.Lock;

/**
 * 为应用程序提供分布式锁。
 *
 * @author 梁济时
 * @since 2023-11-16
 */
public interface DistributedLockProvider {
    /**
     * 获取指定键的分布式锁。
     *
     * @param key 表示分布式锁对应的键的 {@link String}。
     * @return 表示分布式锁的 {@link Lock}。
     */
    Lock get(String key);
}
