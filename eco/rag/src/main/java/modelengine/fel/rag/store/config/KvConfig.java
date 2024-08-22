/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fel.rag.store.config;

import lombok.Getter;
import lombok.Setter;

/**
 * 键值数据库配置信息。
 *
 * @since 2024-05-07
 */
@Getter
@Setter
public class KvConfig {
    private String namespace;

    /**
     * 根据传入的namespace初始化 {@link KvConfig} 的实例。
     *
     * @param namespace namespace
     */
    public KvConfig(String namespace) {
        this.namespace = namespace;
    }
}
