/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.code.breaker.impl;

import static modelengine.fitframework.inspection.Validation.notNull;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import modelengine.fit.jade.aipp.code.breaker.CircuitBreaker;
import modelengine.fit.jade.aipp.code.breaker.CodeExecuteGuard;
import modelengine.fit.jade.aipp.code.code.CodeExecuteRetCode;
import modelengine.fit.jade.aipp.code.command.CodeExecuteCommand;
import modelengine.fit.jade.aipp.code.config.CircuitBreakerConfig;
import modelengine.fit.jade.aipp.code.util.Constant;
import modelengine.fit.jade.aipp.code.util.HashUtil;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Value;
import modelengine.fitframework.exception.FitException;
import modelengine.fitframework.inspection.Validation;
import modelengine.jade.common.exception.ModelEngineException;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * 监视代码执行。
 *
 * @author 邱晓霞
 * @since 2025-01-13
 */
@Component
public class CodeExecuteGuardImpl implements CodeExecuteGuard {
    private final Cache<String, CircuitBreaker> circuitBreakerCache;
    private final CircuitBreakerConfig circuitBreakerConfig;
    private final long expireAfterAccess;
    private final long maximumCacheSize;

    /**
     * 表示 {@link CodeExecuteGuardImpl} 的构造方法。
     *
     * @param circuitBreakerConfig 表示熔断器配置的 {@link CircuitBreakerConfig}。
     * @param expireAfterAccess 表示访问后的过期时间的 {@code long}。
     * @param maximumCacheSize 表示缓存容量的 {@code long}。
     */
    public CodeExecuteGuardImpl(CircuitBreakerConfig circuitBreakerConfig,
            @Value("${code.breaker-manager.expire-after-access}") long expireAfterAccess,
            @Value("${code.breaker-manager.maximum-cache-size}") long maximumCacheSize) {
        this.circuitBreakerConfig = circuitBreakerConfig;
        this.expireAfterAccess = expireAfterAccess;
        this.maximumCacheSize = maximumCacheSize;
        this.circuitBreakerCache = Caffeine.newBuilder()
                .expireAfterAccess(expireAfterAccess, TimeUnit.SECONDS)
                .maximumSize(maximumCacheSize)
                .build();
    }

    @Override
    public Object apply(CodeExecuteCommand command, Supplier<Object> codeExecuteResultSupplier) {
        notNull(command, "Command cannot be null.");
        String code = command.getCode();
        String circuitBreakerId = Validation.notBlank(HashUtil.hash(code), "CircuitBreaker id cannot be blank.");
        CircuitBreaker circuitBreaker =
                this.circuitBreakerCache.get(circuitBreakerId, name -> new CircuitBreaker(this.circuitBreakerConfig));
        circuitBreaker.acquirePermission();

        boolean isSuccess = true;
        try {
            return codeExecuteResultSupplier.get();
        } catch (FitException e) {
            isSuccess = (e.getCode() != Constant.TIME_OUT_CODE);
            throw new ModelEngineException(CodeExecuteRetCode.CODE_EXECUTE_ERROR, e, e.getMessage());
        } finally {
            circuitBreaker.recordResult(isSuccess);
        }
    }
}