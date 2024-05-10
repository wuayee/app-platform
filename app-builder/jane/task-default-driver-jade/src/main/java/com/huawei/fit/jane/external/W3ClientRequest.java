/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jane.external;

import com.huawei.fit.http.client.HttpClassicClient;
import com.huawei.fit.http.client.HttpClassicClientFactory;
import com.huawei.fit.http.client.HttpClassicClientRequest;
import com.huawei.fit.http.client.HttpClassicClientResponse;
import com.huawei.fit.http.protocol.HttpRequestMethod;
import com.huawei.fitframework.annotation.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class W3ClientRequest implements W3Client {

    private final HttpClassicClient client;

    private GetDynamicTokenResponse getDynamicTokenResponse;

    public W3ClientRequest(HttpClassicClientFactory factory) {
        Map<String, Object> map = new HashMap<>();
        map.put("secure.ignore-trust", true);
        map.put("secure.ignore-hostname", true);
        HttpClassicClientFactory.Config config = HttpClassicClientFactory.Config.builder().custom(map).build();
        this.client = factory.create(config);
    }

    @Override
    public GetDynamicTokenResponse getDynamicToken(String endpoint, SoaTokenInfo soaTokenInfo) {
        String requestUrl = "/ApiCommonQuery/appToken/getRestAppDynamicToken";
        try (HttpClassicClientRequest request = client.createRequest(HttpRequestMethod.POST, endpoint + requestUrl)) {
            request.jsonEntity(soaTokenInfo);
            try (HttpClassicClientResponse<GetDynamicTokenResponse> response = client.exchange(request,
                    GetDynamicTokenResponse.class)) {
                response.objectEntity()
                        .ifPresent(getDynamicTokenResponseObjectEntity -> getDynamicTokenResponse
                                = getDynamicTokenResponseObjectEntity.object());
            }
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        return getDynamicTokenResponse;
    }
}
