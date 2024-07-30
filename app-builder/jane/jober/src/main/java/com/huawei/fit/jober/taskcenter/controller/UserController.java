/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.controller;

import com.huawei.fit.http.annotation.DocumentIgnored;
import com.huawei.fit.http.annotation.GetMapping;
import com.huawei.fit.http.annotation.RequestMapping;
import com.huawei.fit.http.annotation.RequestParam;
import com.huawei.fit.http.server.HttpClassicServerRequest;
import com.huawei.fit.jane.task.gateway.Authenticator;
import com.huawei.fit.jane.task.gateway.EmployeeVO;
import com.huawei.fit.jane.task.gateway.PersonService;
import com.huawei.fit.jane.task.gateway.User;
import com.huawei.fit.jober.common.ErrorCodes;
import com.huawei.fit.jober.common.exceptions.JobberParamException;
import com.huawei.fit.jober.common.model.JoberResponse;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.inspection.Validation;

import lombok.RequiredArgsConstructor;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 获取用户信息Controller。
 *
 * @author 陈镕希 c00572808
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
