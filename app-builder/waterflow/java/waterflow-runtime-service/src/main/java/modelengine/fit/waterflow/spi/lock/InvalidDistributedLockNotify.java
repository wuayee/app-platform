/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.spi.lock;

import java.util.concurrent.locks.Lock;
import java.util.function.Consumer;

/**
 * 当分布式锁失效时，向外部通知
 *
 * @author 夏斐
 * @since 2024/3/21
 */
public interface InvalidDistributedLockNotify {
    /**
     * 订阅失效锁的通知
     *
     * @param consumer 接收方
     */
    void subscribe(Consumer<Lock> consumer);

    /**
     * 通知锁失效
     *
     * @param invalidLock 失效的锁
     */
    void notify(Lock invalidLock);
}
