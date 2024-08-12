/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2023. All rights reserved.
 */

package com.huawei.fit.client.support;

import static com.huawei.fitframework.inspection.Validation.greaterThanOrEquals;
import static com.huawei.fitframework.inspection.Validation.notBlank;

import com.huawei.fit.client.Address;

import java.util.Objects;

/**
 * 表示 {@link Address} 的默认实现。
 *
 * @author 季聿阶
 * @since 2022-09-19
 */
public class DefaultAddress implements Address {
    private final String host;
    private final int port;

    public DefaultAddress(String host, int port) {
        this.host = notBlank(host, "The host cannot be blank.");
        this.port = greaterThanOrEquals(port, 0, "The port cannot be negative. [port={0}]", port);
    }

    @Override
    public String host() {
        return this.host;
    }

    @Override
    public int port() {
        return this.port;
    }

    @Override
    public String toString() {
        return "{\"host\": \"" + this.host + "\", \"port\": " + this.port + '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || this.getClass() != obj.getClass()) {
            return false;
        }
        DefaultAddress that = (DefaultAddress) obj;
        return this.port == that.port && Objects.equals(this.host, that.host);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.host, this.port);
    }
}
