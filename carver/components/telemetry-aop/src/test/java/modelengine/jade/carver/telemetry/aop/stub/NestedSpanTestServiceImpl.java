/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.carver.telemetry.aop.stub;

import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.log.Logger;

import modelengine.jade.service.annotations.CarverSpan;
import modelengine.jade.service.annotations.SpanAttr;

/**
 * {@link NestedSpanTestService} 的默认实现。
 *
 * @author 刘信宏
 * @since 2024-07-29
 */
@Component
public class NestedSpanTestServiceImpl implements NestedSpanTestService {
    private static final Logger log = Logger.get(NestedSpanTestServiceImpl.class);

    @CarverSpan(NestedSpanTestService.NESTED_SPAN_NAME)
    @Override
    public void invoke(@SpanAttr(NestedSpanTestService.NESTED_ATTR_KEY) String arg) {
        log.debug("Nested argument: {}", arg);
    }
}
