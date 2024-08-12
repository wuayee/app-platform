/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.waterflow.edatamate.service;

import com.huawei.fit.jober.FlowableService;
import com.huawei.fit.jober.common.TypeNotSupportException;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.annotation.Fitable;

import java.util.List;
import java.util.Map;

/**
 * 抛类型不支持异常的实现
 *
 * @author 杨祥宇
 * @since 2024/2/1
 */
@Component
public class InvokeErrorImpl implements FlowableService {
    @Override
    @Fitable(id = "5abc15280ea44cada216e094e3a76937")
    public List<Map<String, Object>> handleTask(List<Map<String, Object>> flowData) {
        throw new TypeNotSupportException("Processing of this type is not supported");
    }
}
