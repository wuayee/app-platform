/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.controller;

import com.huawei.fit.http.annotation.GetMapping;
import com.huawei.fit.http.annotation.PathVariable;
import com.huawei.fit.http.annotation.PostMapping;
import com.huawei.fit.http.annotation.RequestMapping;
import com.huawei.fit.http.annotation.ResponseStatus;
import com.huawei.fit.http.entity.FileEntity;
import com.huawei.fit.http.protocol.HttpResponseStatus;
import com.huawei.fit.http.server.HttpClassicServerRequest;
import com.huawei.fit.http.server.HttpClassicServerResponse;
import com.huawei.fit.jane.task.domain.File;
import com.huawei.fit.jane.task.gateway.Authenticator;
import com.huawei.fit.jane.task.util.OperationContext;
import com.huawei.fit.jober.common.ErrorCodes;
import com.huawei.fit.jober.common.exceptions.BadRequestException;
import com.huawei.fit.jober.common.util.ParamUtils;
import com.huawei.fit.jober.taskcenter.validation.FileValidator;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.log.Logger;
import com.huawei.fitframework.util.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * 为文件的管理提供 REST 风格 API。
 *
 * @author 陈镕希 c00572808
 * @since 2023-10-10
 */
@Component
@RequestMapping(value = "/v1/files", group = "文件管理接口")
public class FileController extends AbstractController {
    private static final Logger log = Logger.get(FileController.class);

    private final File.Repo repo;

    private final FileValidator validator;

    /**
     * 构造函数
     *
     * @param authenticator 授权校验器
     * @param repo 文件数据层
     * @param validator 文件校验器
     */
    public FileController(Authenticator authenticator, File.Repo repo, FileValidator validator) {
        super(authenticator);
        this.repo = repo;
        this.validator = validator;
    }

    /**
     * upload file.
     *
     * @param httpRequest httpRequest
     * @param httpResponse httpResponse
     * @return Map<String, Object>
     */
    @PostMapping(summary = "上传文件")
    @ResponseStatus(HttpResponseStatus.CREATED)
    public Map<String, Object> upload(HttpClassicServerRequest httpRequest, HttpClassicServerResponse httpResponse) {
        OperationContext context = this.contextOf(httpRequest, null);
        int contentLength = httpRequest.headers()
                .first("Content-Length")
                .map(Integer::valueOf)
                .orElseThrow(() -> new BadRequestException(ErrorCodes.FILE_CONTENT_LENGTH_NOT_ANNOUNCE));
        validator.contentLength(contentLength, context);
        if (!httpRequest.contentType().isPresent()) {
            throw new BadRequestException(ErrorCodes.FILE_TYPE_NOT_DEFINE, ParamUtils.convertOperationContext(context));
        }
        if (StringUtils.startsWithIgnoreCase(httpRequest.contentType().get().mediaType(), "multipart/")) {
            throw new BadRequestException(ErrorCodes.FILE_TYPE_NOT_SUPPORT_MULTIPART);
        }
        String fileName = httpRequest.headers()
                .first("fileName")
                .orElseThrow(() -> new BadRequestException(ErrorCodes.FILE_NAME_NOT_ANNOUNCE));
        File.Declaration declaration = null;
        try {
            declaration = File.Declaration.custom()
                    .name(URLDecoder.decode(fileName, StandardCharsets.UTF_8.name()))
                    .content(httpRequest.entityBytes())
                    .build();
        } catch (UnsupportedEncodingException e) {
            log.error("File name escape error.");
            throw new BadRequestException(ErrorCodes.FILE_NAME_NOT_ESCAPE);
        }
        File entity = repo.upload(declaration, context);
        return Views.viewOf(entity);
    }

    /**
     * download file.
     *
     * @param httpRequest httpRequest
     * @param httpResponse httpResponse
     * @param fileId fileId
     * @return Map<String, Object>
     */
    @GetMapping(value = "/{file_id}", summary = "下载文件")
    @ResponseStatus(HttpResponseStatus.OK)
    public FileEntity download(HttpClassicServerRequest httpRequest, HttpClassicServerResponse httpResponse,
            @PathVariable("file_id") String fileId) {
        File file = repo.download(fileId, this.contextOf(httpRequest, null));
        String fileName;
        try {
            fileName = URLEncoder.encode(StringUtils.substringAfter(file.name(), "/"), StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            log.error("File name escape error.");
            throw new BadRequestException(ErrorCodes.FILE_NAME_NOT_ESCAPE);
        }
        return FileEntity.createAttachment(httpResponse, fileName, new ByteArrayInputStream(file.content()),
                file.content().length);
    }
}
