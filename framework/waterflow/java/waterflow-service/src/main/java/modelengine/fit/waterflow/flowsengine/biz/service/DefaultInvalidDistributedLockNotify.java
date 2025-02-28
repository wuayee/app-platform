/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.flowsengine.biz.service;

import modelengine.fit.waterflow.spi.lock.InvalidDistributedLockNotify;
import modelengine.fitframework.annotation.Component;


import java.util.concurrent.locks.Lock;
import java.util.function.Consumer;

/**
 * 默认的实现，暂时为空即可
 *
 * @author 夏斐
 * @since 2024/4/28
 */
@Component
public class DefaultInvalidDistributedLockNotify implements InvalidDistributedLockNotify {
    @Override
    public void subscribe(Consumer<Lock> consumer) {

    }

    @Override
    public void notify(Lock invalidLock) {

    }
}
