/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fit.jober.aipp.serializer.impl;

import modelengine.fit.jober.aipp.domain.AippSystemConfig;
import modelengine.fit.jober.aipp.po.AippSystemConfigPo;
import modelengine.fit.jober.aipp.serializer.BaseSerializer;

/**
 * 系统配置序列化与反序列化实现类
 *
 * @author 张越
 * @since 2024-11-30
 */
public class AippSystemConfigSerializer implements BaseSerializer<AippSystemConfig, AippSystemConfigPo> {
    @Override
    public AippSystemConfigPo serialize(AippSystemConfig aippSystemConfig) {
        if (aippSystemConfig == null) {
            return null;
        }
        return AippSystemConfigPo.builder()
                .id(aippSystemConfig.getId())
                .configKey(aippSystemConfig.getConfigKey())
                .configValue(aippSystemConfig.getConfigValue())
                .configGroup(aippSystemConfig.getConfigGroup())
                .configParent(aippSystemConfig.getConfigParent())
                .build();
    }

    @Override
    public AippSystemConfig deserialize(AippSystemConfigPo aippSystemConfigPo) {
        if (aippSystemConfigPo == null) {
            return null;
        }
        return AippSystemConfig.builder()
                .id(aippSystemConfigPo.getId())
                .configKey(aippSystemConfigPo.getConfigKey())
                .configValue(aippSystemConfigPo.getConfigValue())
                .configGroup(aippSystemConfigPo.getConfigGroup())
                .configParent(aippSystemConfigPo.getConfigParent())
                .build();
    }
}
