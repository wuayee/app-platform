/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.huawei.fit.jober.aipp.dto.CodeExecuteParamDto;
import com.huawei.fit.jober.aipp.dto.CodeExecuteResDto;
import com.huawei.fit.jober.aipp.init.serialization.AippJacksonObjectSerializer;
import com.huawei.fit.jober.aipp.service.impl.CodeExecuteServiceImpl;

import modelengine.fitframework.broker.client.BrokerClient;
import modelengine.fitframework.serialization.ObjectSerializer;

import org.junit.jupiter.api.Test;

/**
 * CodeExecuteService单元测试类
 *
 * @author 方誉州
 * @since 2024-07-11
 */
public class CodeExecuteServiceTest {
    private CodeExecuteService getMockService(String codeExecuteRes) {
        BrokerClient brokerClientMock = mock(BrokerClient.class, RETURNS_DEEP_STUBS);
        when(brokerClientMock.getRouter(anyString())
                .route(any())
                .invoke(any()))
                .thenReturn(codeExecuteRes);
        ObjectSerializer serializer =
                new AippJacksonObjectSerializer(AippJacksonObjectSerializer.DEFAULT_DATE_TIME_FORMAT);
        return new CodeExecuteServiceImpl(brokerClientMock, serializer);
    }

    @Test
    void testErrorWithUnknowLanguage() {
        String codeExecuteRes = "This is a response";
        CodeExecuteService serviceMock = getMockService(codeExecuteRes);
        CodeExecuteParamDto params = CodeExecuteParamDto.builder()
                .language("rust")
                .build();
        CodeExecuteResDto rsp = serviceMock.run(params.getArgs(), params.getCode(), params.getLanguage());
        assertEquals(false, rsp.getIsOk());
        assertEquals("Not supported language: rust", rsp.getMsg());
        assertEquals(null, rsp.getValue());
    }

    @Test
    void testExecuteCodeReturnError() {
        String codeExecuteRes = "{\"isOk\": false, \"msg\": \"error\"}";
        CodeExecuteService serviceMock = getMockService(codeExecuteRes);
        CodeExecuteParamDto params = CodeExecuteParamDto.builder()
                .language("python")
                .build();
        CodeExecuteResDto rsp = serviceMock.run(params.getArgs(), params.getCode(), params.getLanguage());
        assertEquals(false, rsp.getIsOk());
        assertEquals("error", rsp.getMsg());
        assertEquals(null, rsp.getValue());
    }

    @Test
    void testExecuteCodeReturnOk() {
        String codeExecuteRes = "{\"isOk\": true, \"value\": \"correct\"}";
        CodeExecuteService serviceMock = getMockService(codeExecuteRes);
        CodeExecuteParamDto params = CodeExecuteParamDto.builder()
                .language("python")
                .build();
        CodeExecuteResDto rsp = serviceMock.run(params.getArgs(), params.getCode(), params.getLanguage());
        assertEquals(true, rsp.getIsOk());
        assertEquals(null, rsp.getMsg());
        assertEquals("correct", rsp.getValue());
    }
}
