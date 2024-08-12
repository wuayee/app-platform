/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fitframework.broker.support;

import com.huawei.fitframework.broker.Aliases;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 表示可修改的 {@link Aliases}。
 *
 * @author 季聿阶
 * @since 2023-03-27
 */
public class ConfigurableAliases implements Aliases {
    private final Set<String> aliases = new HashSet<>();

    @Override
    public Set<String> all() {
        return Collections.unmodifiableSet(this.aliases);
    }

    @Override
    public boolean contains(String alias) {
        return this.aliases.contains(alias);
    }

    /**
     * 设置别名的集合。
     *
     * @param aliases 表示待设置的别名集合的 {@link Set}{@code <}{@link String}{@code >}。
     */
    public void set(Set<String> aliases) {
        this.aliases.clear();
        this.aliases.addAll(aliases);
    }

    /**
     * 添加一个别名。
     *
     * @param alias 表示待添加的别名的 {@link String}。
     */
    public void append(String alias) {
        this.aliases.add(alias);
    }

    /**
     * 删除一个别名。
     *
     * @param alias 表示待删除的别名的 {@link String}。
     */
    public void remove(String alias) {
        this.aliases.remove(alias);
    }

    /**
     * 清除所有别名。
     */
    public void clear() {
        this.aliases.clear();
    }

    @Override
    public String toString() {
        return this.aliases.stream().map(alias -> "\"" + alias + "\"").collect(Collectors.joining(", ", "[", "]"));
    }

    @Override
    public boolean equals(Object another) {
        if (this == another) {
            return true;
        }
        if (another == null || this.getClass() != another.getClass()) {
            return false;
        }
        ConfigurableAliases that = (ConfigurableAliases) another;
        return Objects.equals(this.aliases, that.aliases);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.aliases);
    }
}
