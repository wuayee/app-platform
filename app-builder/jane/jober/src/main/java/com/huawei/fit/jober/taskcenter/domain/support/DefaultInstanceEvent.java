/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.domain.support;

import com.huawei.fit.jober.taskcenter.domain.InstanceEvent;
import com.huawei.fit.jober.taskcenter.domain.InstanceEventType;
import modelengine.fitframework.util.StringUtils;

import java.util.Arrays;
import java.util.Objects;

/**
 * 为 {@link InstanceEvent} 提供默认实现。
 *
 * @author 梁济时
 * @since 2023-10-09
 */
public class DefaultInstanceEvent implements InstanceEvent {
    private final InstanceEventType type;

    private final String fitableId;

    public DefaultInstanceEvent(InstanceEventType type, String fitableId) {
        this.type = type;
        this.fitableId = fitableId;
    }

    @Override
    public InstanceEventType type() {
        return this.type;
    }

    @Override
    public String fitableId() {
        return this.fitableId;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj instanceof DefaultInstanceEvent) {
            DefaultInstanceEvent another = (DefaultInstanceEvent) obj;
            return this.type == another.type && Objects.equals(this.fitableId, another.fitableId);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(new Object[] {this.getClass(), this.type, this.fitableId});
    }

    @Override
    public String toString() {
        return StringUtils.format("[type={0}, fitable={1}]", this.type(), this.fitableId());
    }

    /**
     * 实例事件构造器
     */
    public static class Builder implements InstanceEvent.Builder {
        private InstanceEventType type;

        private String fitableId;

        @Override
        public InstanceEvent.Builder type(InstanceEventType type) {
            this.type = type;
            return this;
        }

        @Override
        public InstanceEvent.Builder fitableId(String fitableId) {
            this.fitableId = fitableId;
            return this;
        }

        @Override
        public InstanceEvent build() {
            return new DefaultInstanceEvent(this.type, this.fitableId);
        }
    }
}
