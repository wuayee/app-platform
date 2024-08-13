/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fitframework.broker.support;

import static com.huawei.fitframework.util.ObjectUtils.cast;

import com.huawei.fitframework.broker.Fitable;
import com.huawei.fitframework.broker.GenericableExecutor;
import com.huawei.fitframework.broker.InvocationContext;
import com.huawei.fitframework.exception.FitException;
import com.huawei.fitframework.exception.MethodInvocationException;
import com.huawei.fitframework.exception.RetryableException;
import com.huawei.fitframework.util.ExceptionUtils;

import java.util.Collections;

/**
 * 表示 {@link GenericableExecutor} 的重试调用实现。
 *
 * @author 季聿阶
 * @since 2023-03-27
 */
public class RetryableGenericableExecutor extends AbstractUnicastGenericableExecutor {
    private final GenericableExecutor executor;

    RetryableGenericableExecutor(GenericableExecutor executor) {
        this.executor = executor;
    }

    @Override
    protected Object execute(Fitable fitable, InvocationContext context, Object[] args) {
        int retry = context.retry() + 1;
        RetryableException actualException;
        do {
            try {
                retry--;
                return this.executor.execute(Collections.singletonList(fitable), context, args);
            } catch (RetryableException e) {
                actualException = e;
            } catch (MethodInvocationException e) {
                Throwable cause = ExceptionUtils.getActualCause(e);
                if (cause instanceof RetryableException) {
                    actualException = cast(cause);
                } else {
                    throw FitException.wrap(cause, fitable.genericable().id(), fitable.id());
                }
            } catch (Throwable e) {
                throw FitException.wrap(e, fitable.genericable().id(), fitable.id());
            }
        } while (retry > 0);
        actualException.associateFitable(fitable.genericable().id(), fitable.id());
        throw actualException;
    }
}
