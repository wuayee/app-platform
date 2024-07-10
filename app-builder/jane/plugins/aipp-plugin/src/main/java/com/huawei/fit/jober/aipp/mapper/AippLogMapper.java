/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.mapper;

import com.huawei.fit.jober.aipp.dto.aipplog.AippLogCreateDto;
import com.huawei.fit.jober.aipp.dto.aipplog.AippLogQueryCondition;
import com.huawei.fit.jober.aipp.entity.AippInstLog;

import java.util.List;

/**
 * aipp实例历史记录db接口
 *
 * @author l00611472
 * @since 2024-01-08
 */
public interface AippLogMapper {
    /**
     * 根据 aipp id 查询最近若干条历史记录。
     *
     * @param aippId 表示指定 aipp id 的 {@link String}。
     * @param aippType 表示指定 aipp 类型的 {@link String}。
     * @param createUserAccount 表示创建者账号的 {@link String}。
     * @return 表示查询到的历史记录的 {@link List}{@code <}{@link AippInstLog}{@code >}。
     */
    List<AippInstLog> selectRecentByAippId(String aippId, String aippType, String createUserAccount);

    /**
     * 根据 aipp id 查询指定数量的 instance id。
     *
     * @param aippId 表示指定 aipp id 的 {@link String}。
     * @param aippType 表示指定 aipp 类型的 {@link String}。
     * @param number 表示查询数量的 {@link Integer}。
     * @param createUserAccount 表示创建者账号的 {@link String}。
     * @return 表示查询到的实例 id 的 {@link List}{@code <}{@link String}{@code >}。
     */
    List<String> selectRecentInstanceId(String aippId, String aippType, Integer number, String createUserAccount);

    /**
     * 根据 aipp id 列表查询最近若干条 instance id。
     *
     * @param aippIds 表示指定 aipp id 的 {@link List}{@code <}{@link String}{@code >}。
     * @param aippType 表示指定 aipp 类型的 {@link String}。
     * @param number 表示查询数量的 {@link Integer}。
     * @param createUserAccount 表示创建者账号的 {@link String}。
     * @return 表示查询到的实例 id 的 {@link List}{@code <}{@link String}{@code >}。
     */
    List<String> selectRecentInstanceIdByAippIds(List<String> aippIds, String aippType, Integer number,
            String createUserAccount);

    /**
     * 用于查询简历之后的实例 id 列表。
     *
     * @param aippId 表示指定 aipp id 的 {@link String}。
     * @param aippType 表示指定 aipp 类型的 {@link String}。
     * @param createUserAccount 表示创建者账号的 {@link String}。
     * @return 表示查询到的实例 id 的 {@link List}{@code <}{@link String}{@code >}。
     */
    List<String> selectRecentAfterResume(String aippId, String aippType, String createUserAccount);

    /**
     * 根据 path 模糊匹配 instanceId 查询历史记录。
     *
     * @param instanceIds 表示指定实例 id 的 {@link List}{@code <}{@link String}{@code >}。
     * @return 表示查询到的历史记录的 {@link List}{@code <}{@link AippInstLog}{@code >}。
     */
    List<AippInstLog> getLogsByInstanceIds(List<String> instanceIds);

    /**
     * 根据条件查询历史记录。
     *
     * @param cond 表示查询条件的 {@link AippLogQueryCondition}。
     * @return 表示查询到的历史记录的 {@link List}{@code <}{@link AippInstLog}{@code >}。
     */
    List<AippInstLog> selectWithCondition(AippLogQueryCondition cond);

    /**
     * 根据 instance id 查询最新的 FORM 类型历史记录。
     *
     * @param instanceId 表示指定实例 id 的 {@link String}。
     * @return 表示查询到的历史记录的 {@link AippInstLog}。
     */
    AippInstLog selectLastInstanceFormLog(String instanceId);

    /**
     * 根据 aipp id 列表按时间倒序查询 instance id 列表。
     *
     * @param aippIds 表示指定 aipp id 的 {@link List}{@code <}{@link String}{@code >}。
     * @param aippType 表示指定 aipp 类型的 {@link String}。
     * @param createUserAccount 表示创建者账号的 {@link String}。
     * @return 表示查询到的实例 id 的 {@link List}{@code <}{@link String}{@code >}。
     */
    List<String> selectNormalInstanceIdOrderByTimeDesc(List<String> aippIds, String aippType, String createUserAccount);

    /**
     * 根据 aipp 类型删除历史记录。
     *
     * @param aippId 表示指定 aipp id 的 {@link String}。
     * @param aippType 表示指定 aipp 类型的 {@link String}。
     * @param createUserAccount 表示创建者账号的 {@link String}。
     * @param instanceIdExclude 表示排除的 instance id 的 {@link String}。
     */
    void deleteByType(String aippId, String aippType, String createUserAccount, String instanceIdExclude);

    /**
     * 根据 aipp id 批量删除历史记录。
     *
     * @param aippIds 表示指定 aipp id 的 {@link List}{@code <}{@link String}{@code >}。
     * @param aippType 表示指定 aipp 类型的 {@link String}。
     * @param createUserAccount 表示创建者账号的 {@link String}。
     * @param instanceIdExclude 表示排除的 instance id 的 {@link String}。
     */
    void delete(List<String> aippIds, String aippType, String createUserAccount, String instanceIdExclude);

    /**
     * 插入一条历史记录。
     *
     * @param data 表示 aipp 实例历史记录的 {@link AippLogCreateDto}。
     */
    void insertOne(AippLogCreateDto data);

    /**
     * 更新指定历史记录。
     *
     * @param logId 表示指定历史记录 id 的 {@link Long}。
     * @param newLogData 表示新的历史记录的 {@link String}。
     */
    void updateOne(Long logId, String newLogData);

    /**
     * 获取历史记录的 path 字段。
     *
     * @param parentId 表示实例 id 的 {@link String}。
     * @return 表示查询到的 path 的 {@link String}。
     */
    String getParentPath(String parentId);

    /**
     * 删除指定实例的历史记录。
     *
     * @param instanceId 表示指定实例 id 的 {@link String}。
     */
    void deleteInstanceLog(String instanceId);

    /**
     * 根据 instanceId 查询指定实例的历史记录。
     *
     * @param instanceId 表示指定实例 id 的 {@link String}。
     * @return 表示 aipp 实例历史记录的 {@link List}{@code <}{@link AippInstLog}{@code >}。
     */
    List<AippInstLog> getLogsByInstanceId(String instanceId);
}
