/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fit.http.client.okhttp;

import static modelengine.fitframework.inspection.Validation.notNull;
import static modelengine.fitframework.util.ObjectUtils.getIfNull;

import modelengine.fit.http.client.HttpClassicClientFactory;
import modelengine.fit.http.protocol.ClientRequest;
import modelengine.fit.http.protocol.ClientResponse;
import modelengine.fit.http.protocol.ConfigurableMessageHeaders;
import modelengine.fit.http.protocol.HttpRequestMethod;
import modelengine.fit.http.protocol.HttpVersion;
import modelengine.fit.http.protocol.MessageHeaderNames;
import modelengine.fit.http.protocol.RequestLine;
import modelengine.fit.http.protocol.WritableMessageBody;
import modelengine.fit.http.protocol.support.ClientRequestBody;
import modelengine.fitframework.model.MultiValueMap;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 表示 {@link ClientRequest} 的使用 OkHttp 实现。
 *
 * @author 杭潇
 * @since 2024-04-09
 */
public class OkHttpClientRequest implements ClientRequest {
    private final HttpRequestMethod method;
    private final URL url;
    private final ConfigurableMessageHeaders headers;
    private final WritableMessageBody body;
    private boolean isClosed;
    private final OkHttpClient.Builder clientBuilder;
    private final okhttp3.Request.Builder requestBuilder;
    private final HttpClassicClientFactory.Config config;
    private final ByteArrayOutputStream stream = new ByteArrayOutputStream();

    /**
     * 表示 {@link OkHttpClientRequest} 类的构造方法。
     *
     * @param method 表示指定的 Http 方法的 {@link HttpRequestMethod}。
     * @param url 表示 Http 请求地址的 {@link String}。
     * @param config 表示配置的 {@link HttpClassicClientFactory.Config}。
     */
    public OkHttpClientRequest(HttpRequestMethod method, String url, HttpClassicClientFactory.Config config) {
        this.config = getIfNull(config, () -> HttpClassicClientFactory.Config.builder().build());
        this.method = notNull(method, "The request method cannot be null.");
        try {
            this.url = new URL(url);
            this.requestBuilder = new Request.Builder().url(this.url);
        } catch (IOException e) {
            throw new IllegalStateException("The url is incorrect.", e);
        }
        this.headers = ConfigurableMessageHeaders.create();
        this.headers.set(MessageHeaderNames.HOST, this.url.getHost());
        this.body = new ClientRequestBody(this);

        this.clientBuilder = OkHttpClientBuilderFactory.getOkHttpClientBuilder(this.config);
    }

    @Override
    public void writeStartLineAndHeaders() throws IOException {
        this.checkIfClosed();
        this.requestBuilder.setMethod$okhttp(this.method.name());
        for (String headerName : this.headers.names()) {
            List<String> values = this.headers.all(headerName);
            for (String value : values) {
                this.requestBuilder.addHeader(headerName, value);
            }
        }
    }

    private void checkIfClosed() throws IOException {
        if (this.isClosed) {
            throw new IOException("The okhttp client request has already been closed.");
        }
    }

    @Override
    public void writeBody(int b) throws IOException {
        this.checkIfClosed();
        this.stream.write(b);
        this.updateRequestBody();
    }

    @Override
    public void writeBody(byte[] bytes, int off, int len) throws IOException {
        this.checkIfClosed();
        this.stream.write(bytes, off, len);
        this.updateRequestBody();
    }

    private void updateRequestBody() {
        this.requestBuilder.method(this.method.name(), RequestBody.create(this.stream.toByteArray(), null));
    }

    @Override
    public void flush() throws IOException {}

    @Override
    public ClientResponse readResponse() throws IOException {
        this.checkIfClosed();
        if (this.config.connectTimeout() >= 0) {
            this.clientBuilder.connectTimeout(this.config.connectTimeout(), TimeUnit.MILLISECONDS);
        }
        if (this.config.socketTimeout() >= 0) {
            this.clientBuilder.readTimeout(this.config.socketTimeout(), TimeUnit.MILLISECONDS)
                    .writeTimeout(this.config.socketTimeout(), TimeUnit.MILLISECONDS);
        }
        OkHttpClient client = this.clientBuilder.build();
        Response response = client.newCall(this.requestBuilder.build()).execute();
        try {
            if (response.body() != null) {
                return ClientResponse.create(response.code(),
                        response.message(),
                        MultiValueMap.create(response.headers().toMultimap()),
                        response.body().byteStream());
            }
            throw new IllegalStateException("The response body is null.");
        } finally {
            client.dispatcher().executorService().shutdown();
        }
    }

    @Override
    public RequestLine startLine() {
        return RequestLine.create(HttpVersion.HTTP_1_1, this.method, this.url.getPath());
    }

    @Override
    public ConfigurableMessageHeaders headers() {
        return this.headers;
    }

    @Override
    public WritableMessageBody body() {
        return this.body;
    }

    @Override
    public void close() throws IOException {
        this.isClosed = true;
        this.body.close();
    }
}