/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.eval.mapper;

import com.huawei.jade.app.engine.eval.po.EvalTaskPo;
import com.huawei.jade.app.engine.eval.query.EvalTaskListQuery;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.session.RowBounds;

import java.util.List;

/**
 * 评估任务相关的db接口。
 *
 * @author 董春寅
 * @since 2024-05-28
 */
@Mapper
public interface EvalTaskMapper {
    /**
     * 插入一条评估任务。
     *
     * @param evalTaskPo 表示评估任务的实体类的 {@link EvalTaskPo}。
     */
    void insert(EvalTaskPo evalTaskPo);

    /**
     * 通过id获取评估任务。
     *
     * @param id 表示任务id的 {@link Long}。
     * @return 表示评估任务的 {@link EvalTaskPo}。
     */
    EvalTaskPo getById(long id);

    /**
     * 通过条件查询任务列表。
     *
     * @param evalTaskListQuery 表示需要筛选的条件的 {@link EvalTaskListQuery}。
     * @param rowBounds 表示分页设置的 {@link RowBounds}。
     * @return 表示任务列表的 {@link List}{@code <}{@link EvalTaskPo}{@code >}。
     */
    List<EvalTaskPo> getByConditions(EvalTaskListQuery evalTaskListQuery, RowBounds rowBounds);

    /**
     * 通过条件查询任务总数。
     *
     * @param evalTaskListQuery 表示要筛选的条件的 {@link EvalTaskListQuery}。
     * @return 表示总数的 {@link Long}。
     */
    long getCountByConditions(EvalTaskListQuery evalTaskListQuery);

    /**
     * 通过id设置任务结束信息。
     *
     * @param evalTaskPo 表示包含任务结束信息的任务实体类的 {@link EvalTaskPo}。
     */
    void setFinishById(EvalTaskPo evalTaskPo);

    /**
     * 通过id设置任务启动信息。
     *
     * @param evalTaskPo 表示包含任务启动信息的任务实体类的 {@link EvalTaskPo}。
     */
    void setStartById(EvalTaskPo evalTaskPo);
}
