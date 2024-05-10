/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.entity;

import java.util.List;
import java.util.Objects;

/**
 * 表示任务实例变更消息。
 *
 * @author 梁济时 l00815032
 * @since 2023-08-28
 */
public class InstanceChanged extends InstanceMessage {
    private List<ChangedPropertyValue> changes;

    public InstanceChanged() {
    }

    public InstanceChanged(List<ChangedPropertyValue> changes) {
        this.changes = changes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        InstanceChanged that = (InstanceChanged) o;
        return Objects.equals(changes, that.changes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), changes);
    }

    public List<ChangedPropertyValue> getChanges() {
        return changes;
    }

    public void setChanges(List<ChangedPropertyValue> changes) {
        this.changes = changes;
    }

    /**
     * ChangedPropertyValue
     */
    public static class ChangedPropertyValue extends PropertyValue {
        private String originValue;

        public ChangedPropertyValue() {
        }

        public ChangedPropertyValue(String originValue) {
            this.originValue = originValue;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            if (!super.equals(o)) {
                return false;
            }
            ChangedPropertyValue that = (ChangedPropertyValue) o;
            return Objects.equals(originValue, that.originValue);
        }

        @Override
        public int hashCode() {
            return Objects.hash(super.hashCode(), originValue);
        }

        public String getOriginValue() {
            return originValue;
        }

        public void setOriginValue(String originValue) {
            this.originValue = originValue;
        }
    }
}
