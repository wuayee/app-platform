/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.base.service.impl;

import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.annotation.Fitable;
import com.huawei.fitframework.log.Logger;
import com.huawei.jade.app.engine.base.dto.UsrFeedbackDto;
import com.huawei.jade.app.engine.base.mapper.UserFeedbackMapper;
import com.huawei.jade.app.engine.base.po.UsrFeedbackPo;
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

    private final UserFeedbackMapper usrFeedbackMapper;

    public UsrFeedbackServiceImpl(UserFeedbackMapper userFeedbackMapper) {
        this.usrFeedbackMapper = userFeedbackMapper;
    }

    @Override
    @Fitable(id = "UsrFeedbackCreate")
    public void create(UsrFeedbackDto usrFeedbackDto) {
        usrFeedbackMapper.insert(usrFeedbackDto);
    }

    @Override
    @Fitable(id = "UsrFeedbackUpdate")
    public void updateOne(Long logId, UsrFeedbackDto usrFeedbackDto) {
        usrFeedbackMapper.updateOne(logId, usrFeedbackDto.getUsrFeedback(), usrFeedbackDto.getUsrFeedbackText());
    }

    @Override
    @Fitable(id = "UsrFeedbackDelete")
    public void deleteByLogId(long logId) {
        usrFeedbackMapper.deleteByLogId(logId);
    }

    @Override
    @Fitable(id = "UsrGetAllUsrFeedbacks")
    public List<UsrFeedbackPo> getAllUsrFeedbacks() {
        return usrFeedbackMapper.getAllUsrFeedbacks();
    }

    @Override
    @Fitable(id = "UsrGetUsrFeedbackByLogId")
    public UsrFeedbackPo getUsrFeedbackByLogId(Long logId) {
        return usrFeedbackMapper.getUsrFeedbackByLogId(logId);
    }
}
