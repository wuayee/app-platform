/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.service;

import com.huawei.fit.jane.common.entity.OperationContext;
import com.huawei.fit.jober.aipp.dto.aipplog.AippInstLogDataDto;
import com.huawei.fit.jober.aipp.dto.aipplog.AippLogCreateDto;
import com.huawei.fit.jober.aipp.entity.AippInstLog;

import java.util.List;

/**
 * aipp实例历史记录服务接口
 *
 * @author l00611472
 * @since 2024-01-08
 */
public interface AippLogService {
    /**
     * 查询指定app最近5个的历史记录
     *
     * @param appId 指定app的id
     * @param type app的类型
     * @param context 登录信息
     * @return log数据
     */
    List<AippInstLogDataDto> queryAippRecentInstLog(String appId, String type, OperationContext context);

    /**
     * 查询指定aipp最近轮次的历史记录
     *
     * @param aippId 指定aipp的id
     * @param aippType 指定aipp的类型
     * @param count 轮次数目
     * @param context 登录信息
     * @return log数据
     */
    List<AippInstLogDataDto> queryAippRecentInstLog(String aippId, String aippType, Integer count,
            OperationContext context);

    /**
     * 查询指定chatId的历史记录,
     *
     * @param aippId 指定aipp的id
     * @param aippType 指定aipp的类型
     * @param count 轮次数目
     * @param context 登录信息
     * @param chatId 会话ID
     * @return log数据
     */
    List<AippInstLogDataDto> queryChatRecentInstLog(String aippId, String aippType, Integer count,
                                                    OperationContext context, String chatId);

    /**
     * 查询指定aipp instance的历史记录, 可选开始时间
     *
     * @param instanceId 指定aipp instance的id
     * @param timeString 开始的时间范围, 可能为空
     * @return log数据
     */
    List<AippInstLog> queryInstanceLogSince(String instanceId, String timeString);

    /**
     * 查询指定aipp instance的form类型的最新一条历史记录
     *
     * @param instanceId 指定aipp instance的id
     * @return log数据
     */
    AippInstLog queryLastInstanceFormLog(String instanceId);

    /**
     * 删除指定app的历史记录
     *
     * @param appId 指定app的id
     * @param type 指定app的类型
     * @param context 登录信息
     */
    void deleteAippInstLog(String appId, String type, OperationContext context);

    /**
     * 删除指定aipp预览的历史记录
     *
     * @param previewAippId 指定aipp的id
     * @param context 登录信息
     */
    void deleteAippPreviewLog(String previewAippId, OperationContext context);

    /**
     * 插入aipp的历史记录
     *
     * @param logDto 插入数据
     */
    void insertLog(AippLogCreateDto logDto) throws IllegalArgumentException;

    /**
     * 更新指定log id的记录
     *
     * @param logId 指定log的id
     * @param newLogData 新的log_data
     */
    void updateLog(Long logId, String newLogData) throws IllegalArgumentException;

    List<AippInstLogDataDto> queryRecentLogsSinceResume(String aippId, String aippType, OperationContext context);

    String getParentPath(String parentInstId);

    /**
     * 删除指定实例的历史记录。
     *
     * @param instanceId 指定实例的 id。
     */
    void deleteInstanceLog(String instanceId);
}
