/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fit.jober.aipp.mapper;

import modelengine.fit.jober.aipp.po.AippSystemConfigPo;

import org.apache.ibatis.annotations.Param;

/**
 * 系统配置Mapper
 *
 * @author 张越
 * @since 2024-11-30
 */
public interface AippSystemConfigMapper {
    /**
     * 通过group和key查找配置.
     *
     * @param group 分组.
     * @param key 键值.
     * @return {@link AippSystemConfigPo} 对象.
     */
    AippSystemConfigPo findOne(@Param("configGroup") String group, @Param("configKey") String key);
}