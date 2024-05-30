/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.base.service.impl;

import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.annotation.Fitable;
import com.huawei.fitframework.log.Logger;
import com.huawei.jade.app.engine.base.dto.UsrFeedbackDto;
import com.huawei.jade.app.engine.base.mapper.UsrFeedbackMapper;
import com.huawei.jade.app.engine.base.service.UsrFeedbackService;

import java.util.List;

/**
 * Aipp用户反馈功能实现类
 *
 * @since 2024-5-24
 *
 */
@Component
public class UsrFeedbackServiceImpl implements UsrFeedbackService {
    private static final Logger log = Logger.get(UsrFeedbackServiceImpl.class);

    private final UsrFeedbackMapper usrFeedbackMapper;

    public UsrFeedbackServiceImpl(UsrFeedbackMapper userFeedbackMapper) {
        this.usrFeedbackMapper = userFeedbackMapper;
    }

    @Override
    @Fitable(id = "UsrFeedbackCreate")
    public void create(UsrFeedbackDto usrFeedbackDto) {
        usrFeedbackMapper.insert(usrFeedbackDto);
    }

    @Override
    @Fitable(id = "UsrFeedbackUpdate")
    public void updateOne(String instanceId, UsrFeedbackDto usrFeedbackDto) {
        usrFeedbackMapper.updateOne(instanceId, usrFeedbackDto.getUsrFeedback(), usrFeedbackDto.getUsrFeedbackText());
    }

    @Override
    @Fitable(id = "UsrFeedbackDelete")
    public void deleteByLogId(String instanceId) {
        usrFeedbackMapper.deleteByLogId(instanceId);
    }

    @Override
    @Fitable(id = "UsrGetAllUsrFeedbacks")
    public List<UsrFeedbackDto> getAllUsrFeedbacks() {
        return usrFeedbackMapper.getAllUsrFeedbacks();
    }

    @Override
    @Fitable(id = "getUsrFeedbackByInstanceId")
    public UsrFeedbackDto getUsrFeedbackByInstanceId(String instanceId) {
        return usrFeedbackMapper.getUsrFeedbackByInstanceId(instanceId);
    }
}
