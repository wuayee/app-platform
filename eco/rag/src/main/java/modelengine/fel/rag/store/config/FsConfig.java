/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fel.rag.store.config;

import lombok.Getter;
import lombok.Setter;

/**
 * 文件系统配置信息。
 *
 * @since 2024-05-07
 */
@Setter
@Getter
public class FsConfig {
    private String path;
}
