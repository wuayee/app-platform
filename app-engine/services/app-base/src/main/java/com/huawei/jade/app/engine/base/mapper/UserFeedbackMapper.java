/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.base.mapper;

import com.huawei.jade.app.engine.base.dto.UsrFeedbackDto;
import com.huawei.jade.app.engine.base.po.UsrFeedbackPo;

import java.util.List;

/**
 * 用户反馈映射
 *
 * @since 2024-5-24
 *
 */
public interface UserFeedbackMapper {
    /**
     * 用户反馈创建接口
     *
     * @param usrFeedbackDto 用户反馈信息
     */
    void insert(UsrFeedbackDto usrFeedbackDto);

    /**
     * 用户反馈更新接口
     *
     * @param logId 日志Id
     * @param usrFeedback 用户反馈
     * @param usrFeedbackText 用户反馈文本
     */
    void updateOne(Long logId, Integer usrFeedback, String usrFeedbackText);

    /**
     * 通过日志Id删除用户反馈记录
     *
     * @param logId 日志Id
     */
    void deleteByLogId(Long logId);

    /**
     * 获取用户反馈列表
     *
     * @return 用户反馈列表
     */
    List<UsrFeedbackPo> getAllUsrFeedbacks();

    /**
     * 通过日志Id获取用户反馈信息
     *
     * @param logId 日志Id
     * @return 用户反馈信息
     */
    UsrFeedbackPo getUsrFeedbackByLogId(Long logId);
}
