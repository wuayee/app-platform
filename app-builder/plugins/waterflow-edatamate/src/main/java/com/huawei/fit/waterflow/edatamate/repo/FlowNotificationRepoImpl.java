/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.waterflow.edatamate.repo;

import com.huawei.fit.waterflow.common.utils.UUIDUtil;
import com.huawei.fit.waterflow.edatamate.dao.FlowNotificationMapper;
import com.huawei.fit.waterflow.edatamate.dao.po.FlowNotificationPo;
import com.huawei.fitframework.annotation.Component;

import com.alibaba.fastjson.JSON;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * TaskUpdateRetryRepo实现类
 *
 * @author yangxiangyu
 * @since 2024/5/27
 */
@Component
public class FlowNotificationRepoImpl implements FlowNotificationRepo {
    private final FlowNotificationMapper notifyMapper;

    public FlowNotificationRepoImpl(FlowNotificationMapper notifyMapper) {
        this.notifyMapper = notifyMapper;
    }

    @Override
    public void create(String fitableId, Map<String, Object> data) {
        String id = UUIDUtil.uuid();
        LocalDateTime now = LocalDateTime.now();
        notifyMapper.create(id, fitableId, JSON.toJSONString(data), now);
    }

    @Override
    public List<FlowNotificationPo> findAll() {
        return notifyMapper.findAll();
    }

    @Override
    public void delete(String id) {
        notifyMapper.delete(id);
    }

    @Override
    public List<FlowNotificationPo> findNextRetryList(LocalDateTime nextRetryTime) {
        return notifyMapper.findNextNotifyList(nextRetryTime);
    }

    @Override
    public void update(FlowNotificationPo retryPO) {
        notifyMapper.update(retryPO.getId(), retryPO.getNotifyCount(), retryPO.getNextNotifyTime());
    }
}
