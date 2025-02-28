/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.taskcenter.validation.impl;

import modelengine.fit.jober.taskcenter.validation.IndexValidator;

import modelengine.fit.jober.common.ErrorCodes;
import modelengine.fit.jober.common.exceptions.BadRequestException;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Value;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.util.StringUtils;

/**
 * 为 {@link IndexValidator} 提供默认实现。
 *
 * @author 梁济时
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
