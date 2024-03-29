/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2023. All rights reserved.
 */

package com.huawei.fit.server.http;

import static com.huawei.fit.http.header.HttpHeaderKey.FIT_TLV;

import com.huawei.fit.http.entity.Entity;
import com.huawei.fit.http.entity.support.DefaultObjectEntity;
import com.huawei.fit.http.exception.AsyncTaskExecutionException;
import com.huawei.fit.http.exception.AsyncTaskNotCompletedException;
import com.huawei.fit.http.exception.AsyncTaskNotFoundException;
import com.huawei.fit.http.protocol.HttpResponseStatus;
import com.huawei.fit.http.protocol.MessageHeaderNames;
import com.huawei.fit.http.server.DoHttpHandlerException;
import com.huawei.fit.http.server.HttpClassicServerRequest;
import com.huawei.fit.http.server.HttpClassicServerResponse;
import com.huawei.fit.http.server.handler.AbstractHttpHandler;
import com.huawei.fit.serialization.MessageSerializer;
import com.huawei.fit.serialization.util.MessageSerializerUtils;
import com.huawei.fit.server.http.support.AsyncTaskExecutor;
import com.huawei.fit.server.http.util.FitHttpHandlerUtils;
import com.huawei.fitframework.broker.server.Response;
import com.huawei.fitframework.ioc.BeanContainer;
import com.huawei.fitframework.serialization.RequestMetadata;
import com.huawei.fitframework.serialization.ResponseMetadataV2;
import com.huawei.fitframework.serialization.TagLengthValues;
import com.huawei.fitframework.util.StringUtils;

import java.io.ByteArrayInputStream;
import java.lang.reflect.Type;
import java.util.Optional;

/**
 * 表示处理 FIT 通信方式的处理器。
 *
 * @author 王成 w00863339
 * @since 2023-11-16
 */
public class FitHttpAsyncTaskHandler extends AbstractHttpHandler {
    private static final String TASK_ID_KEY = "tid";

    private final BeanContainer container;

    FitHttpAsyncTaskHandler(BeanContainer container, StaticInfo staticInfo, ExecutionInfo executionInfo) {
        super(staticInfo, executionInfo);
        this.container = container;
    }

    @Override
    public void handle(HttpClassicServerRequest request, HttpClassicServerResponse response)
            throws DoHttpHandlerException {
        RequestMetadata metadata = this.getRequestMetadata(request);
        Optional<Response> resultOp = Optional.empty();
        int code = AsyncTaskNotCompletedException.CODE;
        String message = StringUtils.EMPTY;
        try {
            resultOp = AsyncTaskExecutor.INSTANCE.longPoll(metadata.asyncTaskId());
        } catch (AsyncTaskNotFoundException e) {
            code = AsyncTaskNotFoundException.CODE;
            message = e.getMessage();
        } catch (AsyncTaskExecutionException e) {
            code = AsyncTaskExecutionException.CODE;
            message = e.getMessage();
        }

        FitHttpHandlerUtils.setResponseCode(response, HttpResponseStatus.OK);

        if (resultOp.isPresent()) {
            Response result = resultOp.get();
            FitHttpHandlerUtils.setResponseHeaders(response, result);
            this.setResponseEntity(response, metadata, result);
        } else {
            // 任务执行失败、未完成、未找到等异常场景，只设置消息头，不设置消息体
            Response result = Response.create(ResponseMetadataV2.custom()
                    .version(ResponseMetadataV2.CURRENT_VERSION)
                    .dataFormat(metadata.dataFormatByte())
                    .code(code)
                    .message(message)
                    .build(), null, new byte[0]);
            FitHttpHandlerUtils.setResponseHeaders(response, result);
        }
    }

    private void setResponseEntity(HttpClassicServerResponse response, RequestMetadata metadata, Response result) {
        if (metadata.dataFormat() == FitHttpHandlerUtils.FIT_DATA_FORMAT_JSON) {
            response.entity(new DefaultObjectEntity<>(response, result.data()));
        } else {
            byte[] responseData = this.getResponseData(result.type(), result.data(), metadata.dataFormat());
            response.headers().set(MessageHeaderNames.CONTENT_LENGTH, Integer.toString(responseData.length));
            response.entity(Entity.createBinaryEntity(response, new ByteArrayInputStream(responseData)));
        }
    }

    private RequestMetadata getRequestMetadata(HttpClassicServerRequest request) {
        int dataFormat = FitHttpHandlerUtils.getDataFormat(request);
        String taskId = request.queries().first(TASK_ID_KEY).orElse(StringUtils.EMPTY);
        TagLengthValues tagLengthValues = request.headers()
                .first(FIT_TLV.value())
                .map(FitHttpHandlerUtils::decode)
                .map(TagLengthValues::deserialize)
                .orElse(null);
        return RequestMetadata.custom()
                .dataFormat((byte) dataFormat)
                .asyncTaskId(taskId)
                .tagValues(tagLengthValues)
                .build();
    }

    private byte[] getResponseData(Type returnType, Object data, int format) {
        MessageSerializer messageSerializer = MessageSerializerUtils.getMessageSerializer(this.container, format)
                .orElseThrow(() -> new IllegalStateException(StringUtils.format(
                        "MessageSerializer required but not found. [format={0}]",
                        format)));
        return messageSerializer.serializeResponse(returnType, data);
    }
}
