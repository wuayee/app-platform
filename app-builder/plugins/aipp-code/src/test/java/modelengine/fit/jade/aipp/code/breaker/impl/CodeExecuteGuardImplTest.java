/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.code.breaker.impl;

import static modelengine.fitframework.util.ObjectUtils.cast;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.github.benmanes.caffeine.cache.Cache;

import modelengine.fit.jade.aipp.code.breaker.CircuitBreaker;
import modelengine.fit.jade.aipp.code.breaker.CodeExecuteGuard;
import modelengine.fit.jade.aipp.code.code.CodeExecuteRetCode;
import modelengine.fit.jade.aipp.code.command.CodeExecuteCommand;
import modelengine.fit.jade.aipp.code.config.CircuitBreakerConfig;
import modelengine.fit.jade.aipp.code.domain.entity.ProgrammingLanguage;
import modelengine.fit.jade.aipp.code.util.Constant;
import modelengine.fit.jade.aipp.code.util.HashUtil;
import modelengine.fitframework.exception.FitException;
import modelengine.fitframework.util.ReflectionUtils;
import modelengine.jade.common.exception.ModelEngineException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Deque;
import java.util.HashMap;
import java.util.function.Supplier;

/**
 * 表示 {@link CodeExecuteGuardImpl} 的测试用例。
 *
 * @author 邱晓霞
 * @since 2025-01-15
 */
@DisplayName("测试 CodeExecuteGuardImpl")
public class CodeExecuteGuardImplTest {
    private CodeExecuteGuard codeExecuteGuard;

    private CircuitBreakerConfig circuitBreakerConfig;

    @BeforeEach
    void setUp() {
        this.circuitBreakerConfig = new CircuitBreakerConfig(80, 1, 2);
        this.codeExecuteGuard = new CodeExecuteGuardImpl(circuitBreakerConfig, 60L, 100L);
    }

    @Test
    void shouldApplyCircuitBreakerWhenCodeExecuteCommandValid() {
        CodeExecuteCommand command = new CodeExecuteCommand(new HashMap<>(), "valid code", ProgrammingLanguage.PYTHON);
        Supplier<Object> codeExecuteResultSupplier = () -> "execution result";

        Object result = this.codeExecuteGuard.apply(command, codeExecuteResultSupplier);

        assertNotNull(result);
        assertEquals("execution result", result);
    }

    @Test
    @DisplayName("当过期时间参数校验不通过时，抛出异常")
    void shouldThrowExceptionWhenExpireAfterAccessIsInValid() {
        assertThrows(IllegalArgumentException.class,
                () -> new CodeExecuteGuardImpl(new CircuitBreakerConfig(50, 1, 6), -1, 1));
    }

    @Test
    @DisplayName("当缓存容量参数校验不通过时，抛出异常")
    void shouldThrowExceptionWhenMaximumCacheSizeIsInvalid() {
        assertThrows(IllegalArgumentException.class,
                () -> new CodeExecuteGuardImpl(new CircuitBreakerConfig(50, 1, 6), 1, -1));
    }

    @Test
    @DisplayName("代码执行指令为空，抛出异常")
    void shouldThrowExceptionWhenCodeExecuteCommandIsNull() {
        Supplier<Object> codeExecuteResultSupplier = () -> "execution result";
        assertThatThrownBy(() -> this.codeExecuteGuard.apply(null, codeExecuteResultSupplier)).isInstanceOf(
                IllegalArgumentException.class).hasMessageContaining("Command cannot be null");
    }

    @Test
    @DisplayName("代码执行成功，记录成功")
    void shouldRecordTrueWhenCodeExecuteSuccess() {
        String code = "success code";
        CodeExecuteCommand command = new CodeExecuteCommand(new HashMap<>(), code, ProgrammingLanguage.PYTHON);
        Supplier<Object> codeExecuteResultSupplier = () -> "execution result";
        this.codeExecuteGuard.apply(command, codeExecuteResultSupplier);
        CircuitBreaker circuitBreaker = ((Cache<String, CircuitBreaker>) ReflectionUtils.getField(this.codeExecuteGuard,
                "circuitBreakerCache")).getIfPresent(HashUtil.hash(code));
        Deque<Boolean> slidingWindow = cast(ReflectionUtils.getField(circuitBreaker, "slidingWindow"));
        assertEquals(slidingWindow.size(), 1);
        assertEquals(slidingWindow.getFirst(), true);
    }

    @Test
    @DisplayName("代码执行超时，记录失败")
    void shouldRecordFalseWhenCodeExecuteTimeOut() {
        String code = "fake code";
        String msg = "Execution timed out";
        CodeExecuteCommand command = new CodeExecuteCommand(new HashMap<>(), code, ProgrammingLanguage.PYTHON);
        Supplier<Object> codeExecuteResultSupplier = () -> {
            throw new FitException(Constant.TIME_OUT_CODE, msg);
        };
        assertThatThrownBy(() -> this.codeExecuteGuard.apply(command, codeExecuteResultSupplier)).isInstanceOf(
                ModelEngineException.class).hasMessageContaining(msg);
        CircuitBreaker circuitBreaker = ((Cache<String, CircuitBreaker>) ReflectionUtils.getField(this.codeExecuteGuard,
                "circuitBreakerCache")).getIfPresent(HashUtil.hash(code));
        Deque<Boolean> slidingWindow = cast(ReflectionUtils.getField(circuitBreaker, "slidingWindow"));
        assertEquals(slidingWindow.size(), 1);
        assertEquals(slidingWindow.getFirst(), false);
    }

    @Test
    @DisplayName("代码因超时多次被限制运行，抛出异常")
    void shouldFailWhenStateIsOPEN() {
        String msg = "Execution timed out";
        CodeExecuteCommand command = new CodeExecuteCommand(new HashMap<>(), "fake code", ProgrammingLanguage.PYTHON);
        Supplier<Object> codeExecuteResultSupplier = () -> {
            throw new FitException(Constant.TIME_OUT_CODE, msg);
        };
        assertThatThrownBy(() -> this.codeExecuteGuard.apply(command, codeExecuteResultSupplier)).isInstanceOf(
                ModelEngineException.class).hasMessageContaining(msg);
        Supplier<Object> codeExecuteNormalResultSupplier = () -> "execution result";
        assertThatThrownBy(() -> this.codeExecuteGuard.apply(command, codeExecuteNormalResultSupplier)).isInstanceOf(
                ModelEngineException.class).hasMessageContaining(CodeExecuteRetCode.CODE_EXECUTE_RESTRICTION.getMsg());
    }
}
