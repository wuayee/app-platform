/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.carver.telemetry.aop;

import io.opentelemetry.api.trace.Span;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.aop.ProceedingJoinPoint;
import modelengine.fitframework.aop.annotation.Around;
import modelengine.fitframework.aop.annotation.Aspect;
import modelengine.jade.service.annotations.AppendSpanAttr;

/**
 * {@link AppendSpanAttr } 的切面。
 *
 * @author 马朝阳
 * @since 2024-08-05
 */
@Aspect
@Component
public class AppendSpanAttrAspect {
    private final SpanAttrObserver observer;

    /**
     * 构造函数，用于注入{@link SpanEndObserver}。
     *
     * @param observer 观察者对象
     */
    public AppendSpanAttrAspect(@Fit(alias = "ParamSpanAttributeInjector") SpanAttrObserver observer) {
        this.observer = observer;
    }

    @Around("@annotation(modelengine.jade.service.annotations.AppendSpanAttr)")
    private Object handle(ProceedingJoinPoint joinPoint) throws Throwable {
        Span current = Span.current();
        Object proceedResult = null;
        try {
            proceedResult = joinPoint.proceed();
            return proceedResult;
        } finally {
            this.observer.onAppendSpanAttr(current, joinPoint.getMethod(), joinPoint.getArgs(), proceedResult);
        }
    }
}