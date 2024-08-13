/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.service;

import com.huawei.fit.jane.task.util.OperationContext;
import com.huawei.fit.jober.taskcenter.declaration.OperationRecordDeclaration;
import com.huawei.fit.jober.taskcenter.domain.OperationRecordEntity;
import com.huawei.fit.jober.taskcenter.filter.OperationRecordFilter;
import com.huawei.fitframework.model.RangedResultSet;

/**
 * 为操作记录提供管理
 *
 * @author 姚江
 * @since 2023-11-17 10:40
 */
public interface OperationRecordService {
    /**
     * 操作记录表名
     */
    String TABLE_NAME = "operation_record";

    /**
     * 操作记录表列名：主键id
     */
    String TABLE_FIELD_ID = "id";

    /**
     * 操作记录表列名：被操作对象类型
     */
    String TABLE_FIELD_OBJECT_TYPE = "object_type";

    /**
     * 操作记录表列名：被操作对象Id
     */
    String TABLE_FIELD_OBJECT_ID = "object_id";

    /**
     * 操作记录表列名：操作人
     */
    String TABLE_FIELD_OPERATOR = "operator";

    /**
     * 操作记录表列名：操作时间
     */
    String TABLE_FIELD_OPERATED_TIME = "operated_time";

    /**
     * 操作记录表列名：信息
     */
    String TABLE_FIELD_MESSAGE = "message";

    /**
     * 操作记录表列名：操作
     */
    String TABLE_FIELD_OPERATE = "operate";

    /**
     * 存储操作记录
     *
     * @param declaration 操作记录的声明 {@link OperationRecordDeclaration}
     * @param context 操作上下文 {@link OperationContext}。
     * @return 操作记录实体 {@link OperationRecordEntity}。
     */
    OperationRecordEntity create(OperationRecordDeclaration declaration, OperationContext context);

    /**
     * 查询操作记录
     *
     * @param filter 查询过滤器 {@link OperationRecordFilter}。
     * @param offset 偏移量，64位整数。
     * @param limit 查询条数，32位整数。
     * @param context 操作上下文 {@link OperationContext}。
     * @return 查询结果集 {@link RangedResultSet}{@code <}{@link OperationRecordEntity}{@code >}。
     */
    RangedResultSet<OperationRecordEntity> list(OperationRecordFilter filter, long offset, int limit,
            OperationContext context);
}
