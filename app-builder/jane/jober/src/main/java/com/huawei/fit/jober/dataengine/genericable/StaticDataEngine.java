/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.dataengine.genericable;

import com.huawei.fit.jober.dataengine.rest.request.StaticMetaDataTaskDto;
import com.huawei.fitframework.annotation.Genericable;

/**
 * 静态数据引擎操作类
 *
 * @author 00693950
 * @since 2023/6/12
 */
public interface StaticDataEngine {
    /**
     * 获取第三方平台数据接口
     *
     * @param staticMetaDataTaskDTO {@link StaticMetaDataTaskDto}
     */
    @Genericable(id = "d7495cf0160c499db83b9adf6c2cbd9c")
    void create(StaticMetaDataTaskDto staticMetaDataTaskDTO);

    /**
     * 更新拉取数据接口
     *
     * @param staticMetaDataTaskDTO {@link StaticMetaDataTaskDto}
     */
    @Genericable(id = "4c993fa977140159f7dc798abad704a")
    void update(StaticMetaDataTaskDto staticMetaDataTaskDTO);

    /**
     * 删除拉取数据接口
     *
     * @param taskSourceId 任务数据源定义唯一标识
     */
    @Genericable(id = "3965fea4d8d4409db9b2ab32ce9e3001")
    void delete(String taskSourceId);
}
