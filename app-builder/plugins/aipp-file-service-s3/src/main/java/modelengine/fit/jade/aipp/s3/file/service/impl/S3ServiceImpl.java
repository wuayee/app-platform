/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.s3.file.service.impl;

import static modelengine.fit.jade.aipp.s3.file.code.S3FileRetCode.S3_FILE_DOWNLOAD_FAILED;
import static modelengine.fit.jade.aipp.s3.file.code.S3FileRetCode.S3_FILE_NAME_INVALID;
import static modelengine.fit.jade.aipp.s3.file.code.S3FileRetCode.S3_FILE_UPLOAD_FAILED;
import static modelengine.fit.jober.aipp.constant.AippConstant.NAS_SHARE_DIR;
import static modelengine.fitframework.inspection.Validation.isInstanceOf;
import static modelengine.fitframework.util.FileUtils.extension;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.AccessControlList;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.CanonicalGrantee;
import com.amazonaws.services.s3.model.GroupGrantee;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.Owner;
import com.amazonaws.services.s3.model.Permission;

import modelengine.fit.http.client.HttpClassicClient;
import modelengine.fit.http.client.HttpClassicClientFactory;
import modelengine.fit.http.client.HttpClassicClientRequest;
import modelengine.fit.http.entity.ReadableBinaryEntity;
import modelengine.fit.http.protocol.HttpRequestMethod;
import modelengine.fit.jade.aipp.s3.file.entity.S3FileMetaEntity;
import modelengine.fit.jade.aipp.s3.file.exception.S3FileException;
import modelengine.fit.jade.aipp.s3.file.param.AmazonS3Param;
import modelengine.fit.jade.aipp.s3.file.service.S3Service;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.ioc.annotation.PreDestroy;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.util.ObjectUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;

/**
 * S3 服务实现。
 *
 * @author 兰宇晨
 * @since 2024-12-19
 */
@Component
public class S3ServiceImpl implements S3Service {
    private static final Logger LOG = Logger.get(S3ServiceImpl.class);
    private static final String URL_PATH_SEPARATOR = "/";
    private static final String HTTP_SCHEMA = "http://";
    private static final List<String> EXTS = Arrays.asList("tar.gz");

    private final AmazonS3Param amazonS3Param;
    private final HttpClassicClientFactory factory;
    private AmazonS3 amazonS3;

    public S3ServiceImpl(AmazonS3Param amazonS3Param, HttpClassicClientFactory factory, AmazonS3 amazonS3) {
        this.amazonS3Param = amazonS3Param;
        this.factory = factory;
        this.amazonS3 = amazonS3;
    }

    /**
     * 关闭使用完毕的 amazonS3 客户端。
     */
    @PreDestroy
    public void cleanup() {
        if (this.amazonS3 != null) {
            isInstanceOf(this.amazonS3, AmazonS3Client.class, "The client must be amazonS3 client").shutdown();
            LOG.info("AmazonS3 shutdown");
        }
    }

    @Override
    public S3FileMetaEntity upload(InputStream stream, long length, String fileName) {
        LOG.info("Upload file:{}.", fileName);
        try (InputStream byteInputStream = stream) {
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentLength(length);
            this.amazonS3.putObject(this.amazonS3Param.getBucket(), fileName, byteInputStream, objectMetadata);
            this.setAcl(fileName);
        } catch (AmazonS3Exception | IOException e) {
            LOG.error("Fail to upload file to S3, file name:{}.excption:{}", fileName, e);
            throw new S3FileException(S3_FILE_UPLOAD_FAILED, fileName);
        }
        String fileUrl = this.buildUrl(fileName);
        String fileType = this.getExtension(fileName);
        if (fileType.isEmpty()) {
            throw new S3FileException(S3_FILE_NAME_INVALID, fileName);
        }
        return new S3FileMetaEntity(fileName, fileUrl, fileType.substring(1));
    }

    @Override
    public File download(String fileUrl) {
        HttpClassicClient client = this.factory.create();
        HttpClassicClientRequest request = client.createRequest(HttpRequestMethod.GET, fileUrl);
        ReadableBinaryEntity entity = ObjectUtils.cast(client.exchange(request).entity().get());
        File tempFile;
        try (InputStream inputStream = entity.getInputStream()) {
            tempFile = Files.createTempFile(Paths.get(NAS_SHARE_DIR), "prefix-", "." + extension(fileUrl)).toFile();
            tempFile.deleteOnExit();
            Files.copy(inputStream, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new S3FileException(S3_FILE_DOWNLOAD_FAILED, fileUrl);
        }
        return tempFile;
    }

    private String getExtension(String fileName) {
        for (String ext : EXTS) {
            if (fileName.endsWith("." + ext)) {
                return ext;
            }
        }
        return extension(fileName);
    }

    private void setAcl(String fileName) {
        AccessControlList accessControlList = new AccessControlList();
        accessControlList.setOwner(new Owner(this.amazonS3Param.getHuaweiAppId(), this.amazonS3Param.getAppName()));
        accessControlList.grantPermission(this.getCanonicalGrantee(), Permission.FullControl);
        accessControlList.grantPermission(GroupGrantee.AllUsers, Permission.Read);
        this.amazonS3.setObjectAcl(this.amazonS3Param.getBucket(), fileName, accessControlList);
    }

    private CanonicalGrantee getCanonicalGrantee() {
        CanonicalGrantee canonicalGrantee = new CanonicalGrantee(this.amazonS3Param.getHuaweiAppId());
        canonicalGrantee.setDisplayName(this.amazonS3Param.getAppName());
        return canonicalGrantee;
    }

    private String buildUrl(String fileName) {
        return new StringJoiner(URL_PATH_SEPARATOR).add(HTTP_SCHEMA + this.amazonS3Param.getHost())
                .add(this.amazonS3Param.getBucket())
                .add(fileName)
                .toString();
    }
}