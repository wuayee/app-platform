/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2024. All rights reserved.
 */

package com.huawei.fit.client.http;

import static com.huawei.fit.http.header.HttpHeaderKey.FIT_ASYNC_TASK_ID;
import static com.huawei.fit.http.header.HttpHeaderKey.FIT_CODE;
import static com.huawei.fit.http.header.HttpHeaderKey.FIT_DATA_FORMAT;
import static com.huawei.fit.http.header.HttpHeaderKey.FIT_GENERICABLE_VERSION;
import static com.huawei.fit.http.header.HttpHeaderKey.FIT_MESSAGE;
import static com.huawei.fit.http.header.HttpHeaderKey.FIT_METADATA;
import static com.huawei.fit.http.header.HttpHeaderKey.FIT_TLV;
import static com.huawei.fit.http.header.HttpHeaderKey.FIT_VERSION;
import static com.huawei.fitframework.inspection.Validation.notNull;

import com.huawei.fit.client.Client;
import com.huawei.fit.client.Request;
import com.huawei.fit.client.Response;
import com.huawei.fit.client.http.support.HttpConnectionBuilder;
import com.huawei.fit.client.http.support.HttpsConnectionBuilder;
import com.huawei.fit.http.client.HttpClassicClient;
import com.huawei.fit.http.client.HttpClassicClientFactory;
import com.huawei.fit.http.client.HttpClassicClientRequest;
import com.huawei.fit.http.client.HttpClassicClientResponse;
import com.huawei.fit.http.entity.Entity;
import com.huawei.fit.http.entity.support.DefaultReadableBinaryEntity;
import com.huawei.fit.http.exception.AsyncTaskNotCompletedException;
import com.huawei.fit.http.protocol.HttpRequestMethod;
import com.huawei.fit.http.protocol.HttpResponseStatus;
import com.huawei.fit.http.protocol.MessageHeaderNames;
import com.huawei.fit.http.protocol.MimeType;
import com.huawei.fit.security.Decryptor;
import com.huawei.fit.serialization.MessageSerializer;
import com.huawei.fit.serialization.util.MessageSerializerUtils;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.broker.CommunicationType;
import com.huawei.fitframework.conf.runtime.ClientConfig;
import com.huawei.fitframework.exception.ClientException;
import com.huawei.fitframework.inspection.Nonnull;
import com.huawei.fitframework.ioc.BeanContainer;
import com.huawei.fitframework.ioc.BeanFactory;
import com.huawei.fitframework.log.Logger;
import com.huawei.fitframework.serialization.ResponseMetadataV2;
import com.huawei.fitframework.serialization.TagLengthValues;
import com.huawei.fitframework.util.StringUtils;
import com.huawei.fitframework.util.UuidUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * 为Broker提供基于HTTP调用的客户端。
 *
 * @author 季聿阶 j00559309
 * @author 张越 z00559346
 * @author 詹高扬 z50029227
 * @since 2020-10-05
 */
@Component
public class HttpClient implements Client {
    private static final Logger log = Logger.get(HttpClient.class);
    private static final Set<String> PROTOCOLS = new HashSet<>();
    private static final int FIT_DATA_FORMAT_JSON = 1;
    private static final String APPLICATION_JSON = "application/json;charset=UTF-8";

    private static final int FIT_ASYNC_TIMEOUT_MILLISECONDS = 300_000;

    static {
        PROTOCOLS.add(HttpConnectionBuilder.HTTP);
        PROTOCOLS.add(HttpsConnectionBuilder.HTTPS);
    }

    private final HttpClassicClientFactory factory;
    private final BeanContainer container;
    private final Map<String, HttpConnectionBuilder> builders = new HashMap<>();
    private final ClientConfig clientConfig;

    /**
     * 创建 Http 客户端。
     *
     * @param container 表示 Bean 容器的 {@link BeanContainer}。
     * @param clientConfig 表示 Http 客户端配置的 {@link ClientConfig}。
     */
    public HttpClient(BeanContainer container, ClientConfig clientConfig) {
        this.container = notNull(container, "The bean container cannot be null.");
        this.factory = container.all(HttpClassicClientFactory.class)
                .stream()
                .map(BeanFactory::<HttpClassicClientFactory>get)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("The http classic client factory cannot be null."));
        this.builders.put(HttpConnectionBuilder.HTTP, new HttpConnectionBuilder());
        this.builders.put(HttpsConnectionBuilder.HTTPS, new HttpsConnectionBuilder());
        this.clientConfig = notNull(clientConfig, "The http config cannot be null.");
    }

    @Override
    public Response requestResponse(@Nonnull Request request) {
        if (isAsync(request)) {
            return this.doAsyncRequest(request);
        }
        return this.doSyncRequest(request);
    }

    private Response doSyncRequest(Request request) {
        HttpClassicClient client = this.buildHttpClient(request);
        try (HttpClassicClientRequest clientRequest = this.buildClientDataRequest(client, request)) {
            clientRequest.entity(this.buildRequestEntity(clientRequest, request));
            try (HttpClassicClientResponse<Object> clientResponse = client.exchange(clientRequest,
                    request.returnType())) {
                return this.getDataResponse(request, clientResponse);
            }
        } catch (IOException e) {
            throw new ClientException("Failed to close http classic client.", e);
        }
    }

    private Response doAsyncRequest(Request request) {
        HttpClassicClient client = this.buildHttpClient(request);
        String taskId = UuidUtils.randomUuidString();
        // 第一步：提交异步任务请求
        try (HttpClassicClientRequest clientRequest = this.buildAsyncClientRequest(client, request, taskId)) {
            clientRequest.entity(this.buildRequestEntity(clientRequest, request));
            try (HttpClassicClientResponse<Object> clientResponse = client.exchange(clientRequest,
                    request.returnType())) {
                ResponseMetadataV2 responseMetadataV2 = getResponseMetadata(request, clientResponse);
                // 服务器不支持异步长任务，应按照同步请求处理
                if (clientResponse.statusCode() != HttpResponseStatus.ACCEPTED.statusCode()) {
                    log.info("Async task not supported. Use Sync. [taskId={}]", taskId);
                    return this.getDataResponse(request, clientResponse);
                } else if (responseMetadataV2.code() != ResponseMetadataV2.CODE_OK) {
                    // 如果返回值不为OK，则将结果返回给上层
                    return Response.create(responseMetadataV2, null);
                }
            }
        } catch (IOException e) {
            throw new ClientException("Failed to close http classic client.", e);
        }
        // 第二步：长轮询
        while (true) {
            try (HttpClassicClientRequest clientRequest = this.buildClientLongPollRequest(client, request, taskId)) {
                try (HttpClassicClientResponse<Object> clientResponse = client.exchange(clientRequest,
                        request.returnType())) {
                    ResponseMetadataV2 responseMetadataV2 = getResponseMetadata(request, clientResponse);
                    // 如果返回值为任务未完成，继续长轮询；如果返回值为OK或者其他情况，则将结果返回给上层
                    if (responseMetadataV2.code() != AsyncTaskNotCompletedException.CODE) {
                        Object result = this.getResponseData(request, responseMetadataV2, clientResponse);
                        return Response.create(responseMetadataV2, result);
                    }
                }
            } catch (IOException e) {
                throw new ClientException("Failed to close http classic client.", e);
            }
        }
    }

    private HttpClassicClient buildHttpClient(Request request) {
        int timeout = Math.max(FIT_ASYNC_TIMEOUT_MILLISECONDS,
                Math.toIntExact(request.context().timeoutUnit().toMillis(request.context().timeout())));
        Optional<ClientConfig.Secure> secureInfo = this.clientConfig.secure();
        Map<String, Object> map = new HashMap<>();
        if (secureInfo.isPresent()) {
            ClientConfig.Secure secure = secureInfo.get();
            boolean isEncrypted = secure.encrypted();
            String trustStorePassword = secure.trustStorePassword().orElse(StringUtils.EMPTY);
            String keyStorePassword = secure.keyStorePassword().orElse(StringUtils.EMPTY);
            if (isEncrypted) {
                Decryptor decryptor =
                        notNull(this.container.beans().lookup(Decryptor.class), "The Decryptor cannot be null.");
                if (StringUtils.isNotBlank(trustStorePassword)) {
                    trustStorePassword = decryptor.decrypt(trustStorePassword);
                }
                if (StringUtils.isNotBlank(keyStorePassword)) {
                    keyStorePassword = decryptor.decrypt(keyStorePassword);
                }
            }
            secure.keyStoreFile().ifPresent(keyStore -> map.put(HttpsConstants.CLIENT_SECURE_KEY_STORE_FILE, keyStore));
            secure.trustStoreFile()
                    .ifPresent(trustStore -> map.put(HttpsConstants.CLIENT_SECURE_TRUST_STORE_FILE, trustStore));
            map.put(HttpsConstants.CLIENT_SECURE_IGNORE_TRUST, String.valueOf(secure.ignoreTrust()));
            map.put(HttpsConstants.CLIENT_SECURE_IGNORE_HOSTNAME, String.valueOf(secure.ignoreHostName()));
            map.put(HttpsConstants.CLIENT_SECURE_KEY_STORE_PASSWORD, keyStorePassword);
            map.put(HttpsConstants.CLIENT_SECURE_TRUST_STORE_PASSWORD, trustStorePassword);
        }
        return this.factory.create(HttpClassicClientFactory.Config.builder()
                .connectTimeout(timeout)
                .connectionRequestTimeout(timeout)
                .socketTimeout(timeout)
                .custom(map)
                .build());
    }

    private HttpClassicClientRequest buildClientDataRequest(HttpClassicClient client, Request request) {
        HttpConnectionBuilder builder = this.builders.get(request.protocol());
        notNull(builder, "The protocol is incorrect. [protocol={0}, supported={1}]", request.protocol(), PROTOCOLS);
        String url = builder.buildUrl(request);
        HttpClassicClientRequest clientRequest = client.createRequest(HttpRequestMethod.POST, url);
        fillBaseHeaders(clientRequest, request);
        return clientRequest;
    }

    private HttpClassicClientRequest buildAsyncClientRequest(HttpClassicClient client, Request request, String taskId) {
        HttpClassicClientRequest clientRequest = this.buildClientDataRequest(client, request);
        clientRequest.headers().add(FIT_ASYNC_TASK_ID.value(), taskId);
        return clientRequest;
    }

    private HttpClassicClientRequest buildClientLongPollRequest(HttpClassicClient client, Request request,
            String taskId) {
        HttpConnectionBuilder builder = this.builders.get(request.protocol());
        String url = builder.buildLongPollUrl(request, taskId);
        HttpClassicClientRequest clientRequest = client.createRequest(HttpRequestMethod.GET, url);
        fillBaseHeaders(clientRequest, request);
        return clientRequest;
    }

    private Response getDataResponse(Request request, HttpClassicClientResponse<Object> clientResponse) {
        ResponseMetadataV2 responseMetadataV2 = getResponseMetadata(request, clientResponse);
        if (responseMetadataV2.code() == ResponseMetadataV2.CODE_OK) {
            Object result = this.getResponseData(request, responseMetadataV2, clientResponse);
            return Response.create(responseMetadataV2, result);
        }
        return Response.create(responseMetadataV2, null);
    }

    private static boolean isAsync(Request request) {
        return request.context().communicationType() == CommunicationType.ASYNC;
    }

    private static void fillBaseHeaders(HttpClassicClientRequest clientRequest, Request request) {
        clientRequest.headers()
                .add(FIT_VERSION.value(), String.valueOf(request.metadata().version()))
                .add(FIT_DATA_FORMAT.value(), String.valueOf(request.metadata().dataFormatByte()))
                .add(FIT_GENERICABLE_VERSION.value(), request.metadata().genericableVersion().toString())
                .add(FIT_TLV.value(), encode(request.metadata().tagValues().serialize()))
                .add(MessageHeaderNames.ACCEPT, MimeType.APPLICATION_OCTET_STREAM.value());
    }

    private Entity buildRequestEntity(HttpClassicClientRequest clientRequest, Request request) {
        int format = request.metadata().dataFormat();
        if (format == FIT_DATA_FORMAT_JSON) {
            clientRequest.headers().add(MessageHeaderNames.CONTENT_TYPE, APPLICATION_JSON);
        } else {
            clientRequest.headers().add(MessageHeaderNames.CONTENT_TYPE, MimeType.APPLICATION_OCTET_STREAM.value());
        }
        MessageSerializer messageSerializer = MessageSerializerUtils.getMessageSerializer(this.container, format)
                .orElseThrow(() -> new IllegalStateException(StringUtils.format(
                        "MessageSerializer required but not found. [format={0}]",
                        format)));
        byte[] bytes = messageSerializer.serializeRequest(request.dataTypes(), request.data());
        clientRequest.headers().add(MessageHeaderNames.CONTENT_LENGTH, Integer.toString(bytes.length));
        return new DefaultReadableBinaryEntity(clientRequest, new ByteArrayInputStream(bytes));
    }

    private static ResponseMetadataV2 getResponseMetadata(Request request,
            HttpClassicClientResponse<Object> clientResponse) {
        return clientResponse.headers()
                .first(FIT_METADATA.value())
                .map(HttpClient::decode)
                .map(ResponseMetadataV2::deserialize)
                .orElseGet(() -> getResponseMetadataFromMultiHeaders(request, clientResponse));
    }

    private static ResponseMetadataV2 getResponseMetadataFromMultiHeaders(Request request,
            HttpClassicClientResponse<Object> clientResponse) {
        short version = getResponseVersion(request, clientResponse);
        byte dataFormat = getResponseDataFormat(request, clientResponse);
        int code = getResponseCode(request, clientResponse);
        String message = getResponseMessage(clientResponse);
        TagLengthValues tagValues = getResponseTagLengthValue(clientResponse);
        return ResponseMetadataV2.custom()
                .version(version)
                .dataFormat(dataFormat)
                .code(code)
                .message(message)
                .tagValues(tagValues)
                .build();
    }

    private static short getResponseVersion(Request request, HttpClassicClientResponse<Object> clientResponse) {
        String version = clientResponse.headers()
                .first(FIT_VERSION.value())
                .orElseThrow(() -> new IllegalStateException(StringUtils.format(
                        "No response version. [protocol={0}, address={1}, header={2}]",
                        request.protocol(),
                        request.address(),
                        FIT_VERSION.value())));
        try {
            return Short.parseShort(version);
        } catch (NumberFormatException e) {
            throw new IllegalStateException(StringUtils.format(
                    "Incorrect response version. [protocol={0}, address={1}, version={2}]",
                    request.protocol(),
                    request.address(),
                    version));
        }
    }

    private static byte getResponseDataFormat(Request request, HttpClassicClientResponse<Object> clientResponse) {
        String dataFormat = clientResponse.headers()
                .first(FIT_DATA_FORMAT.value())
                .orElseThrow(() -> new IllegalStateException(StringUtils.format(
                        "No response data format. [protocol={0}, address={1}, header={2}]",
                        request.protocol(),
                        request.address(),
                        FIT_DATA_FORMAT.value())));
        try {
            return Byte.parseByte(dataFormat);
        } catch (NumberFormatException e) {
            throw new IllegalStateException(StringUtils.format(
                    "Incorrect response data format. [protocol={0}, address={1}, dataFormat={2}]",
                    request.protocol(),
                    request.address(),
                    dataFormat));
        }
    }

    private static int getResponseCode(Request request, HttpClassicClientResponse<Object> clientResponse) {
        String code = clientResponse.headers()
                .first(FIT_CODE.value())
                .orElseThrow(() -> new IllegalStateException(StringUtils.format(
                        "No response code. [protocol={0}, address={1}, header={2}]",
                        request.protocol(),
                        request.address(),
                        FIT_CODE.value())));
        try {
            return Integer.parseInt(code);
        } catch (NumberFormatException e) {
            throw new IllegalStateException(StringUtils.format(
                    "Incorrect response code. [protocol={0}, address={1}, code={2}]",
                    request.protocol(),
                    request.address(),
                    code));
        }
    }

    private static String getResponseMessage(HttpClassicClientResponse<Object> clientResponse) {
        return clientResponse.headers().first(FIT_MESSAGE.value()).orElse(StringUtils.EMPTY);
    }

    private static TagLengthValues getResponseTagLengthValue(HttpClassicClientResponse<Object> clientResponse) {
        return clientResponse.headers()
                .first(FIT_TLV.value())
                .map(HttpClient::decode)
                .map(TagLengthValues::deserialize)
                .orElseGet(TagLengthValues::create);
    }

    private Object getResponseData(Request request, ResponseMetadataV2 responseMetadataV2,
            HttpClassicClientResponse<Object> clientResponse) {
        int format = responseMetadataV2.dataFormat();
        MessageSerializer messageSerializer = MessageSerializerUtils.getMessageSerializer(this.container, format)
                .orElseThrow(() -> new IllegalStateException(StringUtils.format(
                        "MessageSerializer required but not found. [format={0}]",
                        format)));
        return messageSerializer.deserializeResponse(request.returnType(), clientResponse.entityBytes());
    }

    @Override
    public Set<String> getSupportedProtocols() {
        return PROTOCOLS;
    }

    private static String encode(byte[] data) {
        return Base64.getEncoder().encodeToString(data);
    }

    private static byte[] decode(String data) {
        return Base64.getDecoder().decode(data);
    }
}
