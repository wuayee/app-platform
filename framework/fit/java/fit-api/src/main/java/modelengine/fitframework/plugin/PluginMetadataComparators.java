/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package modelengine.fitframework.plugin;

import java.util.Comparator;

/**
 * 为 {@link PluginMetadata} 提供基于启动顺序的排序程序。
 *
 * @author 梁济时
 * @since 2023-01-29
 */
public final class PluginMetadataComparators {
    /**
     * 表示插件元数据的按启动顺序的比较程序。
     */
    public static final Comparator<PluginMetadata> STARTUP =
            Comparator.<PluginMetadata, Integer>comparing(metadata -> metadata.category().getId())
                    .thenComparingInt(PluginMetadata::level);
}
