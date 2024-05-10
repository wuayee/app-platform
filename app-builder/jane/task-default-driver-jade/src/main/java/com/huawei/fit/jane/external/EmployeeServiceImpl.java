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
public class EmployeeServiceImpl implements EmployeeService {

    private final HttpClassicClient client;

    private SearchEmployeeResponse searchEmployeeResponse;

    private GetPersonInfoResponse getPersonInfoResponse;

    public EmployeeServiceImpl(HttpClassicClientFactory factory) {
        Map<String, Object> map = new HashMap<>();
        map.put("secure.ignore-trust", true);
        map.put("secure.ignore-hostname", true);
        HttpClassicClientFactory.Config config = HttpClassicClientFactory.Config.builder().custom(map).build();
        this.client = factory.create(config);
    }

    @Override
    public SearchEmployeeResponse searchEmployee(String endpoint, String dynamicToken, String searchValue) {
        String requestUrl = "/f/idata/hr/HRPersonSearch?lang=zh&searchType=4&pagesize=50&searchValue=" + searchValue;
        try (HttpClassicClientRequest request = client.createRequest(HttpRequestMethod.GET, endpoint + requestUrl)) {
            request.headers().set("Authorization", dynamicToken);
            try (HttpClassicClientResponse<SearchEmployeeResponse> response = client.exchange(request,
                    SearchEmployeeResponse.class)) {
                response.objectEntity()
                        .ifPresent(searchEmployeeResponseObjectEntity -> searchEmployeeResponse
                                = searchEmployeeResponseObjectEntity.object());
            }
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        return searchEmployeeResponse;
    }

    @Override
    public GetPersonInfoResponse getPersonInfo(String endpoint, String dynamicToken, Map<String, Object> map) {
        String employeeNmuber = map.get("employee_number") == null ? "" : map.get("employee_number").toString();
        String uid = map.get("uid") == null ? "" : map.get("uid").toString();
        String uuid = map.get("uuid") == null ? "" : map.get("uuid").toString();
        String globalUserId = map.get("global_user_id") == null ? "" : map.get("global_user_id").toString();
        String requestUrl = "/f/idata/hr/getPersonInfo?lang=zh&employee_number=" + employeeNmuber + "&uid=" + uid
                + "&uuid=" + uuid + "&global_user_id=" + globalUserId;
        try (HttpClassicClientRequest request = client.createRequest(HttpRequestMethod.GET, endpoint + requestUrl)) {
            request.headers().set("Authorization", dynamicToken);
            try (HttpClassicClientResponse<GetPersonInfoResponse> response = client.exchange(request,
                    GetPersonInfoResponse.class)) {
                response.objectEntity()
                        .ifPresent(searchEmployeeResponseObjectEntity -> getPersonInfoResponse
                                = searchEmployeeResponseObjectEntity.object());
            }
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        return getPersonInfoResponse;
    }
}
