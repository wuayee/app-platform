/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.oms.operater.log.support;

import static com.huawei.jade.oms.operater.log.util.Constants.LOG_TERMINAL;
import static com.huawei.jade.oms.operater.log.util.Constants.USER_NAME;

import com.huawei.fitframework.annotation.Component;
import com.huawei.jade.authentication.context.UserContext;
import com.huawei.jade.authentication.context.UserContextHolder;
import com.huawei.jade.service.SpanEndObserver;

import io.opentelemetry.api.trace.Span;

import java.lang.reflect.Method;

/**
 * 收集 threadLocal 数据并向 span 中添加事件属性。
 *
 * @author 方誉州
 * @since 2024-08-06
 */
@Component
public class OperationAttrInjector implements SpanEndObserver {
    @Override
    public void onSpanEnd(Span span, Method method, Object[] args, Object result) {
        if (span == null) {
            return;
        }
        UserContext userContext = UserContextHolder.get();
        if (userContext == null) {
            return;
        }
        // 从threadLocal中获取
        span.setAttribute(USER_NAME, userContext.getName()).setAttribute(LOG_TERMINAL, userContext.getIp());
    }
}