/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.carver.telemetry.aop;

import modelengine.jade.common.localemessage.ExceptionLocaleService;
import modelengine.jade.service.CarverGlobalOpenTelemetry;
import modelengine.jade.service.annotations.CarverSpan;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.ContextKey;
import io.opentelemetry.context.ContextStorage;
import io.opentelemetry.context.Scope;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.aop.ProceedingJoinPoint;
import modelengine.fitframework.aop.annotation.Around;
import modelengine.fitframework.aop.annotation.Aspect;
import modelengine.fitframework.exception.FitException;
import modelengine.fitframework.inspection.Validation;

import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicReference;

/**
 * {@link CarverSpan} 的切面。
 *
 * @author 刘信宏
 * @since 2024-07-23
 */
@Aspect
@Component
public class CarverSpanAspect {
    private static final ContextKey<String> TRACE_CONTEXT_KEY = ContextKey.named("carver-trace-scope-name");

    private final SpanEndObserverRepository repository;
    private final ExceptionLocaleService exceptionLocaleService;

    public CarverSpanAspect(SpanEndObserverRepository repository, ExceptionLocaleService exceptionLocaleService) {
        this.repository = repository;
        this.exceptionLocaleService = exceptionLocaleService;
    }

    @Around("@annotation(modelengine.jade.service.annotations.CarverSpan)")
    private Object handle(ProceedingJoinPoint joinPoint) throws Throwable {
        AtomicReference<Span> spanRef = new AtomicReference<>();
        Object proceedResult = null;
        try (Scope scope = this.makeCurrentScope(joinPoint, spanRef)) {
            proceedResult = joinPoint.proceed();
            return proceedResult;
        } catch (Throwable throwable) {
            this.recordException(throwable, spanRef.get());
            throw throwable;
        } finally {
            this.notifyAllSpanEndObserver(spanRef.get(), joinPoint.getMethod(), joinPoint.getArgs(), proceedResult);
            this.finishSpan(spanRef.get());
        }
    }

    private void finishSpan(Span span) {
        if (span == null) {
            return;
        }
        span.end();
    }

    private void recordException(Throwable throwable, Span span) {
        if (span == null) {
            return;
        }
        String localizeMessage = this.exceptionLocaleService.localizeMessage(throwable);
        span.setStatus(StatusCode.ERROR, localizeMessage);
        span.recordException(new FitException(localizeMessage, throwable));
    }

    private Scope makeCurrentScope(ProceedingJoinPoint joinPoint, AtomicReference<Span> spanRef) {
        String traceScopeName = Context.current().get(TRACE_CONTEXT_KEY);
        if (traceScopeName == null) {
            traceScopeName = joinPoint.getSignature().toString();
            Span span = this.getSpan(joinPoint, traceScopeName);
            Context withContext = Context.current().with(TRACE_CONTEXT_KEY, traceScopeName).with(span);
            spanRef.set(span);
            return ContextStorage.get().attach(withContext);
        }
        Span span = this.getSpan(joinPoint, traceScopeName);
        spanRef.set(span);
        return span.makeCurrent();
    }

    private Span getSpan(ProceedingJoinPoint joinPoint, String traceScopeName) {
        Tracer tracer = CarverGlobalOpenTelemetry.get().getTracer(traceScopeName);
        CarverSpan carverSpanAnnotation = Validation.notNull(joinPoint.getMethod().getAnnotation(CarverSpan.class),
                "The @CarverSpan annotation cannot be null.");
        return tracer.spanBuilder(carverSpanAnnotation.value()).startSpan();
    }

    private void notifyAllSpanEndObserver(Span span, Method method, Object[] args, Object result) {
        this.repository.get().forEach(observer -> observer.onSpanEnd(span, method, args, result));
    }
}
