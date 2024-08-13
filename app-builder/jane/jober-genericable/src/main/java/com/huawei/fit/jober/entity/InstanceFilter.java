/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.entity;

import java.util.List;
import java.util.Map;

/**
 * 为任务实例提供过滤配置。
 *
 * @author 梁济时
 * @since 2023-09-11
 */
public class InstanceFilter {
    private List<String> users;

    private Map<String, String> options;

    /**
     * InstanceFilter
     */
    public InstanceFilter() {
    }

    public InstanceFilter(List<String> users, Map<String, String> options) {
        this.users = users;
        this.options = options;
    }

    public List<String> getUsers() {
        return users;
    }

    public void setUsers(List<String> users) {
        this.users = users;
    }

    public Map<String, String> getOptions() {
        return options;
    }

    public void setOptions(Map<String, String> options) {
        this.options = options;
    }
}
