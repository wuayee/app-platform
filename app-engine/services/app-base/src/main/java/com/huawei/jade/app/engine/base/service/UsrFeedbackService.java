/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.base.service;

import com.huawei.fitframework.annotation.Genericable;
import com.huawei.jade.app.engine.base.dto.UsrFeedbackDto;

import java.util.List;

/**
 * Aipp用户反馈功能接口
 *
 * @since 2024-5-24
 *
 */
public interface UsrFeedbackService {
    /**
     * 创建用户反馈信息
     *
     * @param usrFeedbackDto 用户反馈信息
     */
    @Genericable(id = "com.huawei.jade.app.engine.base.service.UsrFeedbackService.create")
    void create(UsrFeedbackDto usrFeedbackDto);

    /**
     * 更新用户反馈记录
     *
     * @param logId 日志Id
     * @param usrFeedbackDto 用户反馈信息
     */
    @Genericable(id = "com.huawei.jade.app.engine.base.service.UsrFeedbackService.update")
    void updateOne(Long logId, UsrFeedbackDto usrFeedbackDto);


    /**
     * 删除用户反馈记录
     *
     * @param logId 对话id
     */
    @Genericable(id = "com.huawei.jade.app.engine.base.service.UsrFeedbackService.delete")
    void deleteByLogId(long logId);

    /**
     * 获取所有用户反馈记录
     *
     * @return 用户反馈列表
     */
    @Genericable(id = "com.huawei.jade.app.engine.base.service.UsrFeedbackService.getAllUsrFeedbacks")
    List<UsrFeedbackDto> getAllUsrFeedbacks();

    /**
     * 通过logId查询用户反馈记录
     *
     * @param logId 日志id
     * @return 用户反馈信息
     */
    @Genericable(id = "com.huawei.jade.app.engine.base.service.UsrFeedbackService.getUsrFeedbackByLogId")
    UsrFeedbackDto getUsrFeedbackByLogId(Long logId);
}
