/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.carver.telemetry.aop.stub;

import modelengine.jade.service.annotations.AppendSpanAttr;
import modelengine.jade.service.annotations.SpanAttr;

import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.log.Logger;

import java.util.Objects;

/**
 * 嵌套操作测试服务，用于测试 @AppendSpanAttr 注解。
 *
 * @author 马朝阳
 * @since 2024-08-06
 */
@Component
public class NestedAppendSpanTestImpl implements NestedSpanTestService {
    private static final Logger log = Logger.get(NestedAppendSpanTestImpl.class);

    /**
     * 嵌套正常和异常操作。
     *
     * @param arg 表示嵌套操作参数的 {@link String}。
     */
    @AppendSpanAttr
    @Override
    public void invoke(@SpanAttr(NestedSpanTestService.NESTED_ATTR_KEY) String arg) {
        if (Objects.equals(arg, "exception")) {
            throw new IllegalStateException("exception message.");
        }
        log.debug("Nested argument: {}", arg);
    }
}
