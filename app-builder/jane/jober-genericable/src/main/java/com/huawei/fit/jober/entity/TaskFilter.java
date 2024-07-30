/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.entity;

import static com.huawei.fitframework.util.ObjectUtils.nullIf;

import java.util.Collections;
import java.util.List;

/**
 * 为任务提供过滤器。
 *
 * @author 陈镕希 c00572808
 * @since 2023-08-29
 */
public class TaskFilter {
    private List<String> ids;

    private List<String> names;

    private List<String> templateIds;

    private List<String> categories;

    private List<String> creators;

    private List<String> orderBys;

    public TaskFilter() {
        this(null, null, null, null, null, null);
    }

    public TaskFilter(List<String> ids, List<String> names, List<String> templateIds, List<String> categories,
            List<String> creators, List<String> orderBys) {
        this.ids = nullIf(ids, Collections.emptyList());
        this.names = nullIf(names, Collections.emptyList());
        this.templateIds = nullIf(templateIds, Collections.emptyList());
        this.categories = nullIf(categories, Collections.emptyList());
        this.creators = nullIf(creators, Collections.emptyList());
        this.orderBys = nullIf(orderBys, Collections.emptyList());
    }

    public List<String> getIds() {
        return ids;
    }

    public void setIds(List<String> ids) {
        this.ids = nullIf(ids, Collections.emptyList());
    }

    public List<String> getNames() {
        return names;
    }

    public void setNames(List<String> names) {
        this.names = nullIf(names, Collections.emptyList());
    }

    public List<String> getTemplateIds() {
        return templateIds;
    }

    public void setTemplateIds(List<String> templateIds) {
        this.templateIds = nullIf(templateIds, Collections.emptyList());
    }

    public List<String> getCategories() {
        return categories;
    }

    public void setCategories(List<String> categories) {
        this.categories = nullIf(categories, Collections.emptyList());
    }

    public List<String> getCreators() {
        return creators;
    }

    public void setCreators(List<String> creators) {
        this.creators = nullIf(creators, Collections.emptyList());
    }

    public List<String> getOrderBys() {
        return orderBys;
    }

    public void setOrderBys(List<String> orderBys) {
        this.orderBys = nullIf(orderBys, Collections.emptyList());
    }
}
