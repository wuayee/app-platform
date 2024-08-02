/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.aipp.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.huawei.fit.dynamicform.DynamicFormMetaService;
import com.huawei.fit.dynamicform.DynamicFormService;
import com.huawei.fit.http.client.HttpClassicClientFactory;
import com.huawei.fit.http.client.HttpClassicClientRequest;
import com.huawei.fit.http.client.HttpClassicClientResponse;
import com.huawei.fit.http.entity.TextEntity;
import com.huawei.fit.jane.meta.multiversion.MetaInstanceService;
import com.huawei.fit.jane.meta.multiversion.MetaService;
import com.huawei.fit.jober.FlowInstanceService;
import com.huawei.fit.jober.aipp.common.exception.AippException;
import com.huawei.fit.jober.aipp.service.impl.AippRunTimeServiceImpl;
import com.huawei.fitframework.util.ObjectUtils;

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
        TextEntity textEntityMock = mock(TextEntity.class);
        when(requestMock.exchange()).thenReturn(responseMock);
        when(responseMock.statusCode()).thenReturn(200);
        when(responseMock.textEntity()).thenReturn(Optional.of(textEntityMock));
        when(textEntityMock.content())
                .thenReturn("{\"result\":\"result\", \"matched\": true, \"complete_query\":\"query\"}");
        Map<String, Object> res = runTimeService.getShareData(null);
        Assertions.assertEquals("result", ObjectUtils.cast(res.get("result")));
        Assertions.assertTrue(ObjectUtils.<Boolean>cast(res.get("matched")));
        Assertions.assertEquals("query", ObjectUtils.cast(res.get("complete_query")));
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
        TextEntity textEntityMock = mock(TextEntity.class);
        when(requestMock.exchange()).thenReturn(responseMock);
        when(responseMock.statusCode()).thenReturn(200);
        when(responseMock.textEntity()).thenReturn(Optional.of(textEntityMock));
        when(textEntityMock.content())
                .thenReturn("{\"result\":\"result\", \"matched\": true, \"complete_query\":\"query\"}");
        Map<String, Object> res = runTimeService.shared(chats);
        Assertions.assertEquals("result", ObjectUtils.cast(res.get("result")));
        Assertions.assertTrue(ObjectUtils.<Boolean>cast(res.get("matched")));
        Assertions.assertEquals("query", ObjectUtils.cast(res.get("complete_query")));
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
