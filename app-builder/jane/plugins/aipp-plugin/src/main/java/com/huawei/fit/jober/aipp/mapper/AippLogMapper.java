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
    List<AippInstLog> selectRecentByAippId(String aippId, String aippType, String createUserAccount);

    List<String> selectRecentInstanceId(String aippId, String aippType, Integer number, String createUserAccount);

    List<String> selectRecentAfterResume(String aippId, String aippType, String createUserAccount);

    List<AippInstLog> getLogsByInstanceId(List<String> instanceIds);

    List<AippInstLog> selectWithCondition(AippLogQueryCondition cond);

    AippInstLog selectLastInstanceFormLog(String instanceId);

    List<String> selectNormalInstanceIdOrderByTimeDesc(String aippId, String aippType, String createUserAccount);

    void deleteByType(String aippId, String aippType, String createUserAccount, String instanceIdExclude);

    void delete(String aippId, String version, String createUserAccount, String instanceIdExclude);

    void insertOne(AippLogCreateDto data);

    void updateOne(Long logId, String newLogData);

    String getParentPath(String parentId);
}
