/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.taskcenter.service.impl;

import modelengine.fit.jane.task.util.Entities;
import modelengine.fit.jober.taskcenter.service.PluginLoginService;
import modelengine.fit.jober.taskcenter.util.DynamicSqlExecutor;

import lombok.RequiredArgsConstructor;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.transaction.Transactional;

import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;

/**
 * 这个类用来请求cookie
 *
 * @author 姚江
 * @since 2024-7-26
 */
@Component
@RequiredArgsConstructor
public class PluginLoginServiceImpl implements PluginLoginService {
    private static final String LOGIN_SUCCEEDED = "<pre style='text-align: center;font-size: 26px;' >"
            + "Login succeeded. You can close this page. \n登录成功，您可关闭此页面。</pre>";

    private static final String LOGIN_FAILED = "<pre style='text-align: center;font-size: 26px;' >"
            + "Login Failed. Please try again later. \n登录失败，请稍后再试。</pre>";

    private final DynamicSqlExecutor executor;

    @Override
    @Transactional
    public void delete(String clientId) {
        String sql = "INSERT INTO client_login (id, client_id) VALUES(?, ?) "
                + "ON CONFLICT(client_id) DO UPDATE set cookie=NULL";
        String id = Entities.generateId();

        executor.executeUpdate(sql, Arrays.asList(id, clientId));
    }

    @Override
    @Transactional
    public String save(String clientId, String cookie) {
        String sql = "INSERT INTO client_login (id, client_id, cookie) VALUES(?, ?, ?) "
                + "ON CONFLICT(client_id) DO UPDATE set cookie=EXCLUDED.cookie";
        String id = Entities.generateId();

        int i = executor.executeUpdate(sql, Arrays.asList(id, clientId, cookie));
        return i == 1 ? LOGIN_SUCCEEDED : LOGIN_FAILED;
    }

    @Override
    @Transactional
    public String get(String clientId) {
        String sql = "SELECT cookie FROM client_login WHERE client_id = ?";
        Object result = executor.executeScalar(sql, Collections.singletonList(clientId));

        return Objects.isNull(result) ? "NULL" : result.toString();
    }
}
