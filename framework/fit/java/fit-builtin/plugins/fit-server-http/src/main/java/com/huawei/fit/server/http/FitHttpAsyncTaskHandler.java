/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2024. All rights reserved.
 */

package com.huawei.fit.server.http;

import static com.huawei.fit.http.header.HttpHeaderKey.FIT_TLV;

import com.huawei.fit.http.exception.AsyncTaskExecutionException;
import com.huawei.fit.http.exception.AsyncTaskNotCompletedException;
import com.huawei.fit.http.exception.AsyncTaskNotFoundException;
import com.huawei.fit.http.protocol.HttpResponseStatus;
import com.huawei.fit.http.server.DoHttpHandlerException;
import com.huawei.fit.http.server.HttpClassicServerRequest;
import com.huawei.fit.http.server.HttpClassicServerResponse;
import com.huawei.fit.http.server.handler.AbstractHttpHandler;
import com.huawei.fit.serialization.http.HttpUtils;
import com.huawei.fit.server.http.support.AsyncTaskExecutor;
import com.huawei.fit.server.http.util.HttpServerUtils;
import com.huawei.fitframework.broker.server.Response;
import com.huawei.fitframework.conf.runtime.WorkerConfig;
import com.huawei.fitframework.ioc.BeanContainer;
import com.huawei.fitframework.serialization.RequestMetadata;
import com.huawei.fitframework.serialization.ResponseMetadata;
import com.huawei.fitframework.serialization.TagLengthValues;
import com.huawei.fitframework.util.StringUtils;

import java.util.Optional;

/**
 * 表示处理 FIT 通信方式的处理器。
 *
 * @author 王成 w00863339
 * @since 2023-11-16
 */
public class FitHttpAsyncTaskHandler extends AbstractHttpHandler {
    private final BeanContainer container;
    private final WorkerConfig worker;

    FitHttpAsyncTaskHandler(BeanContainer container, WorkerConfig worker, StaticInfo staticInfo,
            ExecutionInfo executionInfo) {
        super(staticInfo, executionInfo);
        this.container = container;
        this.worker = worker;
    }

    @Override
    public void handle(HttpClassicServerRequest request, HttpClassicServerResponse response)
            throws DoHttpHandlerException {
        RequestMetadata metadata = this.getRequestMetadata(request);
        try {
            String sourceWorkerId = HttpUtils.getWorkerId(metadata.tagValues());
            String sourceWorkerInstanceId = HttpUtils.getWorkerInstanceId(metadata.tagValues());
            Optional<Response> resultOp =
                    AsyncTaskExecutor.INSTANCE.longPolling(sourceWorkerId, sourceWorkerInstanceId);
            if (resultOp.isPresent()) {
                HttpServerUtils.setResponseCode(response, HttpResponseStatus.OK);
                Response result = resultOp.get();
                HttpServerUtils.setResponseHeaders(response, result);
                HttpServerUtils.setResponseEntity(this.container, metadata.dataFormat(), response, result);
            } else {
                this.fail(response, metadata.dataFormat(), AsyncTaskNotCompletedException.CODE, StringUtils.EMPTY);
            }
        } catch (AsyncTaskNotFoundException | AsyncTaskExecutionException e) {
            this.fail(response, metadata.dataFormat(), e.getCode(), e.getMessage());
        }
    }

    private void fail(HttpClassicServerResponse response, int dataFormatCode, int code, String message) {
        HttpServerUtils.setResponseCode(response, HttpResponseStatus.OK);
        Response result = Response.create(ResponseMetadata.custom()
                .dataFormat(dataFormatCode)
                .code(code)
                .message(message)
                .build());
        HttpUtils.setWorkerId(result.metadata().tagValues(), this.worker.id());
        HttpUtils.setWorkerInstanceId(result.metadata().tagValues(), this.worker.instanceId());
        HttpServerUtils.setResponseHeaders(response, result);
    }

    private RequestMetadata getRequestMetadata(HttpClassicServerRequest request) {
        int dataFormat = HttpServerUtils.getDataFormat(request);
        TagLengthValues tagLengthValues = request.headers()
                .first(FIT_TLV.value())
                .map(HttpUtils::decode)
                .map(TagLengthValues::deserialize)
                .orElseGet(TagLengthValues::create);
        return RequestMetadata.custom().dataFormat(dataFormat).tagValues(tagLengthValues).build();
    }
}
