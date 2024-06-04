/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.base.mapper;

import com.huawei.jade.app.engine.base.dto.UserInfoDto;

/**
 * 用户信息相关映射
 *
 * @since 2024-5-30
 *
 */
public interface UserInfoMapper {
    /**
     * 插入用户信息
     *
     * @param userInfoDto 用户信息消息体
     */
    void insert(UserInfoDto userInfoDto);

    /**
     * 更新用户信息
     *
     * @param userInfoDto 用户信息消息体
     */
    void update(UserInfoDto userInfoDto);

    /**
     * 查询用户信息
     *
     * @param userName 用户名
     * @return 用户信息消息体
     */
    UserInfoDto get(String userName);

    /**
     * 将所有当前默认应用为 appId 的用户重置为 defaultApp
     *
     * @param appId 应用 id
     * @param defaultApp 要重置为的应用 id
     */
    void resetDefaultAppByAppId(String appId, String defaultApp);
}
