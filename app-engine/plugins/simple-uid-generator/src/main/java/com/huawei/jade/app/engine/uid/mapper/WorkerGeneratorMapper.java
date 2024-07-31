/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.uid.mapper;

import com.huawei.jade.app.engine.uid.po.WorkerPo;

import org.apache.ibatis.annotations.Mapper;

/**
 * 表示版本生成器持久层接口。
 *
 * @author 何嘉斌
 * @since 2024-07-29
 */
@Mapper
public interface WorkerGeneratorMapper {
    /**
     * 分配机器号。
     *
     * @param workerPo 表示机器的 {@link WorkerPo}。
     */
    void getWorkerId(WorkerPo workerPo);
}
