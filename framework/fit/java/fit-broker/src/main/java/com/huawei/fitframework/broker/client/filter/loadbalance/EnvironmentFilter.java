/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2024. All rights reserved.
 */

package com.huawei.fitframework.broker.client.filter.loadbalance;

import static com.huawei.fitframework.inspection.Validation.notBlank;

import com.huawei.fitframework.broker.FitableMetadata;
import com.huawei.fitframework.broker.Target;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 指定环境标的负载均衡策略。
 *
 * @author 季聿阶 j00559309
 * @since 2022-06-06
 */
public class EnvironmentFilter extends AbstractFilter {
    private final String environment;

    public EnvironmentFilter(String environment) {
        this.environment = notBlank(environment, "The target environment to filter cannot be blank.");
    }

    @Override
    protected List<Target> loadbalance(FitableMetadata fitable, String localWorkerId, List<Target> toFilterTargets) {
        return toFilterTargets.stream()
                .filter(target -> Objects.equals(target.environment(), this.environment))
                .collect(Collectors.toList());
    }
}
