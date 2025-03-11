/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.appbuilder.gateway.filter;

import static org.apache.http.util.Args.notBlank;
import static org.springframework.util.Assert.notEmpty;

import com.huawei.framework.crypt.grpc.client.CryptClient;
import com.huawei.framework.crypt.grpc.client.exception.CryptoInvokeException;

import com.alibaba.nacos.api.naming.pojo.Instance;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import modelengine.appbuilder.gateway.config.SslIgnoreHttpClientFactory;
import modelengine.appbuilder.gateway.entity.NetPoint;
import modelengine.appbuilder.gateway.entity.ServiceInfo;
import modelengine.appbuilder.gateway.entity.response.ResultVo;
import modelengine.appbuilder.gateway.nacos.DefaultNacosClient;
import modelengine.appbuilder.gateway.service.CryptClientServer;
import modelengine.appbuilder.gateway.utils.UserUtil;
import reactor.core.publisher.Mono;

import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.OrderedGatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;

import java.io.IOException;
import java.net.URLConnection;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Oms 鉴权过滤器。
 *
 * @author 邬涨财
 * @since 2025-01-02
 */
@Slf4j
@Component
public class OmsAuthFilter extends AbstractGatewayFilterFactory<OmsAuthFilter.Config> {
    private static final int OMS_AUTH_FILTER_ORDER = -2;
    private static final String AUTH_TOKEN_NEW_HEADER_KEY = "X-Auth-Token";
    private static final String CSRF_TOKEN_NEW_HEADER_KEY = "X-Csrf-Token";
    private static final String AUTH_TOKEN_INNER_KEY = "X-Auth-Token-Inner";
    private static final String AUTH_TOKEN_KEY = "__Host-X-Auth-Token";
    private static final String APP_ENGINE_AUTH_TOKEN_KEY = "appengine-auth-token";
    private static final String APP_ENGINE_CSRF_TOKEN_KEY = "appengine-csrf-token";
    private static final String CSRF_TOKEN_KEY = "__Host-X-Csrf-Token";
    private static final List<String> STATIC_CONTROLLER = List.of("v1/api/31f20efc7e0848deab6a6bc10fc3021e/file");
    private static final List<String> STATIC_EXTENSIONS = Arrays.asList(".html",
            ".css",
            ".js",
            ".jpg",
            ".jpeg",
            ".png",
            ".gif",
            ".svg",
            ".woff",
            ".woff2",
            ".ttf",
            ".eot",
            ".json",
            ".xml",
            ".pdf");
    private static final long EXPIRED_MILLISECOND = 30000L;
    private static final String HTTPS = "https";
    private static final String GET_MACHINE_TOKEN_SERVICE_NAME = "machine_account_token_key";
    private static final com.huawei.framework.crypt.grpc.client.model.ServiceInfo SERVICE_INFO =
            new com.huawei.framework.crypt.grpc.client.model.ServiceInfo(GET_MACHINE_TOKEN_SERVICE_NAME);

    @Value("${oms.gateway.url}")
    private String omsGatewayUrl;

    @Value("${oms.service-name}")
    private String omsServiceName;

    private final String path = "/framework/v1/iam/roles/query-by-token";

    private final SslIgnoreHttpClientFactory sslHttpClientFactory;
    private final DefaultNacosClient nacosClient;
    private Map<String, ServiceInfo> infos;

    OmsAuthFilter(SslIgnoreHttpClientFactory sslHttpClientFactory, DefaultNacosClient nacosClient) {
        super(Config.class);
        this.sslHttpClientFactory = sslHttpClientFactory;
        this.nacosClient = nacosClient;
        this.infos = new HashMap<>();
    }

    @Override
    public GatewayFilter apply(Config config) {
        log.info("OmsAuthFilter is apply");
        return new OrderedGatewayFilter(this::filter, OMS_AUTH_FILTER_ORDER);
    }

    private Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        log.info("Start oms auth filter.");
        MultiValueMap<String, HttpCookie> cookies = request.getCookies();
        String uri = request.getURI().getPath();
        log.info("Oms auth filter uri: {}", uri);
        String authToken = this.getToken(cookies, AUTH_TOKEN_KEY, uri, exchange, APP_ENGINE_AUTH_TOKEN_KEY);
        String csrfToken = this.getToken(cookies, CSRF_TOKEN_KEY, uri, exchange, APP_ENGINE_CSRF_TOKEN_KEY);
        String fullPath = this.buildUrl(this.omsServiceName, this.path);
        log.info("oms auth full path: {}", fullPath);
        HttpPost httpPost = new HttpPost(fullPath);
        httpPost.setHeader(AUTH_TOKEN_NEW_HEADER_KEY, authToken);
        httpPost.setHeader(CSRF_TOKEN_NEW_HEADER_KEY, csrfToken);
        httpPost.setHeader(AUTH_TOKEN_INNER_KEY, this.getOmsToken());
        ResultVo<List<String>> resultVo = this.send(httpPost);
        if (resultVo.getData().isEmpty()) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            log.error("Authentication failed: Token is null or invalid.");
            return exchange.getResponse().setComplete();
        }
        String userName = resultVo.getData().get(0);
        ServerHttpRequest newRequest;
        try {
            newRequest = UserUtil.buildNewRequestBuilder(exchange, userName).build();
        } catch (CryptoInvokeException e) {
            exchange.getResponse().setStatusCode(HttpStatus.BAD_GATEWAY);
            log.error("Authentication failed: Failed to generate user info by oms auth.");
            return exchange.getResponse().setComplete();
        }
        this.setResponseHeader(exchange, request);
        return chain.filter(exchange.mutate().request(newRequest).build());
    }

    private Object getOmsToken() {
        try {
            CryptClient cryptClient = CryptClientServer.getCryptClient();
            String actualToken = cryptClient.getAccessKeyService().getSharedToken(SERVICE_INFO).getToken();
            return notBlank(actualToken, "The OMS token cannot be blank.");
        } catch (CryptoInvokeException e) {
            throw new IllegalStateException("Failed to retrieve OMS token", e);
        }
    }

    private String getToken(MultiValueMap<String, HttpCookie> cookies, String omsTokenKey, String uri,
            ServerWebExchange exchange, String appEngineTokenKey) {
        String token = "";
        if (cookies.containsKey(omsTokenKey)) {
            token = Objects.requireNonNull(cookies.getFirst(omsTokenKey)).getValue();
            if (this.isStaticPath(uri)) {
                log.info("Start to add app engine token");
                ResponseCookie cookie = this.buildTokenCookie(appEngineTokenKey, token);
                exchange.getResponse().addCookie(cookie);
            }
        } else if (cookies.containsKey(appEngineTokenKey) && (this.isStaticPath(uri) || this.isStaticController(uri))) {
            token = Objects.requireNonNull(cookies.getFirst(appEngineTokenKey)).getValue();
        } else {
            return token;
        }
        return token;
    }

    private boolean isStaticController(String uri) {
        return STATIC_CONTROLLER.stream().anyMatch(uri::contains);
    }

    private ResponseCookie buildTokenCookie(String key, String value) {
        return ResponseCookie.from(key, value).sameSite("None").secure(true).httpOnly(true).path("/").build();
    }

    private boolean isStaticPath(String uri) {
        for (String ext : STATIC_EXTENSIONS) {
            if (uri.toLowerCase(Locale.ROOT).endsWith(ext)) {
                return true;
            }
        }
        String mimeType = URLConnection.guessContentTypeFromName(uri);
        return !StringUtils.isEmpty(mimeType);
    }

    private void setResponseHeader(ServerWebExchange exchange, ServerHttpRequest request) {
        String uri = exchange.getRequest().getURI().getPath();
        this.setContentType(exchange, request, uri);
        this.setSTP(exchange, uri);
        if (uri.endsWith(".js") && (request.getHeaders().containsKey("Origin"))) {
            exchange.getResponse()
                    .getHeaders()
                    .set("Access-Control-Allow-Origin", String.valueOf(request.getHeaders().getFirst("Origin")));
            exchange.getResponse().getHeaders().set("Access-Control-Allow-Credentials", "true");
        }
    }

    private void setSTP(ServerWebExchange exchange, String uri) {
        if (this.isStaticPath(uri)) {
            exchange.getResponse().getHeaders().set("Strict-Transport-Security", "max-age=31536000; includeSubDomains");
        }
    }

    private void setContentType(ServerWebExchange exchange, ServerHttpRequest request, String uri) {
        String mimeType = URLConnection.guessContentTypeFromName(uri);
        if (uri.endsWith(".js")) {
            exchange.getResponse().getHeaders().set("Content-Type", "application/javascript");
        } else if (uri.endsWith(".css")) {
            exchange.getResponse().getHeaders().set("Content-Type", "text/css");
        } else if (uri.endsWith(".html")) {
            exchange.getResponse().getHeaders().set("Content-Type", "text/html");
        } else if (uri.endsWith(".txt")) {
            exchange.getResponse().getHeaders().set("Content-Type", "text/plain");
        } else if (uri.endsWith(".json")) {
            exchange.getResponse().getHeaders().set("Content-Type", "application/json");
        } else if (!StringUtils.isEmpty(mimeType)) {
            exchange.getResponse().getHeaders().set("Content-Type", mimeType);
        } else {
            log.info("Content-Type is not matched");
        }

        // 这块逻辑和上面的判断不能调换，否则会出现覆盖场景
        if (request.getHeaders().containsKey("is_iframe") && uri.endsWith(".html")) {
            String contentType = "";
            String isIframe = request.getHeaders().getFirst("is_iframe");
            if ("1".equals(isIframe)) {
                contentType = "text/html";
            } else if ("0".equals(isIframe)) {
                contentType = "text/plain";
            } else {
                log.info("is_iframe is not matched");
            }
            if (!contentType.isEmpty()) {
                exchange.getResponse().getHeaders().set("Content-Type", contentType);
            }
        }
    }

    private ResultVo send(HttpPost httpPost) {
        try (CloseableHttpClient client = this.sslHttpClientFactory.getHttpClient()) {
            CloseableHttpResponse response = client.execute(httpPost);
            log.info("response code: {}", response.getCode());
            String responseBody = EntityUtils.toString(response.getEntity());
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(responseBody, ResultVo.class);
        } catch (IOException | NoSuchAlgorithmException | KeyStoreException | KeyManagementException e) {
            log.error("I/O exception occurred during POST request: {}", e.getMessage(), e);
            return ResultVo.builder().data(Collections.emptyList()).build();
        } catch (ParseException e) {
            log.error("Can't parse verify result. Cause: {}", e.getMessage(), e);
            return ResultVo.builder().data(Collections.emptyList()).build();
        }
    }

    private String buildUrl(String service, String url) {
        this.refresh(service);
        NetPoint netPoint = this.getNetPointByRoundRobin(service);
        return String.format(Locale.ROOT,
                "%s://%s:%d%s",
                netPoint.getProtocol(),
                netPoint.getHost(),
                netPoint.getPort(),
                url);
    }

    private void refresh(String service) {
        if (!this.infos.containsKey(service) || this.infos.get(service).getLastRefreshTime() == -1L
                || this.infos.get(service).getLastRefreshTime() + EXPIRED_MILLISECOND < System.currentTimeMillis()) {
            this.refreshService(service);
        }
    }

    private void refreshService(String service) {
        List<Instance> instances = this.nacosClient.queryService(service);
        notEmpty(instances, String.format("Cannot find service from nacos. [service=%s]", service));
        this.infos.put(service, new ServiceInfo(instances));
    }

    private NetPoint getNetPointByRoundRobin(String service) {
        Instance oms = this.getOmsByRoundRobin(service);
        NetPoint netPoint = new NetPoint(HTTPS, oms.getIp(), oms.getPort());
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

    /**
     * Apikey 鉴权过滤器的构造工厂类的配置类。
     *
     * @author 邬涨财
     * @since 2025-01-02
     */
    @Data
    @NoArgsConstructor
    public static class Config {}
}
