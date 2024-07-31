/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.uid;

import com.huawei.fitframework.annotation.Component;
import com.huawei.jade.app.engine.uid.mapper.WorkerGeneratorMapper;
import com.huawei.jade.app.engine.uid.po.WorkerPo;

/**
 * 表示机器 ID 获取器接口。
 *
 * @author 何嘉斌
 * @since 2024-07-29
 */
@Component
public class WorkerGenerator {
    private final WorkerGeneratorMapper workerGeneratorMapper;

    public WorkerGenerator(WorkerGeneratorMapper workerGeneratorMapper) {
        this.workerGeneratorMapper = workerGeneratorMapper;
    }

    /**
     * 获取机器 ID。
     *
     * @return 返回获取到的机器 ID 的{@code int}。
     */
    public int getWorkerId() {
        WorkerPo workerPo = new WorkerPo();
        this.workerGeneratorMapper.getWorkerId(workerPo);
        return workerPo.getWorkerId();
    }
}
