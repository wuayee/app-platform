/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.rag.store.connector.schema;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 关系型数据库列信息
 *
 * @since 2024-05-22
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RdbColumn {
    /** 列名 */
    private String name;

    /** 数据类型 */
    private DbFieldType type;

    /** 描述 */
    private String desc;

    /** 是否为索引列 */
    private boolean isIndex;

    /**
     * 转换为关系型数据库创表语句
     * <p>
     * 用户填写的表名均会加上 user_ 前缀
     * </p>
     *
     * @return 创表sql语句
     */
    public String toSqlString() {
        StringBuilder sb = new StringBuilder();
        sb.append("user_").append(this.name);
        switch (this.type) {
            case VARCHAR:
                sb.append(" VARCHAR");
                break;
            case NUMBER:
                sb.append(" BIGINT");
        }
        return sb.toString();
    }
}
