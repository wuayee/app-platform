/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.validation.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.huawei.fit.jober.common.ErrorCodes;
import com.huawei.fit.jober.common.exceptions.BadRequestException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("测试 AuthorizationValidatorImpl")
class AuthorizationValidatorImplTest {
    private static final int SYSTEM_LENGTH_MAXIMUM = 5;

    private static final int USER_ID_LENGTH_MAXIMUM = 5;

    private AuthorizationValidatorImpl validator;

    @BeforeEach
    void setup() {
        this.validator = new AuthorizationValidatorImpl(SYSTEM_LENGTH_MAXIMUM, USER_ID_LENGTH_MAXIMUM);
    }

    @Test
    @DisplayName("当唯一标识不符合要求时抛出异常")
    void should_throw_when_id_is_blank() {
        BadRequestException ex = assertThrows(BadRequestException.class, () -> this.validator.id(" hello "));
        assertEquals(ErrorCodes.AUTHORIZATION_ID_INVALID.getErrorCode(), ex.getCode());
    }

    @Test
    @DisplayName("当授权系统为 null 时抛出异常")
    void should_throw_when_system_is_blank() {
        BadRequestException ex = assertThrows(BadRequestException.class, () -> this.validator.system(null));
        assertEquals(ErrorCodes.AUTHORIZATION_SYSTEM_REQUIRED.getErrorCode(), ex.getCode());
    }

    @Test
    @DisplayName("当授权系统超出长度限制时抛出异常")
    void should_throw_when_system_is_too_long() {
        BadRequestException ex = assertThrows(BadRequestException.class, () -> this.validator.system("system"));
        assertEquals(ErrorCodes.AUTHORIZATION_SYSTEM_TOO_LONG.getErrorCode(), ex.getCode());
    }

    @Test
    @DisplayName("当授权系统符合要求时，返回符合要求的授权系统")
    void should_return_system_when_valid() {
        String system = this.validator.system(" hello ");
        assertEquals("hello", system);
    }

    @Test
    @DisplayName("当用户唯一标识为 null 时抛出异常")
    void should_throw_when_user_id_is_null() {
        BadRequestException ex = assertThrows(BadRequestException.class, () -> this.validator.user(null));
        assertEquals(ErrorCodes.AUTHORIZATION_USER_REQUIRED.getErrorCode(), ex.getCode());
    }

    @Test
    @DisplayName("当用户唯一标识超出长度限制时抛出异常")
    void should_throw_when_user_id_is_too_long() {
        BadRequestException ex = assertThrows(BadRequestException.class, () -> this.validator.user("userId"));
        assertEquals(ErrorCodes.AUTHORIZATION_USER_TOO_LONG.getErrorCode(), ex.getCode());
    }

    @Test
    @DisplayName("当用户唯一标识符合要求时，返回符合要求的用户唯一标识")
    void should_return_user_id_when_valid() {
        String userId = this.validator.user(" hello ");
        assertEquals("hello", userId);
    }

    @Test
    @DisplayName("当令牌为空白字符串时抛出异常")
    void should_throw_when_token_is_blank() {
        BadRequestException ex = assertThrows(BadRequestException.class, () -> this.validator.token("    "));
        assertEquals(ErrorCodes.AUTHORIZATION_TOKEN_REQUIRED.getErrorCode(), ex.getCode());
    }

    @Test
    @DisplayName("当令牌符合要求时，返回符合要求的令牌")
    void should_return_token_when_valid() {
        String token = this.validator.token(" hello ");
        assertEquals("hello", token);
    }
}