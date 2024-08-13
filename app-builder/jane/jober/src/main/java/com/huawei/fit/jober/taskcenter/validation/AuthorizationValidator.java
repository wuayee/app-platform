/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.validation;

/**
 * 为三方系统授权提供校验。
 *
 * @author 梁济时
 * @since 2023-11-27
 */
public interface AuthorizationValidator {
    /**
     * 校验三方系统授权的唯一标识。
     *
     * @param id 表示三方系统授权的唯一标识的 {@link String}。
     * @return 表示符合要求的三方系统授权的唯一标识的 {@link String}。
     */
    String id(String id);

    /**
     * 校验三方系统授权的系统名称。
     *
     * @param system 表示系统名称的 {@link String}。
     * @return 表示符合要求的系统名称的 {@link String}。
     */
    String system(String system);

    /**
     * 校验三方系统授权的用户唯一标识。
     *
     * @param user 表示用户唯一标识的 {@link String}。
     * @return 表示符合要求的用户唯一标识的 {@link String}。
     */
    String user(String user);

    /**
     * 校验三方系统授权的令牌。
     *
     * @param token 表示令牌的 {@link String}。
     * @return 表示符合要求的令牌的 {@link String}。
     */
    String token(String token);

    /**
     * 校验令牌有效期的毫秒数。
     *
     * @param expiration 表示令牌有效期毫秒数的 {@link Long}。
     * @return 表示符合要求的令牌有效期毫秒数的 {@link Long}。
     */
    Long expiration(Long expiration);
}
