/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.task.mapper;

import com.huawei.jade.app.engine.task.po.EvalRecordPo;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 表示评估任务用用例果的持久层接口。
 *
 * @author 何嘉斌
 * @since 2024-08-13
 */
@Mapper
public interface EvalRecordMapper {
    /**
     * 创建评估任务用例结果。
     *
     * @param resultPo 表示评估任务用例结果信息的 {@link List}{@code <}{@link EvalRecordPo}{@code >}。
     */
    void create(List<EvalRecordPo> resultPo);
}
