/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.common.aop;

import com.huawei.fit.jane.task.domain.Tenant;
import com.huawei.fit.jane.task.domain.TenantAccessLevel;
import com.huawei.fit.jane.task.util.OperationContext;
import com.huawei.fit.jober.common.ErrorCodes;
import com.huawei.fit.jober.common.exceptions.BadRequestException;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.aop.ProceedingJoinPoint;
import com.huawei.fitframework.aop.annotation.Around;
import com.huawei.fitframework.aop.annotation.Aspect;
import com.huawei.fitframework.aop.annotation.Pointcut;
import com.huawei.fitframework.inspection.Validation;
import com.huawei.fitframework.log.Logger;
import com.huawei.fitframework.util.StringUtils;

import lombok.RequiredArgsConstructor;

import java.util.stream.IntStream;

/**
 * 根据租户成员信息提供鉴权能力的Aop。
 *
 * @author 陈镕希
 * @since 2023-10-30
 */
@Aspect
@Component
@RequiredArgsConstructor
public class TenantAuthenticationAspect {
    private static final Logger log = Logger.get(TenantAuthenticationAspect.class);

    private final Tenant.Repo repo;

    @Pointcut("@annotation(com.huawei.fit.jober.common.aop.TenantAuthentication)")
    private void tenantPointCut() {
    }

    /**
     * 用户所属租户校验
     *
     * @param pjp the pjp
     * @return the object
     * @throws Throwable the throwable
     */
    @Around("tenantPointCut()")
    public Object tenantAuthentication(ProceedingJoinPoint pjp) throws Throwable {
        int index = IntStream.range(0, pjp.getMethod().getParameterCount())
                .filter(idx -> pjp.getMethod().getParameters()[idx].getParameterizedType() == OperationContext.class)
                .findAny()
                .orElseThrow(() -> {
                    log.error("Cannot get OperationContext type parameter.");
                    return new BadRequestException(ErrorCodes.OPERATION_CONTEXT_IS_REQUIRED);
                });
        OperationContext context = Validation.isInstanceOf(pjp.getArgs()[index], OperationContext.class,
                () -> new BadRequestException(ErrorCodes.TYPE_CONVERT_FAILED));
        String tenantId = context.tenantId();
        Tenant tenant = repo.retrieve(tenantId, context);
        if (TenantAccessLevel.PUBLIC.equals(tenant.accessLevel())) {
            return pjp.proceed();
        }
        String operator = context.operator();
        if (StringUtils.isBlank(operator)) {
            throw new BadRequestException(ErrorCodes.OPERATOR_IS_REQUIRED);
        }
        if (!tenant.isPermitted(repo, operator, context)) {
            log.error("Operation {} does not have authority to operate tenant {}", context.operator(), tenant.id());
            throw new BadRequestException(ErrorCodes.NO_OPERATE_PERMISSION);
        }
        return pjp.proceed();
    }
}
