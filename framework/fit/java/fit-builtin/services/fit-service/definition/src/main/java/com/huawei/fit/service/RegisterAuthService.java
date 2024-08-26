/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.service;

import com.huawei.fit.service.entity.ClientTokenInfo;

import java.time.Instant;

/**
 * 表示注册服务用于权限验证的服务接口。
 *
 * @author 李金绪
 * @since 2024-07-16
 */
public interface RegisterAuthService {
    /**
     * 申请令牌。
     *
     * @param obtainedAt 表示获取令牌的时间的 {@link Instant}。
     */
    void applyToken(Instant obtainedAt);

    /**
     * 刷新令牌。
     *
     * @param obtainedAt 表示获取令牌的时间的 {@link Instant}。
     */
    void refreshToken(Instant obtainedAt);

    /**
     * 获取令牌并保证令牌有效。
     *
     * @return 表示已获取的令牌的信息的 {@link ClientTokenInfo}。
     */
    ClientTokenInfo getToken();

    /**
     * 仅获取令牌。
     *
     * @return 表示已获取的令牌的信息的 {@link ClientTokenInfo}。
     */
    ClientTokenInfo getTokenWithoutCheck();
}
