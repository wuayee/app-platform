/*
 *  Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.bff.service;

import com.huawei.fit.jober.FlowableService;
import com.huawei.fit.jober.common.utils.UuidUtil;
import com.huawei.fit.waterflow.flowsengine.persist.mapper.FitableUsageMapper;
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
