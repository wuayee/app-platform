/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.common.aop;

import modelengine.fit.jober.common.model.LogSubject;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.aop.ProceedingJoinPoint;
import modelengine.fitframework.aop.annotation.Around;
import modelengine.fitframework.aop.annotation.Aspect;
import modelengine.fitframework.aop.annotation.Pointcut;
import modelengine.fitframework.log.Logger;

import java.time.LocalDateTime;

/**
 * TaskCenter
 *
 * @author 董建华
 * @since 2023-09-08
 **/
@Aspect
@Component
public class ServicePublicMethodAccessLogAspect {
    private static final Logger log = Logger.get(ServicePublicMethodAccessLogAspect.class);

    /**
     * 操作日志切入点
     */
    @Pointcut(value = "execution(public * modelengine.fit.jober.taskcenter.service..*.*(..))")
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
            double spentSeconds = (endTime - startTime) / 1000.0d;
            if (spentSeconds > 3) {
                logSubject.setSpentSeconds(spentSeconds);
                log.info(logSubject.toString());
            }
        }
        return result;
    }
}