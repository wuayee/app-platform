/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.validation.impl;

import com.huawei.fit.jober.common.ErrorCodes;
import com.huawei.fit.jober.common.exceptions.BadRequestException;
import com.huawei.fit.jober.taskcenter.validation.IndexValidator;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.annotation.Value;
import com.huawei.fitframework.log.Logger;
import com.huawei.fitframework.util.StringUtils;

/**
 * 为 {@link IndexValidator} 提供默认实现。
 *
 * @author 梁济时 l00815032
 * @since 2024-01-05
 */
@Component
public class IndexValidatorImpl implements IndexValidator {
    private static final Logger log = Logger.get(IndexValidatorImpl.class);

    private final int nameLengthMaximum;

    public IndexValidatorImpl(@Value("${validation.index.name.length.maximum:128}") int nameLengthMaximum) {
        this.nameLengthMaximum = nameLengthMaximum;
    }

    @Override
    public String name(String name) {
        String actual = StringUtils.trim(name);
        if (actual.isEmpty()) {
            log.error("The name of index cannot be blank.");
            throw new BadRequestException(ErrorCodes.INDEX_NAME_REQUIRED);
        }
        if (actual.length() > this.nameLengthMaximum) {
            log.error("The length of name index is out of bounds. [name={}, maximum={}]",
                    actual, this.nameLengthMaximum);
            throw new BadRequestException(ErrorCodes.INDEX_NAME_LENGTH_OUT_OF_BOUNDS);
        }
        return actual;
    }
}
