/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.validation.impl;

import com.huawei.fit.jane.task.util.Entities;
import com.huawei.fit.jober.common.ErrorCodes;
import com.huawei.fit.jober.common.exceptions.BadRequestException;
import com.huawei.fit.jober.taskcenter.validation.AuthorizationValidator;

import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Value;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.util.StringUtils;

/**
 * 为 {@link AuthorizationValidator} 提供实现
 *
 * @author 陈镕希
 * @since 2023-10-10
 */
@Component
public class AuthorizationValidatorImpl implements AuthorizationValidator {
    private static final Logger log = Logger.get(AuthorizationValidatorImpl.class);

    private final int systemLengthMaximum;

    private final int userLengthMaximum;

    public AuthorizationValidatorImpl(
            @Value("${validation.authorization.system.length.maximum:64}") int systemLengthMaximum,
            @Value("${validation.authorization.user.length.maximum:127}") int userLengthMaximum
    ) {
        this.systemLengthMaximum = systemLengthMaximum;
        this.userLengthMaximum = userLengthMaximum;
    }

    @Override
    public String id(String id) {
        return Entities.validateId(StringUtils.trim(id), () -> {
            log.error("The id of authorization is invalid. [id={}]", id);
            return new BadRequestException(ErrorCodes.AUTHORIZATION_ID_INVALID);
        });
    }

    @Override
    public String system(String system) {
        String actual = StringUtils.trim(system);
        if (StringUtils.isEmpty(actual)) {
            log.error("The system of authorization cannot be a blank string.");
            throw new BadRequestException(ErrorCodes.AUTHORIZATION_SYSTEM_REQUIRED);
        }
        if (actual.length() > this.systemLengthMaximum) {
            log.error("The length of authorization system is out of bounds. [system={}, length={}, maximum={}]", actual,
                    actual.length(), this.systemLengthMaximum);
            throw new BadRequestException(ErrorCodes.AUTHORIZATION_SYSTEM_TOO_LONG);
        }
        return actual;
    }

    @Override
    public String user(String user) {
        String actual = StringUtils.trim(user);
        if (StringUtils.isEmpty(actual)) {
            log.error("The user id of authorization cannot be a blank string.");
            throw new BadRequestException(ErrorCodes.AUTHORIZATION_USER_REQUIRED);
        }
        if (actual.length() > this.userLengthMaximum) {
            log.error("The length of authorization user id is out of bounds. [user={}, length={}, maximum={}]", actual,
                    actual.length(), this.userLengthMaximum);
            throw new BadRequestException(ErrorCodes.AUTHORIZATION_USER_TOO_LONG);
        }
        return actual;
    }

    @Override
    public String token(String token) {
        String actual = StringUtils.trim(token);
        if (StringUtils.isEmpty(actual)) {
            log.error("The token of authorization cannot be a blank string.");
            throw new BadRequestException(ErrorCodes.AUTHORIZATION_TOKEN_REQUIRED);
        }
        return actual;
    }

    @Override
    public Long expiration(Long expiration) {
        if (expiration == null) {
            return 0L;
        }
        if (expiration < 0L) {
            log.error("The expiration of token cannot be negative. [expiration={}]", expiration);
            throw new BadRequestException(ErrorCodes.AUTHORIZATION_EXPIRATION_NEGATIVE);
        }
        return expiration;
    }
}
