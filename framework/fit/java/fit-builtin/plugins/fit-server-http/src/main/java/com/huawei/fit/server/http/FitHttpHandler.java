/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2024. All rights reserved.
 */

package com.huawei.fit.server.http;

import static com.huawei.fit.http.header.HttpHeaderKey.FIT_TLV;
import static com.huawei.fitframework.inspection.Validation.greaterThanOrEquals;
import static com.huawei.fitframework.inspection.Validation.notNull;

import com.huawei.fit.http.entity.Entity;
import com.huawei.fit.http.entity.support.DefaultObjectEntity;
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
import com.huawei.fitframework.broker.FitableMetadata;
import com.huawei.fitframework.broker.Genericable;
import com.huawei.fitframework.broker.LocalGenericableRepository;
import com.huawei.fitframework.broker.server.Dispatcher;
import com.huawei.fitframework.broker.server.Response;
import com.huawei.fitframework.ioc.BeanContainer;
import com.huawei.fitframework.serialization.RequestMetadata;
import com.huawei.fitframework.serialization.ResponseMetadataV2;
import com.huawei.fitframework.serialization.TagLengthValues;
import com.huawei.fitframework.serialization.Version;
import com.huawei.fitframework.util.StringUtils;

import java.io.ByteArrayInputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * 表示处理 FIT 通信方式的处理器。
 *
 * @author 季聿阶 j00559309
 * @since 2022-09-14
 */
public class FitHttpHandler extends AbstractHttpHandler {
    private final Dispatcher dispatcher;
    private final BeanContainer container;
    private final LocalGenericableRepository repository;

    FitHttpHandler(BeanContainer container, LocalGenericableRepository repository, StaticInfo staticInfo,
            ExecutionInfo executionInfo, Dispatcher dispatcher) {
        super(staticInfo, executionInfo);
        this.dispatcher = notNull(dispatcher, "The receiver cannot be null.");
        this.container = container;
        this.repository = repository;
    }

    @Override
    public void handle(HttpClassicServerRequest request, HttpClassicServerResponse response)
            throws DoHttpHandlerException {
        RequestMetadata metadata = this.getRequestMetadata(request);
        Object[] data = this.getRequestData(request.entityBytes(), metadata);
        if (Objects.equals(metadata.asyncTaskId(), StringUtils.EMPTY)) {
            this.doSyncHandle(metadata, data, response);
        } else {
            this.doAsyncHandle(metadata, data, response);
        }
    }

    private void doSyncHandle(RequestMetadata metadata, Object[] data, HttpClassicServerResponse response) {
        Response result = this.dispatcher.dispatch(metadata, data);
        FitHttpHandlerUtils.setResponseCode(response, HttpResponseStatus.OK);
        FitHttpHandlerUtils.setResponseHeaders(response, result);
        this.setResponseEntity(response, metadata, result);
    }

    private void doAsyncHandle(RequestMetadata metadata, Object[] data, HttpClassicServerResponse response) {
        int code = AsyncTaskExecutor.INSTANCE.submit(metadata.asyncTaskId(),
                () -> this.dispatcher.dispatch(metadata, data));
        // 不等待计算任务结束，只返回异步任务提交结果
        Response result = Response.create(ResponseMetadataV2.custom()
                .version(ResponseMetadataV2.CURRENT_VERSION)
                .dataFormat(metadata.dataFormatByte())
                .code(code)
                .build(), null, new byte[0]);
        FitHttpHandlerUtils.setResponseCode(response, HttpResponseStatus.ACCEPTED);
        FitHttpHandlerUtils.setResponseHeaders(response, result);
        this.setResponseEntity(response, metadata, result);
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
        List<String> pathList = StringUtils.split(request.path(), '/', ArrayList::new, StringUtils::isNotBlank);
        greaterThanOrEquals(pathList.size(), 3, "Illegal fit path. [path={0}]", request.path());
        String genericableId = pathList.get(pathList.size() - 2);
        String fitableId = pathList.get(pathList.size() - 1);
        TagLengthValues tagLengthValues = request.headers()
                .first(FIT_TLV.value())
                .map(FitHttpHandlerUtils::decode)
                .map(TagLengthValues::deserialize)
                .orElse(null);
        int dataFormat = FitHttpHandlerUtils.getDataFormat(request);
        String genericableVersion = FitHttpHandlerUtils.getGenericableVersion(request);
        String asyncTaskId = FitHttpHandlerUtils.getAsyncTaskId(request);
        return RequestMetadata.custom()
                .dataFormat((byte) dataFormat)
                .asyncTaskId(asyncTaskId)
                .genericableId(genericableId)
                .genericableVersion(Version.builder(genericableVersion).build())
                .fitableId(fitableId)
                .fitableVersion(Version.builder(FitableMetadata.DEFAULT_VERSION).build())
                .tagValues(tagLengthValues)
                .build();
    }

    private Object[] getRequestData(byte[] dataBytes, RequestMetadata metadata) {
        Genericable genericable = this.getGenericable(metadata);
        Method method = genericable.method().method();
        notNull(method, "The genericable method cannot be null. [genericableId={0}]", genericable.id());
        int format = metadata.dataFormat();
        MessageSerializer messageSerializer = MessageSerializerUtils.getMessageSerializer(this.container, format)
                .orElseThrow(() -> new IllegalStateException(StringUtils.format(
                        "MessageSerializer required but not found. [format={0}]",
                        format)));
        Type[] argumentTypes =
                Stream.of(method.getParameters()).map(Parameter::getParameterizedType).toArray(Type[]::new);
        return messageSerializer.deserializeRequest(argumentTypes, dataBytes);
    }

    private byte[] getResponseData(Type returnType, Object data, int format) {
        MessageSerializer messageSerializer = MessageSerializerUtils.getMessageSerializer(this.container, format)
                .orElseThrow(() -> new IllegalStateException(StringUtils.format(
                        "MessageSerializer required but not found. [format={0}]",
                        format)));
        return messageSerializer.serializeResponse(returnType, data);
    }

    private Genericable getGenericable(RequestMetadata metadata) {
        return this.repository.get(metadata.genericableId(), metadata.genericableVersion().toString())
                .orElseThrow(() -> new DoHttpHandlerException(StringUtils.format(
                        "No genericable. [genericableId={0}, genericableVersion={1}]",
                        metadata.genericableId(),
                        metadata.genericableVersion().toString())));
    }
}
