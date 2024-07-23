/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.service;

import com.huawei.fit.jane.common.entity.OperationContext;
import com.huawei.fit.jober.aipp.dto.aipplog.AippInstLogDataDto;
import com.huawei.fit.jober.aipp.entity.AippInstLog;
import com.huawei.fit.jober.aipp.entity.AippLogData;

import java.util.List;
import java.util.Map;

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
     * 查询指定appId的最近一次会话的历史记录
     *
     * @param appId 指定app的id
     * @param context 登录信息
     * @return log数据
     */
    List<AippInstLogDataDto> queryAppRecentChatLog(String appId, String aippType, OperationContext context);

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
     * @param logType 日志类型
     * @param logData 日志数据
     * @param businessData 业务数据
     */
    void insertLog(String logType, AippLogData logData, Map<String, Object> businessData);

    /**
     * 插入MSG类型的历史记录
     *
     * @param msg MSG日志内容
     * @param flowData 流程执行上下文数据。
     */
    void insertMsgLog(String msg, List<Map<String, Object>> flowData);

    /**
     * 插入ERROR类型的历史记录
     *
     * @param msg ERROR日志内容
     * @param flowData 流程执行上下文数据。
     */
    void insertErrorLog(String msg, List<Map<String, Object>> flowData);

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
     * 根据父Instance的路径构建当前Instance的路径。
     *
     * @param instId 表示当前instance的id的 {@link String}。
     * @param parentInstId 表示父instance的id的 {@link String}。
     * @return 表示当前instId的路径的 {@link String}。
     */
    String buildPath(String instId, String parentInstId);

    /**
     * 删除指定实例的历史记录。
     *
     * @param instanceId 指定实例的 id。
     */
    void deleteInstanceLog(String instanceId);

    /**
     * 查询提示词拼接后的历史记录
     *
     * @param aippId 指定aipp的id
     * @param aippType aipp的类型
     * @param count 轮次数目
     * @param context 登录信息
     * @return log数据
     */
    List<AippInstLogDataDto> queryAippRecentInstLogAfterSplice(String aippId, String aippType, Integer count,
            OperationContext context);

    /**
     * 批量查询全量日志（包含父子流程），并过滤掉指定类型的日志。
     *
     * @param instanceIds 表示指定实例 id 列表的 {@link List}{@code <}{@link String}{@code >}。
     * @param filterLogTypes 表示指定日志类型列表的 {@link List}{@code <}{@link String}{@code >}。
     * @return 表示查询到的日志列表的 {@link List}{@code <}{@link AippInstLog}{@code >}。
     */
    List<AippInstLog> queryBatchAndFilterFullLogsByLogType(List<String> instanceIds, List<String> filterLogTypes);

    /**
     * 查询指定实例的日志，并过滤掉指定类型的日志。
     *
     * @param instanceId 表示指定实例 id 的 {@link String}。
     * @param filterLogTypes 表示指定日志类型列表的 {@link List}{@code <}{@link String}{@code >}。
     * @return 表示查询到的日志列表的 {@link List}{@code <}{@link AippInstLog}{@code >}。
     */
    List<AippInstLog> queryAndFilterLogsByLogType(String instanceId, List<String> filterLogTypes);
}
