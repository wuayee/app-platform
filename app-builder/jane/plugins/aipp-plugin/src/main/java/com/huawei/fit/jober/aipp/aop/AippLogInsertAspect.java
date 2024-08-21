/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.aop;

import com.huawei.fit.jober.aipp.dto.aipplog.AippLogCreateDto;
import com.huawei.fit.jober.aipp.service.AippLogStreamService;
import com.huawei.fit.jober.aipp.vo.AippLogVO;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.aop.ProceedingJoinPoint;
import modelengine.fitframework.aop.annotation.Around;
import modelengine.fitframework.aop.annotation.Aspect;
import modelengine.fitframework.aop.annotation.Pointcut;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.util.ObjectUtils;

import lombok.RequiredArgsConstructor;

/**
 * 日志插入注解.
 *
 * @author 张越
 * @since 2024-05-15
 */
@Aspect
@Component
@RequiredArgsConstructor
public class AippLogInsertAspect {
    private static final Logger log = Logger.get(AippLogInsertAspect.class);

    private final AippLogStreamService aippLogStreamService;

    @Pointcut("@annotation(com.huawei.fit.jober.aipp.aop.AippLogInsert)")
    private void aippLogInsertPointCut() {
    }

    /**
     * 遇到合法的日志，就进行推送.
     *
     * @param pjp {@link ProceedingJoinPoint} 对象.
     * @return 被切函数的返回值.
     * @throws Throwable 异常.
     */
    @Around("aippLogInsertPointCut()")
    public Object tenantAuthentication(ProceedingJoinPoint pjp) throws Throwable {
        Object result = pjp.proceed();
        AippLogCreateDto dto = ObjectUtils.cast(pjp.getArgs()[0]);

        // 判断条件和插入中
        if (dto.allFieldsNotNull()) {
            this.aippLogStreamService.send(AippLogVO.fromCreateDto(dto));
        }
        return result;
    }
}
