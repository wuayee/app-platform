/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.tianzhou;

import com.huawei.fit.http.annotation.GetMapping;
import com.huawei.fit.http.annotation.PathVariable;
import com.huawei.fit.http.annotation.PostMapping;
import com.huawei.fit.http.annotation.RequestMapping;
import com.huawei.fit.http.annotation.ResponseStatus;
import com.huawei.fit.http.entity.FileEntity;
import com.huawei.fit.http.protocol.HttpResponseStatus;
import com.huawei.fit.http.server.HttpClassicServerRequest;
import com.huawei.fit.http.server.HttpClassicServerResponse;
import com.huawei.fit.jober.taskcenter.controller.FileController;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.plugin.Plugin;

import lombok.RequiredArgsConstructor;

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
