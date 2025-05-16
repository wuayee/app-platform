/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.appbuilder.gateway.service;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import modelengine.appbuilder.gateway.config.SslHttpClientFactory;
import modelengine.appbuilder.gateway.entity.response.ResultVo;
import modelengine.appbuilder.gateway.utils.Pbkdf2Util;

import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

/**
 * Api Key 鉴权服务。
 *
 * @author 李智超
 * @since 2024-12-11
 */
@Service
@Slf4j
public class ApikeyAuthService {
    private static final Pattern API_KEY_PATTERN = Pattern.compile("Bearer ME-sk-[A-Za-z0-9]{16}-[A-Za-z0-9]{32}");
    private static final String APIKEY_AUTH_BODY_TEMPLATE = "{\"encrypted_key\":\"%s\"}";

    @Getter
    private final Map<String, Integer> authInfo = new ConcurrentHashMap<>();

    @Value("${oms.apikey.url}")
    private String verifyApiKeyUrl;

    @Autowired
    private SslHttpClientFactory sslHttpClientFactory;

    /**
     * api key 鉴权。
     *
     * @param apikey 表示 api key 的 {@link String}。
     * @return Boolean 鉴权是否成功。
     */
    public Boolean authApikeyInfo(String apikey) {
        log.info("Starting API key authentication for API key:");
        if (!isValidApiKey(apikey)) {
            log.error("API key is invalid");
            return Boolean.FALSE;
        }
        List<String> infos = Arrays.asList(apikey.split("-"));
        String salt = infos.get(2);
        String sk = infos.get(3);
        String encrypted = Pbkdf2Util.pbkdf2ForPassStandard(sk, salt);
        ResultVo queryResponse = buildPostRequest(salt + '-' + encrypted);
        if (queryResponse.getData().equals(Boolean.TRUE)) {
            log.info("API key authentication successful.");
            return Boolean.TRUE;
        }
        log.info("API key authentication failed.");
        return Boolean.FALSE;
    }

    private ResultVo buildPostRequest(String apikey) {
        HttpPost httpPost = new HttpPost(this.verifyApiKeyUrl);
        try (StringEntity jsonBody = new StringEntity(String.format(Locale.ROOT, APIKEY_AUTH_BODY_TEMPLATE, apikey),
                ContentType.APPLICATION_JSON); CloseableHttpClient client = this.sslHttpClientFactory.getHttpClient()) {
            httpPost.setEntity(jsonBody);
            CloseableHttpResponse response = client.execute(httpPost);
            String responseBody = EntityUtils.toString(response.getEntity());
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(responseBody, ResultVo.class);
        } catch (IOException e) {
            log.error("I/O exception occurred during POST request: {}", e.getMessage(), e);
            return ResultVo.builder().data(false).build();
        } catch (ParseException e) {
            log.error("Can't parse verify result. Cause: {}", e.getMessage(), e);
            return ResultVo.builder().data(false).build();
        }
    }

    private static boolean isValidApiKey(String apiKey) {
        if (apiKey == null || apiKey.isEmpty()) {
            log.warn("API key is null or empty.");
            return false;
        }
        return API_KEY_PATTERN.matcher(apiKey).matches();
    }
}
