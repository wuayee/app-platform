/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fit.jober.aipp.repository.impl;

import modelengine.fit.jober.aipp.domain.AippSystemConfig;
import modelengine.fit.jober.aipp.mapper.AippSystemConfigMapper;
import modelengine.fit.jober.aipp.repository.AippSystemConfigRepository;
import modelengine.fit.jober.aipp.serializer.impl.AippSystemConfigSerializer;

import modelengine.fitframework.annotation.Component;

import java.util.Optional;

/**
 * 应用创建仓库实现类
 *
 * @author 张越
 * @since 2024-11-30
 */
@Component
public class AippSystemConfigRepositoryImpl implements AippSystemConfigRepository {
    private final AippSystemConfigMapper aippSystemConfigMapper;
    private final AippSystemConfigSerializer serializer;

    public AippSystemConfigRepositoryImpl(AippSystemConfigMapper aippSystemConfigMapper) {
        this.aippSystemConfigMapper = aippSystemConfigMapper;
        this.serializer = new AippSystemConfigSerializer();
    }

    @Override
    public Optional<AippSystemConfig> find(String group, String key) {
        return Optional.ofNullable(this.serializer.deserialize(this.aippSystemConfigMapper.findOne(group, key)));
    }
}
