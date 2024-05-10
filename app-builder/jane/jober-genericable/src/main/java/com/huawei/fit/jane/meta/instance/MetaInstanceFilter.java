/*
 *  Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jane.meta.instance;

import static com.huawei.fitframework.util.ObjectUtils.nullIf;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Meta实例MetaInstanceFilter。
 *
 * @author 孙怡菲 s00664640
 * @since 2023-12-12
 */
public class MetaInstanceFilter {
    private List<String> ids;

    private List<String> tags;

    private Map<String, List<String>> infos;

    private List<String> orderBy;

    public MetaInstanceFilter() {
        this(null, null, null, null);
    }

    public MetaInstanceFilter(List<String> ids, List<String> tags, Map<String, List<String>> infos,
            List<String> orderBy) {
        this.ids = nullIf(ids, Collections.emptyList());
        this.tags = nullIf(tags, Collections.emptyList());
        this.infos = nullIf(infos, Collections.emptyMap());
        this.orderBy = nullIf(orderBy, Collections.emptyList());
    }

    public List<String> getIds() {
        return ids;
    }

    public void setIds(List<String> ids) {
        this.ids = nullIf(ids, Collections.emptyList());
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = nullIf(tags, Collections.emptyList());
    }

    public Map<String, List<String>> getInfos() {
        return infos;
    }

    public void setInfos(Map<String, List<String>> infos) {
        this.infos = nullIf(infos, Collections.emptyMap());
    }

    public List<String> getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(List<String> orderBy) {
        this.orderBy = nullIf(orderBy, Collections.emptyList());
    }
}
