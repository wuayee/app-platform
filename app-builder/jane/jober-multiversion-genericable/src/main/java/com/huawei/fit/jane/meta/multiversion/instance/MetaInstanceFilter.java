/*
 *  Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jane.meta.multiversion.instance;

import static com.huawei.fitframework.util.ObjectUtils.nullIf;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Meta实例MetaInstanceFilter。
 *
 * @author 孙怡菲
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

    public MetaInstanceFilter(List<String> ids, List<String> tags,
        Map<String, List<String>> infos, List<String> orderBy) {
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

    /**
     * putInfo
     *
     * @param key key
     * @param value value
     */
    public void putInfo(String key, List<String> value) {
        if (this.infos == null || this.infos.isEmpty()) {
            this.infos = new HashMap<String, List<String>>() {{
                put(key, value);
            }};
            return;
        }
        this.infos.put(key, value);
    }

    public List<String> getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(List<String> orderBy) {
        this.orderBy = nullIf(orderBy, Collections.emptyList());
    }
}
