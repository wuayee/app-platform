/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fitframework.retry.support;

import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.aop.MethodSignature;
import com.huawei.fitframework.aop.ProceedingJoinPoint;
import com.huawei.fitframework.aop.Signature;
import com.huawei.fitframework.aop.annotation.Around;
import com.huawei.fitframework.aop.annotation.Aspect;
import com.huawei.fitframework.ioc.BeanContainer;
import com.huawei.fitframework.retry.Condition;
import com.huawei.fitframework.retry.RecoverCallable;
import com.huawei.fitframework.retry.RetryExecutor;
import com.huawei.fitframework.retry.annotation.Backoff;
import com.huawei.fitframework.retry.annotation.Recover;
import com.huawei.fitframework.retry.annotation.Retryable;
import com.huawei.fitframework.retry.backoff.ExponentialRetryBackOff;
import com.huawei.fitframework.retry.condition.ConditionComposite;
import com.huawei.fitframework.retry.condition.ExceptionCondition;
import com.huawei.fitframework.retry.condition.TimesLimitedRetryCondition;
import com.huawei.fitframework.util.AnnotationUtils;
import com.huawei.fitframework.util.ObjectUtils;
import com.huawei.fitframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;

/**
 * 重试机制的处理器。
 *
 * @author 邬涨财 w00575064
 * @since 2023-02-21
 */
@Aspect
@Component
public class RetryableHandler {
    private final BeanContainer beanContainer;

    public RetryableHandler(BeanContainer beanContainer) {
        this.beanContainer = beanContainer;
    }

    @Around("@annotation(com.huawei.fitframework.retry.annotation.Retryable)")
    private Object handle(ProceedingJoinPoint joinPoint) {
        Signature signature = joinPoint.getSignature();
        if (!(signature instanceof MethodSignature)) {
            String msg = "Failed to parse retryable annotation: the annotation only used in method.";
            throw new IllegalStateException(msg);
        }
        MethodSignature methodSignature = ObjectUtils.cast(signature);
        Object target = joinPoint.getTarget();
        Method method = ReflectionUtils.getDeclaredMethod(target.getClass(),
                methodSignature.getName(),
                methodSignature.getParameterTypes());
        Object[] args = joinPoint.getArgs();
        Retryable retryable = AnnotationUtils.getAnnotation(this.beanContainer, method, Retryable.class)
                .orElseThrow(() -> new IllegalStateException(
                        "Failed to parse retryable annotation: the annotation is not exist."));
        Backoff backoffAnnotation = retryable.backoff();
        RetryExecutor<Object> retryExecutor = RetryExecutor.builder()
                .recoverCondition(new ExceptionCondition(Arrays.asList(retryable.value())))
                .retryCondition(this.buildRetryCondition(retryable))
                .backOff(this.buildBackOff(backoffAnnotation))
                .recover(this.buildRecover(retryable.recover(), method, target, args))
                .callable(this.buildCallable(joinPoint, args))
                .build();
        return retryExecutor.execute();
    }

    private Condition buildRetryCondition(Retryable retryable) {
        Condition timesLimitedRetryCondition = new TimesLimitedRetryCondition(retryable.maxAttempts());
        List<Class<? extends Throwable>> capturedExceptions = Arrays.asList(retryable.value());
        Condition exceptionRetryCondition = new ExceptionCondition(capturedExceptions);
        return ConditionComposite.combine(timesLimitedRetryCondition, exceptionRetryCondition);
    }

    private ExponentialRetryBackOff<Object> buildBackOff(Backoff backoffAnnotation) {
        return new ExponentialRetryBackOff<>(backoffAnnotation.minDelay(),
                backoffAnnotation.maxDelay(),
                backoffAnnotation.multiplier());
    }

    private RecoverCallable<Object> buildRecover(String recover, Method retryableMethod, Object target, Object[] args) {
        Method[] methods = retryableMethod.getDeclaringClass().getDeclaredMethods();
        Method recoverMethod = Arrays.stream(methods)
                .filter(searchedMethod -> this.isMethodSearched(recover, retryableMethod, searchedMethod))
                .findAny()
                .orElseThrow(() -> new IllegalStateException(
                        "Failed to parse recover method: recover method is not found."));
        return (exception) -> {
            LinkedList<Object> recoverArgs = new LinkedList<>(Arrays.asList(args));
            recoverArgs.addFirst(exception);
            return ReflectionUtils.invoke(target, recoverMethod, recoverArgs.toArray(new Object[0]));
        };
    }

    private boolean isMethodSearched(String recover, Method retryableMethod, Method searchedMethod) {
        if (!Objects.equals(searchedMethod.getName(), recover)) {
            return false;
        }
        if (!AnnotationUtils.getAnnotation(this.beanContainer, searchedMethod, Recover.class).isPresent()) {
            return false;
        }
        return this.compareParameters(searchedMethod, retryableMethod.getParameterTypes());
    }

    private boolean compareParameters(Method method, Class<?>[] retryableClass) {
        Class<?>[] parameterTypes = method.getParameterTypes();
        if (parameterTypes.length == 0 || (parameterTypes.length != retryableClass.length + 1)
                || !Exception.class.isAssignableFrom(parameterTypes[0])) {
            return false;
        }
        for (int i = 1; i < parameterTypes.length; ++i) {
            if (!parameterTypes[i].equals(retryableClass[i - 1])) {
                return false;
            }
        }
        return true;
    }

    private Callable<Object> buildCallable(ProceedingJoinPoint joinPoint, Object[] args) {
        return () -> {
            try {
                return joinPoint.proceed(args);
            } catch (Exception e) {
                throw e;
            } catch (Throwable e) {
                throw new IllegalStateException(e);
            }
        };
    }
}
