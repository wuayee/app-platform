/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jane.meta.definition;

import static modelengine.fitframework.util.ObjectUtils.nullIf;

import java.util.Collections;
import java.util.List;

/**
 * 查询MetaTemplate所用Filter。
 *
 * @author 陈镕希
 * @since 2024-02-04
 */
public class MetaTemplateFilter {
    private List<String> ids;

    private List<String> names;

    public MetaTemplateFilter() {
        this(null, null);
    }

    public MetaTemplateFilter(List<String> ids, List<String> names) {
        this.ids = nullIf(ids, Collections.emptyList());
        this.names = nullIf(names, Collections.emptyList());
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
}
