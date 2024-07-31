/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.entity;

/**
 * 分页查询参数
 *
 * @author 陈镕希 c00572808
 * @since 2023-06-13
 */
public class Page {
    /**
     * 请求获取的页数
     */
    private Integer pageNo;

    /**
     * 每页的大小
     */
    private Integer pageSize;

    /**
     * Page
     */
    public Page() {
    }

    public Page(Integer pageNo, Integer pageSize) {
        this.pageNo = pageNo;
        this.pageSize = pageSize;
    }

    public Integer getPageNo() {
        return pageNo;
    }

    public void setPageNo(Integer pageNo) {
        this.pageNo = pageNo;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }
}
