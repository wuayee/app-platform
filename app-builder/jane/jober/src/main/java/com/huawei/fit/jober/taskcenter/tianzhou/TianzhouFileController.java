/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.tianzhou;

import com.huawei.fit.jober.taskcenter.controller.FileController;

import lombok.RequiredArgsConstructor;
import modelengine.fit.http.annotation.GetMapping;
import modelengine.fit.http.annotation.PathVariable;
import modelengine.fit.http.annotation.PostMapping;
import modelengine.fit.http.annotation.RequestMapping;
import modelengine.fit.http.annotation.ResponseStatus;
import modelengine.fit.http.entity.FileEntity;
import modelengine.fit.http.protocol.HttpResponseStatus;
import modelengine.fit.http.server.HttpClassicServerRequest;
import modelengine.fit.http.server.HttpClassicServerResponse;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.plugin.Plugin;

import java.util.Map;

/**
 * 为文件提供 REST 风格 API。
 *
 * @author 陈镕希
 * @since 2023-10-12
 */
@Component
@RequestMapping(value = "/v1/jane/files", group = "天舟文件管理接口")
@RequiredArgsConstructor
public class TianzhouFileController {
    private final FileController fileController;

    private final Plugin plugin;

    /**
     * upload file.
     *
     * @param httpRequest httpRequest
     * @param httpResponse httpResponse
     * @return Map<String, Object>
     */
    @PostMapping(summary = "上传文件")
    @ResponseStatus(HttpResponseStatus.OK)
    public Map<String, Object> upload(HttpClassicServerRequest httpRequest, HttpClassicServerResponse httpResponse) {
        return View.viewOf(() -> fileController.upload(httpRequest, httpResponse), plugin, httpRequest);
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
        return fileController.download(httpRequest, httpResponse, fileId);
    }
}
