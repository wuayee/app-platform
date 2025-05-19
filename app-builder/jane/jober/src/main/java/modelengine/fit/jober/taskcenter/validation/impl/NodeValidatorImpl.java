/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.taskcenter.validation.impl;

import modelengine.fit.jane.task.util.Entities;
import modelengine.fit.jane.task.util.OperationContext;
import modelengine.fit.jober.common.util.ParamUtils;
import modelengine.fit.jober.common.utils.VersionUtils;
import modelengine.fit.jober.taskcenter.validation.NodeValidator;

import modelengine.fit.jober.common.ErrorCodes;
import modelengine.fit.jober.common.exceptions.BadRequestException;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Value;
import modelengine.fitframework.util.StringUtils;

import java.util.Set;

/**
 * 为 {@link NodeValidator} 提供实现。
 *
 * @author 梁济时
 * @since 2023-08-17
 */
@Component
public class NodeValidatorImpl implements NodeValidator {
    private final int nameLengthMaximum;

    public NodeValidatorImpl(@Value("${validation.node.name.length.maximum:64}") int nameLengthMaximum) {
        this.nameLengthMaximum = nameLengthMaximum;
    }

    @Override
    public String name(String name, Set<String> names, OperationContext context) {
        String realName = VersionUtils.getRealName(name);
        if (StringUtils.isEmpty(realName)) {
            throw new BadRequestException(ErrorCodes.TYPE_NAME_REQUIRED, ParamUtils.convertOperationContext(context));
        } else if (realName.length() > this.nameLengthMaximum) {
            throw new BadRequestException(ErrorCodes.TYPE_NAME_LENGTH_OUT_OF_BOUNDS,
                    ParamUtils.convertOperationContext(context));
        } else if (names.contains(realName)) {
            throw new BadRequestException(ErrorCodes.TYPE_NAME_ALREADY_EXISTS,
                    ParamUtils.convertOperationContext(context));
        } else {
            return name;
        }
    }

    @Override
    public String parentId(String parentId, OperationContext context) {
        if (StringUtils.isEmpty(parentId)) {
            return Entities.emptyId();
        } else {
            return Entities.validateId(parentId, () -> new BadRequestException(ErrorCodes.NODE_ID_INVALID,
                    ParamUtils.convertOperationContext(context)));
        }
    }
}
