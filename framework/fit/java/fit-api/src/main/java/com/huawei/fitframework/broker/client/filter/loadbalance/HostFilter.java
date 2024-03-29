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
 * 指定主机地址的负载均衡策略。
 *
 * @author 张浩亮 z00352201
 * @author 季聿阶 j00559309
 * @since 2021-09-26
 */
public class HostFilter extends ChampionFilter {
    private final String host;

    public HostFilter(String host) {
        this.host = notBlank(host, "The target host to filter cannot be blank.");
    }

    @Override
    protected Optional<Target> select(FitableMetadata fitable, String localWorkerId, List<Target> toFilterTargets) {
        return toFilterTargets.stream().filter(target -> StringUtils.equals(target.host(), this.host)).findFirst();
    }
}
