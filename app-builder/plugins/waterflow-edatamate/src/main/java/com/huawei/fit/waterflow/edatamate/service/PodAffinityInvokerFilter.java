/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.waterflow.edatamate.service;

import static com.huawei.fitframework.util.ObjectUtils.cast;

import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.broker.FitableMetadata;
import com.huawei.fitframework.broker.Target;
import com.huawei.fitframework.broker.client.Invoker.Filter;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * pod亲和调度的策略
 * 这里约定workerId以pod name为前缀，基于workerId和任务id做亲和关联。算子实例发生变化时不要求和保证必须在同一个pod上
 *
 * @author songyongtan
 * @since 2024/5/23
 */
@Component("a3000PodAffinityFilter")
public class PodAffinityInvokerFilter implements Filter {
    @Override
    public List<Target> filter(FitableMetadata fitable, String localWorkerId, List<Target> toFilterTargets,
                               Map<String, Object> extensions) {
        String taskInstanceId = cast(Optional.ofNullable(extensions.get("taskInstanceId")).orElse(""));
        if (toFilterTargets.isEmpty()) {
            return toFilterTargets;
        }

        List<Target> sortedTargets = toFilterTargets.stream()
                .sorted(Comparator.comparing(Target::workerId))
                .collect(Collectors.toList());

        return Collections.singletonList(sortedTargets.get(Math.abs(taskInstanceId.hashCode()) % sortedTargets.size()));
    }
}
