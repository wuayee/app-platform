/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.carver.exporter;

import static modelengine.jade.carver.operation.enums.OperationLogConstant.SYS_OP_IPADDR_KEY;
import static modelengine.jade.carver.operation.enums.OperationLogConstant.SYS_OP_LANGUAGE_KEY;
import static modelengine.jade.carver.operation.enums.OperationLogConstant.SYS_OP_OPERATOR_KEY;
import static modelengine.jade.carver.operation.enums.OperationLogConstant.SYS_OP_RESULT_KEY;
import static modelengine.jade.carver.operation.enums.OperationLogConstant.SYS_OP_SUCCEED;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.when;

import modelengine.fit.http.client.HttpClassicClient;
import modelengine.fit.http.client.HttpClassicClientFactory;
import modelengine.fit.http.client.HttpClassicClientRequest;
import modelengine.fit.http.client.HttpClassicClientResponse;
import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.test.annotation.FitTestWithJunit;
import modelengine.fitframework.test.annotation.Mock;
import modelengine.fitframework.util.MapBuilder;
import modelengine.jade.carver.exporter.support.DefaultOperationLogExporter;
import modelengine.jade.carver.operation.OperationLogLocaleService;
import modelengine.jade.carver.operation.support.CompositParam;
import modelengine.jade.carver.operation.support.OperationLogFields;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Map;

/**
 * {@link DefaultOperationLogExporter} 测试类。
 *
 * @author 方誉州
 * @since 2024-08-05
 */
@FitTestWithJunit(includeClasses = {DefaultOperationLogExporter.class})
public class DefaultOperationLogExporterTest {
    private static CompositParam params;

    @Mock
    private HttpClassicClientFactory httpFactoryMock;
    @Mock
    private HttpClassicClient httpClientMock;
    @Mock
    private HttpClassicClientRequest requestMock;
    @Mock
    private HttpClassicClientResponse<Object> responseMock;
    @Mock
    private OperationLogLocaleService localeServiceMock;
    @Fit
    private OperationLogExporter operationLogExporter;

    @BeforeAll
    static void setUpParams() {
        Map<String, String> userAttribute =
                MapBuilder.<String, String>get().put("key", "hello").put("value", "world").put("cause", "test").build();
        Map<String, String> systemAttribute = MapBuilder.<String, String>get()
                .put(SYS_OP_RESULT_KEY, SYS_OP_SUCCEED)
                .put(SYS_OP_IPADDR_KEY, "127.0.0.1")
                .put(SYS_OP_LANGUAGE_KEY, "en")
                .put(SYS_OP_OPERATOR_KEY, "Admin")
                .build();
        params = new CompositParam(userAttribute, systemAttribute);
    }

    @BeforeEach
    void setUpHttpError() {
        setMockHttpClient(404);
    }

    @AfterEach
    void tearDown() {
        clearInvocations(this.responseMock);
    }

    @Test
    void testSuccessWitHttpException() {
        operationLogExporter.export("test", params);
        Mockito.verify(responseMock, Mockito.times(1)).reasonPhrase();
    }

    private void setMockHttpClient(int statusCode) {
        when(httpFactoryMock.create(any())).thenReturn(httpClientMock);
        when(httpClientMock.createRequest(any(), any())).thenReturn(requestMock);
        when(requestMock.exchange()).thenReturn(responseMock);
        when(responseMock.statusCode()).thenReturn(statusCode);
        when(responseMock.reasonPhrase()).thenReturn("test");
        when(localeServiceMock.getLocaleMessage(any(), any())).thenReturn(new OperationLogFields());
    }
}
