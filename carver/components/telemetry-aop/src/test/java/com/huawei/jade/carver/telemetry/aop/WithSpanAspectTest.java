/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.carver.telemetry.aop;

import static com.huawei.jade.carver.telemetry.aop.stub.SpanDemo.EXCEPTION_MESSAGE;
import static com.huawei.jade.carver.telemetry.aop.stub.SpanDemo.PARENT_SPAN_NAME;
import static com.huawei.jade.carver.telemetry.aop.stub.SpanDemo.SPAN_ATTRIBUTE_KEY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.huawei.fitframework.annotation.Fit;
import com.huawei.fitframework.test.annotation.FitTestWithJunit;
import com.huawei.fitframework.test.annotation.Mock;
import com.huawei.fitframework.util.MapBuilder;
import com.huawei.fitframework.util.ObjectUtils;
import com.huawei.jade.authentication.context.UserContext;
import com.huawei.jade.authentication.context.UserContextHolder;
import com.huawei.jade.carver.telemetry.aop.observers.ParamSpanAttributeInjector;
import com.huawei.jade.carver.telemetry.aop.observers.ThreadLocalSpanEventInjector;
import com.huawei.jade.carver.telemetry.aop.parsers.ComplexSpanAttributeParser;
import com.huawei.jade.carver.telemetry.aop.parsers.DefaultSpanAttributeParser;
import com.huawei.jade.carver.telemetry.aop.stub.NestedSpanTestService;
import com.huawei.jade.carver.telemetry.aop.stub.NestedSpanTestServiceImpl;
import com.huawei.jade.carver.telemetry.aop.stub.SpanDemo;
import com.huawei.jade.carver.telemetry.aop.stub.WithSpanObjectParse;
import com.huawei.jade.carver.telemetry.aop.stub.WithSpanParserDemo;
import com.huawei.jade.service.CarverGlobalOpenTelemetry;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanBuilder;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.ContextKey;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.util.Collections;

/**
 * {@link WithSpanAspect} 的测试。
 *
 * @author 刘信宏
 * @since 2024-07-25
 */
@FitTestWithJunit(includeClasses = {
        WithSpanAspect.class, SpanDemo.class, NestedSpanTestServiceImpl.class, SpanAttributeParserRepository.class,
        DefaultSpanAttributeParser.class, ComplexSpanAttributeParser.class, WithSpanParserDemo.class,
        SpanEndObserverRepository.class, ParamSpanAttributeInjector.class, ThreadLocalSpanEventInjector.class
})
public class WithSpanAspectTest {
    @Mock
    private OpenTelemetry mockOpenTelemetry;

    @Mock
    private Tracer mockTrace;

    @Mock
    private SpanBuilder mockSpanBuilder;

    @Mock
    private Span mockSpan;

    @Fit
    private SpanDemo spanDemo;

    @Fit
    private WithSpanParserDemo withSpanParserDemo;

    private MockedStatic<CarverGlobalOpenTelemetry> telemetryScopedMock;

    private MockedStatic<UserContextHolder> userContextHolderMock;

    @BeforeEach
    void setup() {
        this.telemetryScopedMock = mockStatic(CarverGlobalOpenTelemetry.class);
        this.telemetryScopedMock.when(CarverGlobalOpenTelemetry::get).thenReturn(this.mockOpenTelemetry);
        this.userContextHolderMock = mockStatic(UserContextHolder.class);
        this.userContextHolderMock.when(UserContextHolder::get).thenReturn(new UserContext("Admin", "127.0.0.1", "en"));
        when(this.mockOpenTelemetry.getTracer(any())).thenReturn(this.mockTrace);
        when(this.mockTrace.spanBuilder(any())).thenReturn(this.mockSpanBuilder);
        when(this.mockSpanBuilder.startSpan()).thenReturn(this.mockSpan);
    }

    @AfterEach
    void tearDown() {
        this.telemetryScopedMock.close();
        this.userContextHolderMock.close();
        clearInvocations(this.mockOpenTelemetry, this.mockTrace, this.mockSpanBuilder, this.mockSpan);
    }

    @Test
    @DisplayName("触发切面，成功设置 Span 属性。")
    void shouldOkWhenHandleSuccessThenSetSpanAttr() {
        String playerArg = "playerArg";
        this.spanDemo.handleSuccess(playerArg);

        verify(this.mockSpan).setAttribute(eq(SPAN_ATTRIBUTE_KEY), eq(playerArg));
        verify(this.mockSpan).end();
    }

    @Test
    @DisplayName("触发切面，业务异常，成功设置异常 Span 属性。")
    void shouldOkWhenHandleExceptionThenSetSpanEvent() {
        String playerArg = "playerArg";
        assertThatThrownBy(() -> this.spanDemo.handleException(playerArg)).isInstanceOf(IllegalStateException.class);

        verify(this.mockSpan).recordException(argThat(throwable -> {
            assertThat(throwable.getMessage()).isEqualTo(playerArg + EXCEPTION_MESSAGE);
            return true;
        }));
        verify(this.mockSpan).setStatus(eq(StatusCode.ERROR), eq(playerArg + EXCEPTION_MESSAGE));
        verify(this.mockSpan).setAttribute(eq(SPAN_ATTRIBUTE_KEY), eq(playerArg));
        verify(this.mockSpan).end();
    }

    @Test
    @DisplayName("触发嵌套切面，成功设置 Span 属性。")
    void shouldOkWhenHandleNestedSpanThenSetSpanEvent() {
        doAnswer(arg -> {
            Context argument = ObjectUtils.cast(arg.getArgument(0));
            return argument.with(ContextKey.named("opentelemetry-trace-span-key"), this.mockSpan);
        }).when(this.mockSpan).storeInContext(any(Context.class));

        String playerArg = "playerArg";
        this.spanDemo.handleNested(playerArg);

        verify(this.mockSpan, times(1)).makeCurrent();
        verify(this.mockTrace).spanBuilder(eq(PARENT_SPAN_NAME));
        verify(this.mockTrace).spanBuilder(eq(NestedSpanTestService.NESTED_SPAN_NAME));
        verify(this.mockSpan).setAttribute(eq(SPAN_ATTRIBUTE_KEY), eq(playerArg));
        verify(this.mockSpan).setAttribute(eq(NestedSpanTestService.NESTED_ATTR_KEY), eq(playerArg));
        verify(this.mockSpan, times(2)).end();
    }

    @Test
    @DisplayName("触发解析器解析基本类型，成功设置 Span 属性。")
    void shouldOkWhenMatchPrimitiveExpression() {
        Object kvObj = 10;
        this.withSpanParserDemo.handleKVParser(kvObj);
        verify(this.mockSpan).setAttribute(eq("player"), eq(""));
        verify(this.mockSpan).end();
    }

    @Test
    @DisplayName("触发解析器解析键值对，成功设置 Span 属性。")
    void shouldOkWhenMatchKVExpression() {
        Object kvObj = MapBuilder.<String, Object>get()
            .put("k1", MapBuilder.<String, Object>get().put("k2", "v").build())
            .put("k11", MapBuilder.<String, Object>get().put("k22", "v").build())
            .build();
        this.withSpanParserDemo.handleKVParser(kvObj);
        verify(this.mockSpan).setAttribute(eq("player"), eq("v"));
        verify(this.mockSpan).setAttribute(eq("player2"), eq("v"));
        verify(this.mockSpan).end();
    }

    @Test
    @DisplayName("触发解析器解析对象，成功设置 Span 属性。")
    void shouldOkWhenMatchObjectExpression() {
        Object obj = new WithSpanObjectParse.Outer(new WithSpanObjectParse.Inner("v"));
        this.withSpanParserDemo.handleObjectParser(obj);
        verify(this.mockSpan).setAttribute(eq("player"), eq("{k2=v}"));
        verify(this.mockSpan).setAttribute(eq("player2"), eq("v"));
        verify(this.mockSpan).setAttribute(eq("player3"), eq("v"));
        verify(this.mockSpan).end();
    }

    @Test
    @DisplayName("触发解析器解析列表，成功设置 Span 属性。")
    void shouldOkWhenMatchListExpression() {
        Object outers = Collections.singletonList(new WithSpanObjectParse.Outer(new WithSpanObjectParse.Inner("v1")));
        this.withSpanParserDemo.handleListParser(outers);
        verify(this.mockSpan).setAttribute(eq("player"), eq("{k2=v1}"));
        verify(this.mockSpan).setAttribute(eq("player2"), eq("v1"));
        verify(this.mockSpan).setAttribute(eq("player3"), eq(""));
        verify(this.mockSpan).end();
    }
}
