/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.base.controller;

import com.huawei.fit.http.annotation.GetMapping;
import com.huawei.fit.http.annotation.PatchMapping;
import com.huawei.fit.http.annotation.PathVariable;
import com.huawei.fit.http.annotation.PostMapping;
import com.huawei.fit.http.annotation.RequestBody;
import com.huawei.fit.http.annotation.RequestMapping;
import com.huawei.fitframework.annotation.Component;
import com.huawei.jade.app.engine.base.common.resp.Response;
import com.huawei.jade.app.engine.base.common.resp.ResponseCode;
import com.huawei.jade.app.engine.base.dto.UserInfoDto;
import com.huawei.jade.app.engine.base.service.UserInfoService;

/**
 * 用户信息相关控制器
 *
 * @since 2024-5-30
 *
 */
@Component
@RequestMapping("/aipp/usr")
public class UserInfoController {
    private final UserInfoService userInfoService;

    public UserInfoController(UserInfoService userInfoService) {
        this.userInfoService = userInfoService;
    }

    /**
     * 创建用户信息记录
     *
     * @param userInfoDto 用户信息消息体
     * @return 用户信息 id
     */
    @PostMapping("/info")
    public Response<Long> createUsrAppCollection(@RequestBody UserInfoDto userInfoDto) {
        Long userInfoId = userInfoService.createUserInfo(userInfoDto);
        return Response.success(userInfoId, ResponseCode.OK);
    }

    /**
     * 修改用户信息
     *
     * @param userInfoDto 用户信息消息体
     * @return 响应信息
     */
    @PatchMapping("/info")
    public Response<Void> updateUsrAppCollection(@RequestBody UserInfoDto userInfoDto) {
        userInfoService.updateUserInfo(userInfoDto);
        return Response.success(ResponseCode.OK);
    }

    /**
     * 获取用户信息记录
     *
     * @param userName 用户名
     * @return 用户信息
     */
    @GetMapping("/info/{userName}")
    public Response<UserInfoDto> createUsrAppCollection(@PathVariable("userName") String userName) {
        return Response.success(userInfoService.getUserInfo(userName), ResponseCode.OK);
    }
}
