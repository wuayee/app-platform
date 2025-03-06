/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.jade.oms.operater.log.support;

import com.huawei.fitframework.annotation.Component;
import com.huawei.jade.authentication.context.UserContext;
import com.huawei.jade.authentication.context.UserContextHolder;
import com.huawei.jade.service.SpanEndObserver;

import io.opentelemetry.api.trace.Span;
import modelengine.jade.oms.operater.log.util.Constants;

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
        span.setAttribute(Constants.USER_NAME, userContext.getName()).setAttribute(Constants.LOG_TERMINAL, userContext.getIp());
    }
}