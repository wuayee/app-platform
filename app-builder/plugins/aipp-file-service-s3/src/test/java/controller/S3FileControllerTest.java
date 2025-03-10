/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import modelengine.fit.http.HttpMessage;
import modelengine.fit.http.entity.Entity;
import modelengine.fit.http.entity.FileEntity;
import modelengine.fit.http.entity.NamedEntity;
import modelengine.fit.http.entity.PartitionedEntity;
import modelengine.fit.http.entity.support.DefaultNamedEntity;
import modelengine.fit.http.entity.support.DefaultPartitionedEntity;
import modelengine.fit.jade.aipp.s3.file.controller.S3FileController;
import modelengine.fit.jade.aipp.s3.file.entity.S3FileMetaEntity;
import modelengine.fit.jade.aipp.s3.file.service.S3Service;
import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.test.annotation.FitTestWithJunit;
import modelengine.fitframework.test.annotation.Mock;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

/**
 * 测试 S3FileController。
 *
 * @author 兰宇晨
 * @since 2024-12-27
 */
@DisplayName("测试 S3FileController")
// 框架当前不支持PartitionedEntity的报文测试，待后续支持后修改为 MvcTest
@FitTestWithJunit(includeClasses = S3FileController.class)
public class S3FileControllerTest {
    @Fit
    S3FileController fileController;

    @Mock
    private S3Service s3Service;

    @Test
    @DisplayName("测试上传 S3 文件成功")
    void shouldReturnOkWhenUpload() throws IOException {
        S3FileMetaEntity metaEntity = new S3FileMetaEntity("name", "url", "type");
        when(this.s3Service.upload(any(), anyLong(), anyString())).thenReturn(metaEntity);
        Entity entity = FileEntity.createAttachment(mock(HttpMessage.class), "test.txt", mock(InputStream.class), 100);
        NamedEntity namedEntity = new DefaultNamedEntity(mock(HttpMessage.class), "generic", entity);
        PartitionedEntity partitionedEntity =
                new DefaultPartitionedEntity(mock(HttpMessage.class), Collections.singletonList(namedEntity));
        List<S3FileMetaEntity> s3FileMetaEntities = this.fileController.upload(partitionedEntity);
        assertThat(s3FileMetaEntities).isNotEmpty();
        assertThat(s3FileMetaEntities.get(0)).extracting(S3FileMetaEntity::getFileName,
                S3FileMetaEntity::getFileUrl,
                S3FileMetaEntity::getFileType).containsExactly("name", "url", "type");
    }
}
