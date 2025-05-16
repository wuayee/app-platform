/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.taskcenter.controller;

import lombok.RequiredArgsConstructor;
import modelengine.fit.http.annotation.DocumentIgnored;
import modelengine.fit.http.annotation.GetMapping;
import modelengine.fit.http.annotation.RequestMapping;
import modelengine.fit.http.annotation.RequestParam;
import modelengine.fit.http.server.HttpClassicServerRequest;
import modelengine.fit.jane.task.gateway.Authenticator;
import modelengine.fit.jane.task.gateway.EmployeeVO;
import modelengine.fit.jane.task.gateway.PersonService;
import modelengine.fit.jane.task.gateway.User;
import modelengine.fit.jober.common.ErrorCodes;
import modelengine.fit.jober.common.exceptions.JobberParamException;
import modelengine.fit.jober.common.model.JoberResponse;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.inspection.Validation;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 获取用户信息Controller。
 *
 * @author 陈镕希
 * @since 2023-08-02
 */
@Component
@DocumentIgnored
@RequiredArgsConstructor
@RequestMapping("/v1/user")
public class UserController {
    private final Authenticator authenticator;

    private final PersonService personService;

    /**
     * 获取登录用户信息。
     *
     * @param httpRequest http请求
     * @return 登录用户信息的 {@link JoberResponse}。
     */
    @GetMapping(path = "/sso_login_info")
    public JoberResponse<Map<String, Object>> getUserLoginInfo(HttpClassicServerRequest httpRequest) {
        User userInfo = this.authenticator.authenticate(httpRequest);
        Map<String, Object> values = new LinkedHashMap<>(3);
        values.put("account", userInfo.account());
        values.put("chineseName", userInfo.fqn());
        return JoberResponse.success(values);
    }

    /**
     * 模糊搜索对应员工信息列表。
     *
     * @param keyword 搜索的用户关键字的 {@link JoberResponse}。
     * @param httpRequest http请求
     * @return 员工信息列表的 {@link JoberResponse}{@code <}{@link List}{@code <}{@link EmployeeVO}{@code >}{@code >}。
     */
    @GetMapping
    public JoberResponse<List<EmployeeVO>> searchEmployee(@RequestParam("keyword") String keyword,
        HttpClassicServerRequest httpRequest) {
        String uid = this.authenticator.authenticate(httpRequest).account();
        Validation.notBlank(keyword, () -> new JobberParamException(ErrorCodes.INPUT_PARAM_IS_EMPTY, "keyword"));
        return JoberResponse.success(this.personService.searchEmployeeInfo(keyword, uid));
    }
}