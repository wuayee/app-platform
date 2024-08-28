/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.aipp.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.huawei.fit.dynamicform.DynamicFormMetaService;
import com.huawei.fit.dynamicform.DynamicFormService;
import com.huawei.fit.jane.meta.multiversion.MetaInstanceService;
import com.huawei.fit.jane.meta.multiversion.MetaService;
import com.huawei.fit.jober.FlowInstanceService;
import com.huawei.fit.jober.aipp.common.exception.AippException;
import com.huawei.fit.jober.aipp.service.impl.AippRunTimeServiceImpl;

import modelengine.fit.http.client.HttpClassicClientFactory;
import modelengine.fit.http.client.HttpClassicClientRequest;
import modelengine.fit.http.client.HttpClassicClientResponse;
import modelengine.fit.http.entity.ObjectEntity;
import modelengine.fitframework.util.MapBuilder;
import modelengine.fitframework.util.ObjectUtils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * AippRuntimeService的share接口测试类
 *
 * @since 2024-08-01
 */
public class AippShareTest {
    @InjectMocks
    private AippRunTimeServiceImpl runTimeService;
    @Mock
    private AopAippLogService aopAippLogServiceMock;
    @Mock
    private DynamicFormMetaService dynamicFormMetaServiceMock;
    @Mock
    private MetaService metaServiceMock;
    @Mock
    private DynamicFormService dynamicFormServiceMock;
    @Mock
    private MetaInstanceService metaInstanceServiceMock;
    @Mock
    private FlowInstanceService flowInstanceServiceMock;
    @Mock
    private UploadedFileManageService uploadedFileManageServiceMock;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private HttpClassicClientFactory httpClientFactoryMock;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetShareData() {
        HttpClassicClientRequest requestMock = mock(HttpClassicClientRequest.class);
        HttpClassicClientResponse<Object> responseMock = mock(HttpClassicClientResponse.class);
        when(httpClientFactoryMock.create().createRequest(any(), any())).thenReturn(requestMock);
        ObjectEntity objectEntityMock = mock(ObjectEntity.class);
        when(requestMock.exchange()).thenReturn(responseMock);
        when(responseMock.statusCode()).thenReturn(200);
        when(responseMock.objectEntity()).thenReturn(Optional.of(objectEntityMock));
        Map<String, Object> mockMap = MapBuilder.<String, Object>get().put("result", 123).build();
        when(objectEntityMock.object()).thenReturn(mockMap);
        Map<String, Object> res = runTimeService.getShareData(null);
        Assertions.assertEquals(123, ObjectUtils.<Integer>cast(res.get("result")));
    }

    @Test
    void testGetShareDataException() {
        HttpClassicClientRequest requestMock = mock(HttpClassicClientRequest.class);
        HttpClassicClientResponse<Object> responseMock = mock(HttpClassicClientResponse.class);
        when(httpClientFactoryMock.create().createRequest(any(), any())).thenReturn(requestMock);
        when(requestMock.exchange()).thenReturn(responseMock);
        when(responseMock.statusCode()).thenReturn(404);
        Assertions.assertThrows(AippException.class, () -> runTimeService.getShareData(null));
    }

    @Test
    void testShared() {
        List<Map<String, Object>> chats = new LinkedList<>();
        HttpClassicClientRequest requestMock = mock(HttpClassicClientRequest.class);
        HttpClassicClientResponse<Object> responseMock = mock(HttpClassicClientResponse.class);
        when(httpClientFactoryMock.create().createRequest(any(), any())).thenReturn(requestMock);
        ObjectEntity objectEntityMock = mock(ObjectEntity.class);
        when(requestMock.exchange()).thenReturn(responseMock);
        when(responseMock.statusCode()).thenReturn(200);
        when(responseMock.objectEntity()).thenReturn(Optional.of(objectEntityMock));
        Map<String, Object> mockMap = MapBuilder.<String, Object>get().put("result", 123).build();
        when(objectEntityMock.object()).thenReturn(mockMap);
        Map<String, Object> res = runTimeService.shared(chats);
        Assertions.assertEquals(123, ObjectUtils.<Integer>cast(res.get("result")));
    }

    @Test
    void testSharedException() {
        List<Map<String, Object>> chats = new LinkedList<>();
        HttpClassicClientRequest requestMock = mock(HttpClassicClientRequest.class);
        HttpClassicClientResponse<Object> responseMock = mock(HttpClassicClientResponse.class);
        when(httpClientFactoryMock.create().createRequest(any(), any())).thenReturn(requestMock);
        when(requestMock.exchange()).thenReturn(responseMock);
        when(responseMock.statusCode()).thenReturn(404);
        Assertions.assertThrows(AippException.class, () -> runTimeService.shared(chats));
    }
}
