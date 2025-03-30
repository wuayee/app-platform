/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.northbound.controller;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import modelengine.fit.jane.task.gateway.Authenticator;

import modelengine.fit.http.entity.NamedEntity;
import modelengine.fit.http.entity.PartitionedEntity;
import modelengine.fit.http.protocol.Address;
import modelengine.fit.http.protocol.support.DefaultMessageHeaders;
import modelengine.fit.http.server.HttpClassicServerRequest;
import modelengine.fit.http.support.DefaultCookieCollection;
import modelengine.fit.jober.aipp.common.exception.AippErrCode;
import modelengine.fit.jober.aipp.common.exception.AippException;
import modelengine.fit.jober.aipp.genericable.adapter.FileServiceAdapter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * {@link FileController} 的测试类。
 *
 * @author 曹嘉美
 * @since 2024-12-17
 */
@DisplayName("测试 FileController")
class FileControllerTest {
    private final String tenantId = "tenant123";
    private final String appId = "app456";
    private final FileServiceAdapter fileService = mock(FileServiceAdapter.class);
    private final Authenticator authenticator = mock(Authenticator.class);
    private final FileController fileController = new FileController(authenticator, fileService);
    private final PartitionedEntity receivedFile = mock(PartitionedEntity.class);
    private final HttpClassicServerRequest httpRequest = mock(HttpClassicServerRequest.class);

    @BeforeEach
    void setUp() {
        Mockito.when(httpRequest.headers()).thenReturn(new DefaultMessageHeaders());
        Mockito.when(httpRequest.cookies()).thenReturn(new DefaultCookieCollection());
        Mockito.when(httpRequest.remoteAddress())
                .thenReturn(Address.builder().hostAddress("127.0.0.1").port(6666).build());
    }

    @Test
    @DisplayName("当上传文件时，返回正确结果。")
    void shouldReturnOkWhenUploadFiles() throws IOException {
        NamedEntity namedEntity = mock(NamedEntity.class);
        when(namedEntity.isFile()).thenReturn(true);
        when(namedEntity.name()).thenReturn("file1.txt");
        List<NamedEntity> entities = Collections.singletonList(namedEntity);
        when(receivedFile.entities()).thenReturn(entities);
        assertThatCode(() -> this.fileController.uploadFile(httpRequest,
                "tenant123",
                "app456",
                receivedFile)).doesNotThrowAnyException();
        verify(fileService, times(1)).uploadFile(any(), eq(tenantId), eq("file1.txt"), eq(appId), eq(receivedFile));
    }

    @Test
    @DisplayName("当上传文件为空时，返回正确结果。")
    void shouldThrowExceptionWhenUploadNoFile() {
        when(receivedFile.entities()).thenReturn(Collections.emptyList());
        assertThatCode(() -> fileController.uploadFile(httpRequest, tenantId, appId, receivedFile)).isInstanceOf(
                AippException.class).extracting("code").isEqualTo(AippErrCode.UPLOAD_FAILED.getCode());
    }
}
