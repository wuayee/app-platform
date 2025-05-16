/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.bff.service;

import modelengine.fit.jober.common.utils.UuidUtil;

import modelengine.fit.waterflow.flowsengine.persist.mapper.FitableUsageMapper;
import modelengine.fit.waterflow.spi.FlowableService;
import modelengine.fitframework.annotation.Alias;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Fitable;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * {@link FlowableService}的simpleFitable实现类
 *
 * @author 孙怡菲
 * @since 2023-12-16
 */
@Component
@Alias("simple-fitable")
public class SimpleFitable implements FlowableService {
    private final FitableUsageMapper fitableUsageMapper;

    public SimpleFitable(FitableUsageMapper fitableUsageMapper) {
        this.fitableUsageMapper = fitableUsageMapper;
    }

    @Override
    @Fitable(id = "3e460bc100a74f8ca7b94f6dce31a021")
    public List<Map<String, Object>> handleTask(List<Map<String, Object>> flowData) {
        this.fitableUsageMapper.save(UuidUtil.uuid(), Collections.singletonList("Saturday_Test"));
        return flowData;
    }
}
