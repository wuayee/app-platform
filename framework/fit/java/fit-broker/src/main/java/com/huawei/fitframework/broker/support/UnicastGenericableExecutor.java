/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fitframework.broker.support;

import com.huawei.fitframework.broker.Fitable;
import com.huawei.fitframework.broker.GenericableExecutor;
import com.huawei.fitframework.broker.InvocationContext;
import com.huawei.fitframework.exception.FitException;

/**
 * 表示 {@link GenericableExecutor} 的单播调用实现。
 *
 * @author 季聿阶 j00559309
 * @since 2023-03-27
 */
public class UnicastGenericableExecutor extends AbstractUnicastGenericableExecutor {
    @Override
    protected Object execute(Fitable fitable, InvocationContext context, Object[] args) {
        try {
            return fitable.execute(context, args);
        } catch (Throwable e) {
            throw FitException.wrap(e, fitable.genericable().id(), fitable.id());
        }
    }
}
