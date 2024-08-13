/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.common.audit;

import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.log.Logger;
import com.huawei.fitframework.util.ObjectUtils;
import com.huawei.jade.authentication.context.UserContext;
import com.huawei.jade.authentication.context.UserContextHolder;
import com.huawei.jade.common.po.BasePo;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;

import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;

/**
 * 表示审计信息拦截器，自动填充 {@link BasePo} 相关字段。
 *
 * @author 易文渊
 * @since 2024-08-13
 */
@Component
@Intercepts(@Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class}))
public class AuditInterceptor implements Interceptor {
    private static final Logger log = Logger.get(AuditInterceptor.class);

    @Override
    public Object intercept(Invocation invocation) throws InvocationTargetException, IllegalAccessException {
        this.fillField(invocation);
        return invocation.proceed();
    }

    private void fillField(Invocation invocation) {
        Object[] args = invocation.getArgs();
        if (args.length < 2) {
            log.warn("The method Executor#update argument count must be equal to 2.");
            return;
        }
        MappedStatement statement = ObjectUtils.cast(args[0]);
        SqlCommandType sqlType = statement.getSqlCommandType();
        if (sqlType != SqlCommandType.INSERT && sqlType != SqlCommandType.UPDATE) {
            return;
        }
        UserContext userContext = UserContextHolder.get();
        if (userContext == null) {
            log.warn("The user context is null.");
            return;
        }
        this.handleParameter(args[1], sqlType, userContext, LocalDateTime.now());
    }

    private void handleParameter(Object parameter, SqlCommandType sqlType, UserContext userContext,
            LocalDateTime dateTime) {
        if (parameter instanceof Map) {
            this.handleMap(ObjectUtils.cast(parameter), sqlType, userContext, dateTime);
        } else if (parameter instanceof Collection) {
            this.handleCollection(ObjectUtils.cast(parameter), sqlType, userContext, dateTime);
        } else {
            this.handleSingleEntity(parameter, sqlType, userContext, dateTime);
        }
    }

    private void handleSingleEntity(Object entity, SqlCommandType sqlType, UserContext userContext,
            LocalDateTime dateTime) {
        if (!(entity instanceof BasePo)) {
            return;
        }
        BasePo basePo = ObjectUtils.cast(entity);
        if (sqlType == SqlCommandType.INSERT) {
            if (basePo.getCreatedBy() == null) {
                basePo.setCreatedBy(userContext.getName());
            }
            if (basePo.getCreatedAt() == null) {
                basePo.setCreatedAt(dateTime);
            }
        }
        if (basePo.getUpdatedBy() == null) {
            basePo.setUpdatedBy(userContext.getName());
        }
        if (basePo.getUpdatedAt() == null) {
            basePo.setUpdatedAt(dateTime);
        }
    }

    private void handleCollection(Collection<?> collection, SqlCommandType sqlType, UserContext userContext,
            LocalDateTime dateTime) {
        collection.forEach(value -> this.handleParameter(value, sqlType, userContext, dateTime));
    }

    private void handleMap(Map<?, ?> map, SqlCommandType sqlType, UserContext userContext, LocalDateTime dateTime) {
        map.values().forEach(value -> this.handleParameter(value, sqlType, userContext, dateTime));
    }

    @Override
    public Object plugin(Object target) {
        if (target instanceof Executor) {
            return Plugin.wrap(target, this);
        }
        return target;
    }
}