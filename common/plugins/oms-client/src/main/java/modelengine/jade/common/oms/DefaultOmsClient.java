/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.jade.common.oms;

import static modelengine.fitframework.inspection.Validation.notBlank;
import static modelengine.fitframework.inspection.Validation.notEmpty;
import static modelengine.fitframework.inspection.Validation.notNull;
import static modelengine.fitframework.util.ObjectUtils.cast;


import com.huawei.framework.crypt.grpc.client.CryptClient;
import com.huawei.framework.crypt.grpc.client.exception.CryptoInvokeException;

import modelengine.fit.http.client.HttpClassicClient;
import modelengine.fit.http.client.HttpClassicClientFactory;
import modelengine.fit.http.client.HttpClassicClientRequest;
import modelengine.fit.http.client.HttpClassicClientResponse;
import modelengine.fit.http.entity.TextEntity;
import modelengine.fit.http.protocol.HttpRequestMethod;
import modelengine.fit.http.protocol.HttpResponseStatus;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.inspection.Nonnull;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.serialization.ObjectSerializer;
import modelengine.fitframework.util.LockUtils;
import modelengine.fitframework.util.MapBuilder;
import modelengine.fitframework.util.StringUtils;
import modelengine.fitframework.util.TypeUtils;
import modelengine.jade.common.oms.entity.NetPoint;
import modelengine.jade.common.oms.entity.ServiceInfo;
import modelengine.jade.common.oms.nacos.NacosClient;

import com.alibaba.nacos.api.naming.pojo.Instance;

import modelengine.jade.crypt.client.CryptClientServer;
import modelengine.jade.oms.OmsClient;
import modelengine.jade.oms.entity.FileEntity;
import modelengine.jade.oms.entity.NamedEntity;
import modelengine.jade.oms.entity.PartitionedEntity;
import modelengine.jade.oms.response.ResultVo;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSink;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * 表示 {@link OmsClient} 的默认实现。
 *
 * @author 李金绪
 * @author 何天放
 * @since 2024-11-27
 */
@Component
public class DefaultOmsClient implements OmsClient {
    private static final Logger log = Logger.get(DefaultOmsClient.class);
    private static final List<String> SERVICE_NAMES = Arrays.asList("Framework", "Monitor");
    private static final String HEADER_TOKEN = "X-Auth-Token-Inner";
    private static final String CLIENT_HTTP_IGNORE_TRUST = "client.http.secure.ignore-trust";
    private static final String DEFAULT_MIMETYPE = "application/octet-stream";
    private static final String SECURE = "secure";
    private static final String HTTP = "http";
    private static final String HTTPS = "https";
    private static final String TRUE = "true";
    private static final int DEFAULT_BUFFER_SIZE = 8192;
    private static final long EXPIRED_MILLISECOND = 30000L;
    private static final String GET_MACHINE_TOKEN_SERVICE_NAME = "machine_account_token_key";
    private static final com.huawei.framework.crypt.grpc.client.model.ServiceInfo SERVICE_INFO =
            new com.huawei.framework.crypt.grpc.client.model.ServiceInfo(GET_MACHINE_TOKEN_SERVICE_NAME);

    private final ObjectSerializer serializer;
    private final NacosClient nacosClient;
    private final HttpClassicClient httpClassicClient;
    private final OkHttpClient okHttpClient;
    private final Lock lock;
    private final Map<String, ServiceInfo> infos;

    /**
     * 构造方法。
     *
     * @param nacosClient 表示 Nacos 的 {@link NacosClient}；
     * @param httpClassicClientFactory 表示工厂的 {@link HttpClassicClientFactory}。
     */
    public DefaultOmsClient(@Fit(alias = "json") ObjectSerializer serializer, NacosClient nacosClient,
                            HttpClassicClientFactory httpClassicClientFactory) throws NoSuchAlgorithmException, KeyManagementException {
        this.serializer = notNull(serializer, "The serializer cannot be null.");
        this.nacosClient = notNull(nacosClient, "The nacos client cannot be null.");
        this.httpClassicClient = this.buildHttpClassicClient(httpClassicClientFactory);
        this.lock = LockUtils.newReentrantLock();
        this.infos = new HashMap<>();
        this.okHttpClient = this.getOkHttpClient();
    }

    private OkHttpClient getOkHttpClient() throws NoSuchAlgorithmException, KeyManagementException {
        final OkHttpClient okHttpClient;
        // 创建一个信任所有证书的TrustManager
        TrustManager[] trustAllCerts = new TrustManager[] {
                new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(X509Certificate[] chain, String authType)
                            throws CertificateException {
                    }

                    @Override
                    public void checkServerTrusted(X509Certificate[] chain, String authType)
                            throws CertificateException {
                    }

                    @Override
                    public X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[0];
                    }
                }
        };

        // 初始化SSLContext
        SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
        sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

        // 创建一个信任所有主机名的HostnameVerifier
        HostnameVerifier allHostsValid = new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };

        // 创建OkHttpClient实例
        return new OkHttpClient.Builder().sslSocketFactory(sslContext.getSocketFactory(),
                (X509TrustManager) trustAllCerts[0]).hostnameVerifier(allHostsValid).build();
    }

    @Override
    public <T> ResultVo<T> executeJson(String service, HttpRequestMethod method, String url, Object param,
                                       Class<T> resultType) {
        HttpClassicClientRequest request = this.httpClassicClient.createRequest(method, this.buildUrl(service, url));
        request.headers().add(HEADER_TOKEN, this.getOmsToken());
        if (param != null) {
            request.jsonEntity(param);
        }
        return this.execute(url, resultType, request);
    }

    private String getOmsToken() {
        try {
            CryptClient cryptClient = CryptClientServer.getCryptClient();
            String actualToken = cryptClient.getAccessKeyService().getSharedToken(SERVICE_INFO).getToken();
            return notBlank(actualToken, "The OMS token cannot be blank.");
        } catch (CryptoInvokeException e) {
            throw new IllegalStateException("Failed to retrieve OMS token", e);
        }
    }

    @Override
    public <T> ResultVo<T> executeText(String service, HttpRequestMethod method, String url, String param,
            Class<T> resultType) {
        HttpClassicClientRequest request = this.httpClassicClient.createRequest(method, this.buildUrl(service, url));
        request.headers().add(HEADER_TOKEN, this.getOmsToken());
        if (param != null) {
            request.entity(TextEntity.create(request, param));
        }
        return this.execute(url, resultType, request);
    }

    private <T> ResultVo<T> execute(String url, Class<T> resultType, HttpClassicClientRequest request) {
        ParameterizedType parameterizedType = TypeUtils.parameterized(ResultVo.class, new Type[] {resultType});
        try (HttpClassicClientResponse<ResultVo<T>> response = request.exchange(parameterizedType)) {
            if (response.statusCode() != HttpResponseStatus.OK.statusCode()) {
                throw new IllegalStateException(StringUtils.format("Failed to execute to OMS. [code={0}, msg={1}]",
                        response.statusCode(),
                        response.reasonPhrase()));
            }
            if (!response.objectEntity().isPresent()) {
                throw new IllegalStateException(StringUtils.format("OMS exchange failed with null response. [url={0}]",
                        url));
            }
            log.info("OMS exchange success. [url={}]", url);
            if (resultType == Void.class) {
                return new ResultVo<>();
            }
            return response.objectEntity().get().object();
        } catch (IOException e) {
            throw new IllegalStateException(StringUtils.format("Failed to fetch model list. [cause:{0}]",
                    e.getMessage()), e);
        }
    }

    @Override
    public <T> ResultVo<T> upload(String service, HttpRequestMethod method, String url, PartitionedEntity entity,
            Class<T> resultType) {
        RequestBody requestBody = this.buildRequestBody(entity);
        Request request = new Request.Builder().header(HEADER_TOKEN, this.getOmsToken())
                .url(this.buildUrl(service, url))
                .method(method.name(), requestBody)
                .build();
        try (Response response = this.okHttpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IllegalStateException(StringUtils.format("Failed to upload files to OMS. [code={0}, msg={1}]",
                        response.code(),
                        response.message()));
            }
            if (response.body() == null) {
                return new ResultVo<>();
            }
            ParameterizedType parameterizedType = TypeUtils.parameterized(ResultVo.class, new Type[] {resultType});
            if (resultType == Void.class) {
                return new ResultVo<>();
            }
            return cast(this.serializer.deserialize(response.body().byteStream(), parameterizedType));
        } catch (IOException e) {
            throw new IllegalStateException("Failed to fetch model list.", e);
        }
    }

    private RequestBody buildRequestBody(PartitionedEntity entity) {
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        List<NamedEntity> namedEntities = entity.entities();
        for (NamedEntity namedEntity : namedEntities) {
            String name = namedEntity.name();
            if (namedEntity.isFile()) {
                FileEntity file = namedEntity.asFile();
                builder.addFormDataPart(name, file.filename(), this.buildFileBody(file));
            }
            if (namedEntity.isText()) {
                builder.addFormDataPart(name, namedEntity.asText().content());
            }
        }
        return builder.build();
    }

    private RequestBody buildFileBody(FileEntity file) {
        MediaType mediaType = MediaType.parse(this.getContentType(file.filename()));
        return new RequestBody() {
            @Override
            public MediaType contentType() {
                return mediaType;
            }

            @Override
            public long contentLength() {
                return file.length();
            }

            @Override
            public void writeTo(@Nonnull BufferedSink sink) {
                try (InputStream fileInputStream = file.inputStream()) {
                    byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
                    int bytesRead;
                    while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                        Objects.requireNonNull(sink).write(buffer, 0, bytesRead);
                    }
                } catch (IOException e) {
                    throw new IllegalStateException("Failed to reading file and writing to sink.", e);
                }
            }
        };
    }

    private String getContentType(String fileName) {
        try {
            String contentType = Files.probeContentType(Paths.get(fileName));
            return !StringUtils.isBlank(contentType) ? contentType : DEFAULT_MIMETYPE;
        } catch (IOException e) {
            throw new IllegalStateException("Get file content type error.", e);
        }
    }

    private HttpClassicClient buildHttpClassicClient(HttpClassicClientFactory httpClassicClientFactory) {
        HttpClassicClientFactory.Config config = HttpClassicClientFactory.Config.builder()
                .custom(cast(MapBuilder.get().put(CLIENT_HTTP_IGNORE_TRUST, true).build()))
                .build();
        return httpClassicClientFactory.create(config);
    }

    private String buildUrl(String service, String url) {
        if (!SERVICE_NAMES.contains(service)) {
            throw new IllegalStateException(StringUtils.format("The service is invalid. [service={0}]", service));
        }
        return LockUtils.synchronize(this.lock, () -> {
            this.refresh(service);
            NetPoint netPoint = this.getNetPointByRoundRobin(service);
            return StringUtils.format("{0}://{1}:{2}{3}",
                    netPoint.getProtocol(),
                    netPoint.getHost(),
                    netPoint.getPort(),
                    url);
        });
    }

    private void refresh(String service) {
        if (!this.infos.containsKey(service) || this.infos.get(service).getLastRefreshTime() == -1L
                || this.infos.get(service).getLastRefreshTime() + EXPIRED_MILLISECOND < System.currentTimeMillis()) {
            this.refreshService(service);
        }
    }

    private void refreshService(String service) {
        List<Instance> instances = this.nacosClient.queryService(service);
        notEmpty(instances, StringUtils.format("Cannot find service from nacos. [service={0}]", service));
        this.infos.put(service, new ServiceInfo(instances));
    }

    private NetPoint getNetPointByRoundRobin(String service) {
        Instance oms = this.getOmsByRoundRobin(service);
        Map<String, String> metadata = oms.getMetadata();
        String protocol = metadata.containsKey(SECURE) && Objects.equals(metadata.get(SECURE), TRUE) ? HTTPS : HTTP;
        NetPoint netPoint = new NetPoint(protocol, oms.getIp(), oms.getPort());
        log.info("Got oms instance. [ip={}, port={}]", netPoint.getHost(), netPoint.getPort());
        return netPoint;
    }

    private Instance getOmsByRoundRobin(String service) {
        ServiceInfo serviceInfo = this.infos.get(service);
        List<Instance> instances = serviceInfo.getInstances();
        AtomicInteger curOmsIndex = serviceInfo.getCurOmsIndex();
        curOmsIndex.getAndUpdate(index -> (index + 1) % instances.size());
        return instances.get(curOmsIndex.get());
    }
}
