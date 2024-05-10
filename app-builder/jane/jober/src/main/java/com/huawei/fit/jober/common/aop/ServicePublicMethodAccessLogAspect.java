/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.common.aop;

import com.huawei.fit.jober.common.model.LogSubject;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.aop.ProceedingJoinPoint;
import com.huawei.fitframework.aop.annotation.Around;
import com.huawei.fitframework.aop.annotation.Aspect;
import com.huawei.fitframework.aop.annotation.Pointcut;
import com.huawei.fitframework.log.Logger;

import java.time.LocalDateTime;

/**
 * TaskCenter
 *
 * @author d30022216
 * @since 2023-09-08
 **/
@Aspect
@Component
public class ServicePublicMethodAccessLogAspect {
    private static final Logger log = Logger.get(ServicePublicMethodAccessLogAspect.class);

    /**
     * 操作日志切入点
     */
    @Pointcut(value = "execution(public * com.huawei.fit.jober.taskcenter.service..*.*(..))")
    public void servicePublicMethodPc() {
    }

    /**
     * 围绕方法打印日志
     *
     * @param pjp the pjp
     * @return the object
     * @throws Throwable the throwable
     */
    @Around("servicePublicMethodPc()")
    public Object doAround(ProceedingJoinPoint pjp) throws Throwable {
        LogSubject logSubject = new LogSubject();
        // 记录时间
        long startTime = System.currentTimeMillis();
        // 接口请求时间
        logSubject.setStartTime(LocalDateTime.now().toString());
        // 访问的方法路径+名称
        logSubject.setMethodFullName(pjp.getTarget().getClass() + "." + pjp.getMethod().getName());

        Object result;
        try {
            // 执行结果
            result = pjp.proceed();
        } finally {
            // 执行消耗时间
            long endTime = System.currentTimeMillis();
            double spentSeconds = (endTime - startTime) / 1000.0;
            if (spentSeconds > 3) {
                logSubject.setSpentSeconds(spentSeconds);
                log.info(logSubject.toString());
            }
        }
        return result;
    }
}