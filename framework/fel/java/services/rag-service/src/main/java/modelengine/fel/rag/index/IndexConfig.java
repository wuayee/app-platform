/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fel.rag.index;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Locale;

/**
 * 向量型索引服务的配置参数
 *
 * @since 2024-06-05
 */
@Builder
@Getter
@Setter
@AllArgsConstructor
public class IndexConfig {
    private String dbType;
    private String host;
    private Integer port;
    private String username;
    private String password;
    private String databaseName;
    private String embeddingModelName;
    private String embeddingApiKey;

    /**
     * 判断配置信息是否有效。
     *
     * @return 返回配置信息是否有效。
     */
    public boolean isInvalid() {
        return host == null || port == null || username == null || password == null || databaseName == null;
    }

    /**
     * 根据配置信息生成唯一标识，用于区分不同数据库连接。
     *
     * @return 返回唯一标识
     */
    public String generateId() {
        String combined = String.format(Locale.ROOT, "%s:%d:%s:%s", host, port, username, databaseName);
        if (dbType != null) {
            combined = dbType + ":" + combined;
        }
        return Integer.toHexString(combined.hashCode());
    }
}
