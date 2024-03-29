/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2024. All rights reserved.
 */

package com.huawei.fitframework.broker.client.filter.loadbalance;

import static com.huawei.fitframework.inspection.Validation.notNull;

import com.huawei.fitframework.broker.FitableMetadata;
import com.huawei.fitframework.broker.Target;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 每一个不同的 Fit 进程（Worker）都调用 1 次的负载均衡策略。
 *
 * @author 季聿阶 j00559309
 * @since 2021-06-11
 */
public class WorkerFairFilter extends AbstractFilter {
    private final ChampionFilter championFilter;

    public WorkerFairFilter(ChampionFilter championFilter) {
        this.championFilter = notNull(championFilter, "No championFilter.");
    }

    @Override
    protected List<Target> loadbalance(FitableMetadata fitable, String localWorkerId, List<Target> toFilterTargets) {
        return toFilterTargets.stream()
                .collect(Collectors.groupingBy(Target::workerId))
                .values()
                .stream()
                .map(workerTargets -> this.championFilter.select(fitable, localWorkerId, workerTargets))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }
}
