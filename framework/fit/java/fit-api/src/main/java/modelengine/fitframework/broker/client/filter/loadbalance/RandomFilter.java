/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2024. All rights reserved.
 */

package modelengine.fitframework.broker.client.filter.loadbalance;

import modelengine.fitframework.broker.FitableMetadata;
import modelengine.fitframework.broker.Target;

import java.security.SecureRandom;
import java.util.List;
import java.util.Optional;
import java.util.Random;

/**
 * 随机的负载均衡策略。
 *
 * @author 季聿阶
 * @since 2021-06-11
 */
public class RandomFilter extends ChampionFilter {
    private final Random random = new SecureRandom();

    @Override
    protected Optional<Target> select(FitableMetadata fitable, String localWorkerId, List<Target> toFilterTargets) {
        return Optional.of(toFilterTargets.get(this.random.nextInt(toFilterTargets.size())));
    }
}
