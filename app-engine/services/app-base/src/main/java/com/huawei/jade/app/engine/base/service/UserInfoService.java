/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.base.service;

import com.huawei.jade.app.engine.base.dto.UserInfoDto;

/**
 * 用户信息接口
 *
 * @since 2024-5-30
 *
 */
public interface UserInfoService {
    /**
     * 插入用户信息
     *
     * @param userInfoDto 用户信息消息体
     * @return 用户信息 id
     */
    Long createUserInfo(UserInfoDto userInfoDto);

    /**
     * 更新用户信息
     *
     * @param userInfoDto 用户信息
     */
    void updateUserInfo(UserInfoDto userInfoDto);

    /**
     * 查询用户信息
     *
     * @param userName 用户名
     * @return 用户信息
     */
    UserInfoDto getUserInfo(String userName);
}
