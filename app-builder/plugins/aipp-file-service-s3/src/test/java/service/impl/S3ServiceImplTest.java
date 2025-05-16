/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AccessControlList;
import com.amazonaws.services.s3.model.AmazonS3Exception;

import modelengine.fit.http.client.HttpClassicClientFactory;
import modelengine.fit.jade.aipp.s3.file.entity.S3FileMetaEntity;
import modelengine.fit.jade.aipp.s3.file.exception.S3FileException;
import modelengine.fit.jade.aipp.s3.file.param.AmazonS3Param;
import modelengine.fit.jade.aipp.s3.file.service.S3Service;
import modelengine.fit.jade.aipp.s3.file.service.impl.S3ServiceImpl;
import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.test.annotation.FitTestWithJunit;
import modelengine.fitframework.test.annotation.Mock;

import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 测试 S3ServiceImpl。
 *
 * @author 兰宇晨
 * @since 2024-12-26
 */
@FitTestWithJunit(includeClasses = {S3ServiceImpl.class, AmazonS3Param.class})
public class S3ServiceImplTest {
    @Fit
    private S3Service s3Service;
    @Fit
    private AmazonS3Param amazonS3Param;
    @Mock
    private HttpClassicClientFactory httpClientFactoryMock;
    @Mock
    private AmazonS3 amazonS3;

    @Test
    @DisplayName("上传文件成功")
    void shouldReturnOkWhenUpload() throws IOException {
        when(this.amazonS3.putObject(any(), any(), any(), any())).thenReturn(null);
        doNothing().when(this.amazonS3).setObjectAcl(any(), any(), any(AccessControlList.class));
        File file = getFile();
        try (InputStream stream = new FileInputStream(file)) {
            S3FileMetaEntity entity = this.s3Service.upload(stream, file.length(), file.getName());
            assertThat(entity).extracting(S3FileMetaEntity::getFileName,
                            S3FileMetaEntity::getFileUrl,
                            S3FileMetaEntity::getFileType)
                    .containsExactly(file.getName(), "http://mockhost/mockbucket/" + file.getName(), "tmp");
        }
    }

    @Test
    @DisplayName("上传文件异常")
    void shouldNotOkWhenUpload() throws IOException {
        when(this.amazonS3.putObject(any(), any(), any(), any())).thenThrow(new AmazonS3Exception(""));
        File file = getFile();
        try (InputStream stream = new FileInputStream(file)) {
            AssertionsForClassTypes.assertThatThrownBy(() -> this.s3Service.upload(stream,
                    file.length(),
                    file.getName())).isInstanceOf(S3FileException.class);
        }
    }

    private File getFile() {
        return new File(this.getClass().getClassLoader().getResource("test.tmp").getFile());
    }
}
