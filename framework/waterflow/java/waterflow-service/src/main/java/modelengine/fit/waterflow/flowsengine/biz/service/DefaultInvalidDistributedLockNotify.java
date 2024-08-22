/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2020. All rights reserved.
 */

package modelengine.fit.waterflow.flowsengine.biz.service;

import com.huawei.fit.jane.task.gateway.InvalidDistributedLockNotify;
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
