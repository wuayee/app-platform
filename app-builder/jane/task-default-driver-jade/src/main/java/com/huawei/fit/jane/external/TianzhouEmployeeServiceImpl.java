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
public class TianzhouEmployeeServiceImpl implements TianzhouEmployeeService {

    private final HttpClassicClient client;

    private SearchEmployeeUidResponse searchEmployeeUidResponse;

    public TianzhouEmployeeServiceImpl(HttpClassicClientFactory factory) {
        Map<String, Object> map = new HashMap<>();
        map.put("secure.ignore-trust", true);
        map.put("secure.ignore-hostname", true);
        HttpClassicClientFactory.Config config = HttpClassicClientFactory.Config.builder().custom(map).build();
        this.client = factory.create(config);
    }

    @Override
    public SearchEmployeeUidResponse searchEmployeeUid(String endpoint, String appId, String appKey, String keyword,
            String uid) {
        String requestUrl = "/api/framework/v1/user/search?keyword=" + keyword + "&uid=" + uid;
        try (HttpClassicClientRequest request = client.createRequest(HttpRequestMethod.GET, endpoint + requestUrl)) {
            request.headers().set("X-HW-ID", appId);
            request.headers().set("X-HW-APPKEY", appKey);
            try (HttpClassicClientResponse<SearchEmployeeUidResponse> response = client.exchange(request,
                    SearchEmployeeUidResponse.class)) {
                response.objectEntity()
                        .ifPresent(employeeUidResponseObjectEntity -> searchEmployeeUidResponse
                                = employeeUidResponseObjectEntity.object());
            }
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        return searchEmployeeUidResponse;
    }
}
