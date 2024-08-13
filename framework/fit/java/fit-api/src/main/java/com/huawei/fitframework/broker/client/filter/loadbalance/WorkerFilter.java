/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2024. All rights reserved.
 */

package com.huawei.fitframework.broker.client.filter.loadbalance;

import static com.huawei.fitframework.inspection.Validation.notBlank;

import com.huawei.fitframework.broker.FitableMetadata;
import com.huawei.fitframework.broker.Target;
import com.huawei.fitframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

/**
 * 指定进程唯一标识的负载均衡策略。
 *
 * @author 季聿阶
 * @since 2021-08-26
 */
public class WorkerFilter extends ChampionFilter {
    private final String workerId;

    public WorkerFilter(String workerId) {
        this.workerId = notBlank(workerId, "The target worker id to filter cannot be blank.");
    }

    @Override
    protected Optional<Target> select(FitableMetadata fitable, String localWorkerId, List<Target> toFilterTargets) {
        return toFilterTargets.stream()
                .filter(target -> StringUtils.equals(target.workerId(), this.workerId))
                .findFirst();
    }
}
