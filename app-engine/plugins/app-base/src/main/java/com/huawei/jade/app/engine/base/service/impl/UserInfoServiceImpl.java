/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.base.service.impl;

import com.huawei.fitframework.annotation.Component;
import com.huawei.jade.app.engine.base.dto.UserInfoDto;
import com.huawei.jade.app.engine.base.mapper.UserInfoMapper;
import com.huawei.jade.app.engine.base.service.UserInfoService;

/**
 * 用户信息服务接口实现
 *
 * @since 2024-5-30
 *
 */
@Component
public class UserInfoServiceImpl implements UserInfoService {
    private final UserInfoMapper userInfoMapper;

    public UserInfoServiceImpl(UserInfoMapper userInfoMapper) {
        this.userInfoMapper = userInfoMapper;
    }

    /**
     * 插入用户信息
     *
     * @param userInfoDto 用户信息消息体
     * @return 用户信息 id
     */
    @Override
    public Long createUserInfo(UserInfoDto userInfoDto) {
        userInfoMapper.insert(userInfoDto);
        return userInfoDto.getId();
    }

    /**
     * 更新用户信息
     *
     * @param userInfoDto 用户信息
     */
    @Override
    public void updateUserInfo(UserInfoDto userInfoDto) {
        userInfoMapper.update(userInfoDto);
    }

    /**
     * 查询用户信息
     *
     * @param userName 用户名
     * @return 用户信息
     */
    @Override
    public UserInfoDto getUserInfo(String userName) {
        return userInfoMapper.get(userName);
    }
}
