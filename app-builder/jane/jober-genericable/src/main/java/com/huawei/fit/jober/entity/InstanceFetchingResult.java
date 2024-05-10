/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.entity;

import java.util.List;

/**
 * 为数据获取提供结果。
 *
 * @author 梁济时 l00815032
 * @since 2023-09-11
 */
public class InstanceFetchingResult {
    private List<InstanceInfo> instances;

    private Boolean more;

    public InstanceFetchingResult() {
    }

    public InstanceFetchingResult(List<InstanceInfo> instances, Boolean more) {
        this.instances = instances;
        this.more = more;
    }

    public List<InstanceInfo> getInstances() {
        return instances;
    }

    public void setInstances(List<InstanceInfo> instances) {
        this.instances = instances;
    }

    public Boolean getMore() {
        return more;
    }

    public void setMore(Boolean more) {
        this.more = more;
    }
}
