/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fel.rag.store.connector;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

/**
 * 数据库连接配置。
 *
 * @since 2024-05-07
 */
@Getter
@Setter
@Builder
public class ConnectorProperties {
    private String host;
    private int port;
    private String username;
    private String password;

    /**
     * 根据连接参数构建 {@link ConnectorProperties} 的实例。
     *
     * @param host host
     * @param port port
     * @param username username
     * @param password password
     */
    public ConnectorProperties(@NonNull String host, int port, @NonNull String username, @NonNull String password) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
    }
}
