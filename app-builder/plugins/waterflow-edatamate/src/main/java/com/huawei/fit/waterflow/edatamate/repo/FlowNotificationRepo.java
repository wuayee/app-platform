/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.waterflow.edatamate.repo;

import com.huawei.fit.waterflow.edatamate.dao.po.FlowNotificationPo;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 流程回调repo
 *
 * @author yangxiangyu
 * @since 2024/5/27
 */
public interface FlowNotificationRepo {
    /**
     * 创建回调对象
     *
     * @param fitableId 回调对象id
     * @param data 回调参数
     */
    void create(String fitableId, Map<String, Object> data);

    /**
     * 查询通知列表
     *
     * @return 重试对象列表
     */
    List<FlowNotificationPo> findAll();

    /**
     * 删除通知对象
     *
     * @param id 重试id
     */
    void delete(String id);

    /**
     * 查询下次可通知数据
     *
     * @param nextRetryTime 下次重试时间
     * @return 重试列表
     */
    List<FlowNotificationPo> findNextRetryList(LocalDateTime nextRetryTime);

    /**
     * 更新通知对象信息
     *
     * @param flowNotificationPO 通知对象信息
     */
    void update(FlowNotificationPo flowNotificationPO);
}
