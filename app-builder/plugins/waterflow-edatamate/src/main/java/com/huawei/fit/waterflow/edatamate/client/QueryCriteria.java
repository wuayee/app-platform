/*
 *  Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.waterflow.edatamate.client;

import lombok.NoArgsConstructor;

/**
 * 流程查询参数
 *
 * @author y00679285
 * @since 2023/10/24
 */
@NoArgsConstructor
public class QueryCriteria {
    private String tag;
    private String createUser;
    private String offset;
    private String limit;

    public void setTag(final String tag) {
        this.tag = tag;
    }

    public void setCreateUser(final String createUser) {
        this.createUser = createUser;
    }

    public void setOffset(final String offset) {
        this.offset = offset;
    }

    public void setLimit(final String limit) {
        this.limit = limit;
    }

    public String getTag() {
        return this.tag;
    }

    public String getCreateUser() {
        return this.createUser;
    }

    public String getOffset() {
        return this.offset;
    }

    public String getLimit() {
        return this.limit;
    }
}
