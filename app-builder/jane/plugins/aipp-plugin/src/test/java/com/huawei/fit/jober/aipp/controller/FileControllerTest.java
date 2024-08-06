/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.aipp.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.huawei.fit.http.client.HttpClassicClientFactory;
import com.huawei.fit.http.client.HttpClassicClientRequest;
import com.huawei.fit.http.client.HttpClassicClientResponse;
import com.huawei.fit.http.entity.FileEntity;
import com.huawei.fit.http.server.HttpClassicServerResponse;
import com.huawei.fit.jane.task.gateway.Authenticator;
import com.huawei.fit.jober.aipp.service.OperatorService;
import com.huawei.fit.jober.aipp.service.UploadedFileManageService;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * FileController测试类
 *
 * @since 2024-08-01
 */
public class FileControllerTest {
    private static final HttpClassicClientFactory FACTORY_MOCK =
            mock(HttpClassicClientFactory.class, RETURNS_DEEP_STUBS);
    private static final Authenticator AUTHENTICATOR_MOCK = mock(Authenticator.class);
    private static final OperatorService OPERATOR_SERVICE_MOCK = mock(OperatorService.class);
    private static final UploadedFileManageService UPLOAD_SERVICE_MOCK = mock(UploadedFileManageService.class);

    @Test
    void testGetFile() {
        FileController fileController =
                new FileController(AUTHENTICATOR_MOCK, UPLOAD_SERVICE_MOCK, 1000, OPERATOR_SERVICE_MOCK, FACTORY_MOCK);
        String url = "http://test";
        String base64Url =
                new String(Base64.getEncoder().encode(url.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8);
        HttpClassicClientRequest requestMock = mock(HttpClassicClientRequest.class);
        HttpClassicClientResponse<Object> responseMock = mock(HttpClassicClientResponse.class);
        when(requestMock.exchange()).thenReturn(responseMock);
        when(responseMock.statusCode()).thenReturn(200);
        byte[] entityBytes = url.getBytes(StandardCharsets.UTF_8);
        when(responseMock.entityBytes()).thenReturn(entityBytes);
        when(FACTORY_MOCK.create(any()).createRequest(any(), any())).thenReturn(requestMock);
        try {
            FileEntity res =
                    fileController.getFile(null, null, base64Url, null, "1.txt", mock(HttpClassicServerResponse.class));
            byte[] buff = new byte[entityBytes.length];
            res.read(buff);
            assertEquals(url, new String(buff, StandardCharsets.UTF_8));
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    void testGetFileException() {
        FileController fileController =
                new FileController(AUTHENTICATOR_MOCK, UPLOAD_SERVICE_MOCK, 1000, OPERATOR_SERVICE_MOCK, FACTORY_MOCK);
        String url = "http://test";
        String base64Url =
                new String(Base64.getEncoder().encode(url.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8);
        HttpClassicClientRequest requestMock = mock(HttpClassicClientRequest.class);
        HttpClassicClientResponse<Object> responseMock = mock(HttpClassicClientResponse.class);
        when(requestMock.exchange()).thenReturn(responseMock);
        when(responseMock.statusCode()).thenReturn(404);
        assertThrows(IOException.class,
                () -> fileController.getFile(null,
                        null,
                        base64Url,
                        null,
                        "1.txt",
                        mock(HttpClassicServerResponse.class)));
    }
}
