/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.base.controller;

import com.huawei.fit.http.annotation.DeleteMapping;
import com.huawei.fit.http.annotation.GetMapping;
import com.huawei.fit.http.annotation.PatchMapping;
import com.huawei.fit.http.annotation.PathVariable;
import com.huawei.fit.http.annotation.PostMapping;
import com.huawei.fit.http.annotation.RequestBody;
import com.huawei.fit.http.annotation.RequestMapping;
import com.huawei.fitframework.annotation.Component;
import com.huawei.jade.app.engine.base.dto.UsrFeedbackDto;
import com.huawei.jade.app.engine.base.service.UsrFeedbackService;

import java.util.List;

/**
 * 处理用户Aipp请求
 *
 * @since 2024-5-24
 *
 */
@Component
@RequestMapping("/aipp/usr")
public class UsrFeedbackController {
    private final UsrFeedbackService usrFeedbackService;

    public UsrFeedbackController(UsrFeedbackService usrFeedbackService) {
        this.usrFeedbackService = usrFeedbackService;
    }

    /**
     * 创建用户反馈记录
     *
     * @param usrFeedbackDto 用户反馈消息体
     */
    @PostMapping("/feedback")
    public void createUsrFeedback(@RequestBody UsrFeedbackDto usrFeedbackDto) {
        usrFeedbackService.create(usrFeedbackDto);
    }

    /**
     * 更新用户反馈信息
     *
     * @param usrFeedbackDto 用户反馈消息体
     * @param logId 日志Id
     */
    @PatchMapping("/feedback/{logId}")
    public void updateUsrFeedback(@PathVariable("logId") Long logId, @RequestBody UsrFeedbackDto usrFeedbackDto) {
        usrFeedbackService.updateOne(logId, usrFeedbackDto);
    }

    /**
     * 删除用户反馈信息
     *
     * @param logId 对话id
     */
    @DeleteMapping("/feedback/{logId}")
    public void deleteByLogId(@PathVariable("logId") Long logId) {
        usrFeedbackService.deleteByLogId(logId);
    }

    /**
     * 获取用户反馈信息列表
     *
     * @return 用户反馈信息列表
     */
    @GetMapping("/feedbacks")
    public List<UsrFeedbackDto> getAllUsrFeedbacks() {
        return usrFeedbackService.getAllUsrFeedbacks();
    }

    /**
     * 通过LogId获取对话信息列表
     *
     * @param logId 日志Id
     * @return 对话信息
     */
    @GetMapping("/feedback/{logId}")
    public UsrFeedbackDto getAllAnswerByLogId(@PathVariable("logId") Long logId) {
        return usrFeedbackService.getUsrFeedbackByLogId(logId);
    }
}
