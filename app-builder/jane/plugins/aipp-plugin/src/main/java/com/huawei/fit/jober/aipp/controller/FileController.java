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
import com.huawei.fit.http.client.HttpClassicClientFactory;
import com.huawei.fit.http.client.HttpClassicClientRequest;
import com.huawei.fit.http.client.HttpClassicClientResponse;
import com.huawei.fit.http.entity.FileEntity;
import com.huawei.fit.http.entity.NamedEntity;
import com.huawei.fit.http.entity.PartitionedEntity;
import com.huawei.fit.http.protocol.HttpRequestMethod;
import com.huawei.fit.http.protocol.HttpResponseStatus;
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

/**
 * 文件接口
 *
 * @author 孙怡菲 s00664640
 * @since 2024-05-10
 */
@Component
@RequestMapping(path = "/v1/api/{tenant_id}", group = "文件相关操作")
public class FileController extends AbstractController {
    private static final Logger log = Logger.get(FileController.class);

    private final int xiaoHaiReadTimeout;
    private final UploadedFileManageService uploadedFileManageService;
    private final OperatorService operatorService;
    private final HttpClassicClientFactory factory;

    /**
     * 构造函数
     *
     * @param authenticator 认证器
     * @param uploadedFileManageService 文件管理服务
     * @param xiaoHaiReadTimeout 小海读取超时时间
     * @param operatorService 操作服务
     */
    public FileController(Authenticator authenticator, UploadedFileManageService uploadedFileManageService,
            @Value("${model.xiaohai.read-timeout}") int xiaoHaiReadTimeout, OperatorService operatorService,
            HttpClassicClientFactory factory) {
        super(authenticator);
        this.xiaoHaiReadTimeout = xiaoHaiReadTimeout;
        this.uploadedFileManageService = uploadedFileManageService;
        this.operatorService = operatorService;
        this.factory = factory;
    }

    /**
     * 上传文件
     *
     * @param fileName 租户ID
     * @return 文件响应DTO
     */
    private static String generateUniqueFileName(String fileName) {
        return UUID.randomUUID() + "." + getFileExtension(fileName);
    }

    /**
     * 从远程下载文件
     *
     * @param base64fileUrl 经过Base64编码的URL
     * @return 文件字节数组
     * @throws IOException 下载异常
     */
    private byte[] downloadFromRemote(String base64fileUrl) throws IOException {
        String baseUrl = new String(Base64.getDecoder().decode(base64fileUrl.getBytes(StandardCharsets.UTF_8)),
                StandardCharsets.UTF_8);
        log.info("getFile url={}", baseUrl);
        HttpClassicClientRequest request = factory.create(HttpUtils.requestConfig(this.xiaoHaiReadTimeout))
                .createRequest(HttpRequestMethod.GET, baseUrl);
        try (HttpClassicClientResponse<Object> response = HttpUtils.execute(request)) {
            if (response.statusCode() != HttpResponseStatus.OK.statusCode()) {
                throw new IOException(String.format(Locale.ROOT,
                        "send http fail. url=%s result=%d",
                        request.requestUri(),
                        response.statusCode()));
            }
            return response.entityBytes();
        }
    }

    /**
     * 从远端下载文件或者从nas下载文件
     *
     * @param httpRequest Http请求
     * @param tenantId 租户ID
     * @param base64fileUrl 经过Base64编码的URL
     * @param fileCanonicalPath 文件的规范路径
     * @param fileName 文件名
     * @param httpClassicServerResponse Http响应
     * @return 文件实体
     * @throws IOException 文件读取异常
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

    /**
     * 上传文件
     *
     * @param httpRequest Http请求
     * @param tenantId 租户ID
     * @param fileName 文件名
     * @param aippId AIPP ID
     * @param receivedFile 接收到的文件
     * @return 文件响应DTO
     * @throws IOException 文件读取异常
     */
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

    /**
     * 获取文件内容
     *
     * @param filePath 文件路径
     * @param token 文本长度限制
     * @return 文件内容
     */
    @GetMapping(path = "/file/content", description = "获取文件内容")
    public String extractFileContent(@RequestParam(value = "filePath") String filePath,
            @RequestParam(value = "token", defaultValue = "20000") Integer token) {
        File file = Paths.get(filePath).toFile();
        String fileContent = this.operatorService.fileExtractor(
                file, FileExtensionEnum.findType(file.getName()));
        return AippStringUtils.textLenLimit(fileContent, token);
    }
}
