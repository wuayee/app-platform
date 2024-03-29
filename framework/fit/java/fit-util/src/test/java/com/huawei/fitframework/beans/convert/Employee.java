/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fitframework.beans.convert;

/**
 * 表示测试的类型。
 *
 * @author 季聿阶 j00559309
 * @since 2024-02-25
 */
public class Employee {
    private long id;
    private String name;
    private boolean online;
    private Department department;

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isOnline() {
        return this.online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public Department getDepartment() {
        return this.department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }
}
