/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.aipp.controller;

import static com.huawei.fit.jober.aipp.enums.FileExtensionEnum.getFileExtension;

import com.huawei.fit.http.annotation.GetMapping;
import com.huawei.fit.http.annotation.PathVariable;
import com.huawei.fit.http.annotation.PostMapping;
import com.huawei.fit.http.annotation.RequestHeader;
import com.huawei.fit.http.annotation.RequestMapping;
import com.huawei.fit.http.annotation.RequestParam;
import com.huawei.fit.http.entity.FileEntity;
import com.huawei.fit.http.entity.NamedEntity;
import com.huawei.fit.http.entity.PartitionedEntity;
import com.huawei.fit.http.server.HttpClassicServerRequest;
import com.huawei.fit.http.server.HttpClassicServerResponse;
import com.huawei.fit.jane.common.controller.AbstractController;
import com.huawei.fit.jane.common.response.Rsp;
import com.huawei.fit.jane.task.gateway.Authenticator;
import com.huawei.fit.jober.aipp.common.exception.AippErrCode;
import com.huawei.fit.jober.aipp.common.exception.AippException;
import com.huawei.fit.jober.aipp.dto.FileRspDto;
import com.huawei.fit.jober.aipp.enums.FileExtensionEnum;
import com.huawei.fit.jober.aipp.service.OperatorService;
import com.huawei.fit.jober.aipp.service.UploadedFileManageService;
import com.huawei.fit.jober.aipp.util.AippFileUtils;
import com.huawei.fit.jober.aipp.util.AippStringUtils;
import com.huawei.fit.jober.aipp.util.HttpUtils;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.annotation.Value;
import com.huawei.fitframework.log.Logger;
import com.huawei.fitframework.util.StringUtils;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequestMapping(path = "/v1/api/{tenant_id}", group = "文件相关操作")
public class FileController extends AbstractController {
    private static final Logger log = Logger.get(FileController.class);
    private final int xiaoHaiReadTimeout;
    private final UploadedFileManageService uploadedFileManageService;
    private final OperatorService operatorService;

    public FileController(Authenticator authenticator, UploadedFileManageService uploadedFileManageService,
            @Value("${model.xiaohai.read-timeout}") int xiaoHaiReadTimeout, OperatorService operatorService) {
        super(authenticator);
        this.xiaoHaiReadTimeout = xiaoHaiReadTimeout;
        this.uploadedFileManageService = uploadedFileManageService;
        this.operatorService = operatorService;
    }

    private static String generateUniqueFileName(String fileName) {
        return UUID.randomUUID() + "." + getFileExtension(fileName);
    }

    private byte[] downloadFromRemote(String base64fileUrl) throws IOException {
        String baseUrl = new String(Base64.getDecoder().decode(base64fileUrl.getBytes(StandardCharsets.UTF_8)),
                StandardCharsets.UTF_8);
        log.info("getFile url={}", baseUrl);
        HttpGet httpGet = new HttpGet(baseUrl);
        httpGet.setConfig(HttpUtils.requestConfig(this.xiaoHaiReadTimeout));
        try (CloseableHttpResponse response = HttpUtils.execute(httpGet)) {
            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                throw new IOException(String.format(Locale.ROOT,
                        "send http fail. url=%s result=%d",
                        httpGet.getURI(),
                        response.getStatusLine().getStatusCode()));
            }
            return EntityUtils.toByteArray(response.getEntity());
        }
    }

    /**
     * 从远端下载文件或者从nas下载文件
     *
     * @param base64fileUrl 经过Base64编码的URL
     * @param fileName 文件名称
     * @param httpClassicServerResponse fit注入httpResponse
     * @return 文件
     * @throws IOException 下载异常
     */
    @GetMapping(path = "/file", description = "下载文件")
    public FileEntity getFile(HttpClassicServerRequest httpRequest, @PathVariable("tenant_id") String tenantId,
            @RequestParam(value = "fileUrl", required = false) String base64fileUrl,
            @RequestParam(value = "filePath", required = false) String fileCanonicalPath,
            @RequestParam(value = "fileName") String fileName, HttpClassicServerResponse httpClassicServerResponse)
            throws IOException {
        byte[] buffer;
        if (StringUtils.isNotBlank(base64fileUrl)) {
            buffer = downloadFromRemote(base64fileUrl);
        } else if (StringUtils.isNotBlank(fileCanonicalPath)) {
            Path filePath = Paths.get(fileCanonicalPath);
            if (!filePath.toFile().exists()) {
                throw new AippException(this.contextOf(httpRequest, tenantId), AippErrCode.FILE_EXPIRED_OR_BROKEN);
            }
            buffer = Files.readAllBytes(filePath);
        } else {
            throw new IllegalArgumentException("base64fileUrl and fileCanonicalPath are empty");
        }
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(buffer);
        return FileEntity.createAttachment(httpClassicServerResponse,
                fileName,
                byteArrayInputStream,
                byteArrayInputStream.available());
    }

    @PostMapping(path = "/file", description = "上传文件")
    public Rsp<FileRspDto> uploadFile(HttpClassicServerRequest httpRequest, @PathVariable("tenant_id") String tenantId,
            @RequestHeader(value = "attachment-filename", defaultValue = "blank") String fileName,
            @RequestParam(value = "aipp_id", required = false) String aippId, PartitionedEntity receivedFile)
            throws IOException {
        String uniqueFileName = generateUniqueFileName(fileName);
        log.info("upload file fileName={} uniqueFileName={}", fileName, uniqueFileName);
        File targetFile = Paths.get(AippFileUtils.NAS_SHARE_DIR, uniqueFileName).toFile();

        List<NamedEntity> entities =
                receivedFile.entities().stream().filter(NamedEntity::isFile).collect(Collectors.toList());
        try (InputStream inStream = entities.get(0).asFile().getInputStream()) {
            FileUtils.copyInputStreamToFile(inStream, targetFile);
            uploadedFileManageService.addFileRecord(aippId,
                    contextOf(httpRequest, tenantId).getW3Account(),
                    targetFile.getCanonicalPath());
        } catch (IOException e) {
            Path fileToDeletePath = Paths.get(targetFile.getPath());
            if (Files.exists(fileToDeletePath)) {
                try {
                    Files.delete(fileToDeletePath);
                } catch (IOException e1) {
                    log.error("Failed to delete file.", e1);
                }
            }
            log.error("write file={} fail.", fileName, e);
            // 待添加 context 入参， 否则国际化未生效
            throw new AippException(AippErrCode.UPLOAD_FAILED);
        }
        log.info("upload file fileName={} uniqueFileName={} success.", fileName, uniqueFileName);
        return Rsp.ok(FileRspDto.builder()
                .fileName(fileName)
                .filePath(targetFile.getCanonicalPath())
                .fileType(getFileExtension(fileName))
                .build());
    }

    @GetMapping(path = "/file/content", description = "获取文件内容")
    public String extractFileContent(@RequestParam(value = "filePath") String filePath,
            @RequestParam(value = "token", defaultValue = "20000") Integer token) {
        File file = Paths.get(filePath).toFile();
        String fileContent = this.operatorService.fileExtractor(
                file, FileExtensionEnum.findType(file.getName()));
        return AippStringUtils.textLenLimit(fileContent, token);
    }
}
