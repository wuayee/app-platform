/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.waterflow.edatamate.dao;

import com.huawei.fit.waterflow.edatamate.dao.po.FlowNotificationPo;

import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDateTime;
import java.util.List;

/**
 * TaskUpdateRetry对应Mapper类
 *
 * @author yangxiangyu
 * @since 2024/5/27
 */
@Mapper
public interface FlowNotificationMapper {
    /**
     * 创建任务回调通知对象
     *
     * @param id id
     * @param fitableId 调用对象
     * @param data 调用对象参数
     * @param time 下次被通知时间
     */
    void create(String id, String fitableId, String data, LocalDateTime time);

    /**
     * 查找所有通知对象
     *
     * @return 重试列表
     */
    List<FlowNotificationPo> findAll();

    /**
     * 删除通知对象
     *
     * @param id 重试id标识
     */
    void delete(String id);

    /**
     * 查询可通知列表
     *
     * @param time 可重试时间
     * @return 重试列表
     */
    List<FlowNotificationPo> findNextNotifyList(LocalDateTime time);

    /**
     * 更新通知信息
     *
     * @param notifyId id标识
     * @param notifyCount 通知次数
     * @param nextNotifyTime 下次通知时间
     */
    void update(String notifyId, int notifyCount, LocalDateTime nextNotifyTime);
}
