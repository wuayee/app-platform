/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2024. All rights reserved.
 */

package com.huawei.fit.http.client.jdk;

import static com.huawei.fit.http.protocol.util.SslUtils.getKeyManagers;
import static com.huawei.fit.http.protocol.util.SslUtils.getTrustManagers;
import static com.huawei.fitframework.inspection.Validation.notNull;
import static com.huawei.fitframework.util.ObjectUtils.cast;
import static com.huawei.fitframework.util.ObjectUtils.getIfNull;

import com.huawei.fit.client.http.HttpsConstants;
import com.huawei.fit.http.client.HttpClassicClientFactory;
import com.huawei.fit.http.protocol.ClientRequest;
import com.huawei.fit.http.protocol.ClientResponse;
import com.huawei.fit.http.protocol.ConfigurableMessageHeaders;
import com.huawei.fit.http.protocol.HttpRequestMethod;
import com.huawei.fit.http.protocol.HttpVersion;
import com.huawei.fit.http.protocol.MessageHeaderNames;
import com.huawei.fit.http.protocol.MessageHeaderValues;
import com.huawei.fit.http.protocol.RequestLine;
import com.huawei.fit.http.protocol.WritableMessageBody;
import com.huawei.fit.http.protocol.support.ClientRequestBody;
import com.huawei.fitframework.model.MultiValueMap;
import com.huawei.fitframework.util.StringUtils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.cert.X509Certificate;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * 表示 {@link ClientRequest} 的使用 {@link HttpURLConnection} 的实现。
 *
 * @author 季聿阶 j00559309
 * @since 2022-11-25
 */
public class UrlConnectionClientRequest implements ClientRequest {
    private final HttpRequestMethod method;
    private final URL url;
    private final HttpURLConnection connection;
    private final ConfigurableMessageHeaders headers;
    private final WritableMessageBody body;
    private boolean isSetOutput = false;
    private boolean isClosed;
    private final HttpClassicClientFactory.Config config;

    /**
     * 创建客户端连接请求。
     *
     * @param method 表示 Http 连接请求的方法的 {@link HttpRequestMethod}。
     * @param url 表示连接地址的 {@link String}。
     * @param config 表示配置的 {@link com.huawei.fit.http.client.HttpClassicClientFactory.Config}。
     */
    public UrlConnectionClientRequest(HttpRequestMethod method, String url, HttpClassicClientFactory.Config config) {
        this.config = getIfNull(config, () -> HttpClassicClientFactory.Config.builder().build());
        this.method = notNull(method, "The request method cannot be null.");
        try {
            this.url = new URL(url);
            this.connection = cast(this.url.openConnection());
            if (config.connectTimeout() >= 0) {
                this.connection.setConnectTimeout(config.connectTimeout());
            }
            if (config.socketTimeout() >= 0) {
                this.connection.setReadTimeout(config.socketTimeout());
            }
            this.connection.setUseCaches(false);
            this.connection.setInstanceFollowRedirects(false);
            this.connection.setDoInput(true);
            if (this.connection instanceof HttpsURLConnection) {
                try {
                    this.setSslConfig();
                } catch (GeneralSecurityException e) {
                    throw new IllegalStateException("Failed to set https config.", e);
                }
            }
        } catch (IOException e) {
            throw new IllegalStateException("The url is incorrect.", e);
        }
        this.headers = ConfigurableMessageHeaders.create();
        this.headers.set(MessageHeaderNames.HOST, this.url.getHost());
        this.body = new ClientRequestBody(this);
    }

    private void setSslConfig() throws GeneralSecurityException, IOException {
        String trustStoreFile = cast(this.config.custom().get(HttpsConstants.CLIENT_SECURE_TRUST_STORE_FILE));
        String keyStoreFile = cast(this.config.custom().get(HttpsConstants.CLIENT_SECURE_KEY_STORE_FILE));
        String trustStorePassword = cast(this.config.custom().get(HttpsConstants.CLIENT_SECURE_TRUST_STORE_PASSWORD));
        String keyStorePassword = cast(this.config.custom().get(HttpsConstants.CLIENT_SECURE_KEY_STORE_PASSWORD));
        boolean ignoreHostname = Boolean.parseBoolean(
                String.valueOf(this.config.custom().getOrDefault(HttpsConstants.CLIENT_SECURE_IGNORE_HOSTNAME, false)));
        boolean ignoreTrust = Boolean.parseBoolean(
                String.valueOf(this.config.custom().getOrDefault(HttpsConstants.CLIENT_SECURE_IGNORE_TRUST, false)));

        KeyManager[] keyManagers;
        if (StringUtils.isNotBlank(keyStoreFile) && StringUtils.isNotBlank(keyStorePassword)) {
            keyManagers = getKeyManagers(keyStoreFile, keyStorePassword);
        } else {
            keyManagers = null;
        }

        TrustManager[] trustManagers;
        if (ignoreTrust) {
            trustManagers = this.getIgnoreTrustManagers();
        } else {
            if (StringUtils.isNotBlank(trustStoreFile) && StringUtils.isNotBlank(trustStorePassword)) {
                trustManagers = getTrustManagers(trustStoreFile, trustStorePassword);
            } else {
                trustManagers = null;
            }
        }
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(keyManagers, trustManagers, null);
        HttpsURLConnection secureConnection = cast(this.connection);
        secureConnection.setSSLSocketFactory(sslContext.getSocketFactory());
        if (ignoreHostname) {
            secureConnection.setHostnameVerifier((hostname, session) -> true);
        }
    }

    private TrustManager[] getIgnoreTrustManagers() {
        return new TrustManager[] {
                new X509TrustManager() {
                    @Override
                    public X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }

                    @Override
                    public void checkClientTrusted(X509Certificate[] certs, String authType) {}

                    @Override
                    public void checkServerTrusted(X509Certificate[] certs, String authType) {}
                }
        };
    }

    @Override
    public void writeStartLineAndHeaders() throws IOException {
        this.checkIfClosed();
        this.writeStartLine();
        this.writeHeaders();
    }

    @Override
    public void writeBody(int b) throws IOException {
        this.checkIfClosed();
        this.setDoOutput();
        this.connection.getOutputStream().write(b);
    }

    @Override
    public void writeBody(byte[] bytes, int off, int len) throws IOException {
        this.checkIfClosed();
        this.setDoOutput();
        this.connection.getOutputStream().write(bytes, off, len);
    }

    private void setDoOutput() throws IOException {
        if (!this.isSetOutput) {
            this.connection.setDoOutput(true);
            this.connection.connect();
            this.isSetOutput = true;
        }
    }

    @Override
    public ClientResponse readResponse() throws IOException {
        this.checkIfClosed();
        return ClientResponse.create(this.connection.getResponseCode(),
                this.connection.getResponseMessage(),
                MultiValueMap.create(this.connection::getHeaderFields),
                this.connection.getInputStream());
    }

    private void checkIfClosed() throws IOException {
        if (this.isClosed) {
            throw new IOException("The url connection client request has already been closed.");
        }
    }

    private void writeStartLine() throws IOException {
        if (this.method == HttpRequestMethod.PATCH || this.method == HttpRequestMethod.PUT) {
            this.connection.setRequestMethod(HttpRequestMethod.POST.name());
            this.connection.addRequestProperty(MessageHeaderNames.X_HTTP_METHOD_OVERRIDE, this.method.name());
        } else {
            this.connection.setRequestMethod(this.method.name());
        }
    }

    private void writeHeaders() {
        for (String headerName : this.headers.names()) {
            List<String> values = this.headers.all(headerName);
            for (String value : values) {
                this.connection.addRequestProperty(headerName, value);
            }
        }
        if (this.headers.contains(MessageHeaderNames.CONTENT_LENGTH)) {
            long len = this.headers.first(MessageHeaderNames.CONTENT_LENGTH)
                    .map(Long::parseLong)
                    .orElseThrow(() -> new IllegalStateException("No 'Content-Length' header value."));
            this.connection.setFixedLengthStreamingMode(len);
            return;
        }
        if (this.headers.contains(MessageHeaderNames.TRANSFER_ENCODING)) {
            boolean isChunked = this.headers.first(MessageHeaderNames.TRANSFER_ENCODING)
                    .filter(value -> StringUtils.equalsIgnoreCase(value, MessageHeaderValues.CHUNKED))
                    .isPresent();
            if (isChunked) {
                this.connection.setChunkedStreamingMode(0);
            }
        }
    }

    @Override
    public void flush() throws IOException {}

    @Override
    public void close() throws IOException {
        this.isClosed = true;
        this.body.close();
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
}
