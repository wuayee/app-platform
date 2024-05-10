/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.aipp.service;

import com.huawei.fit.jane.common.entity.OperationContext;
import com.huawei.fit.jane.common.response.Rsp;
import com.huawei.fit.jober.aipp.common.PageResponse;
import com.huawei.fit.jober.aipp.common.exception.AippException;
import com.huawei.fit.jober.aipp.common.exception.AippForbiddenException;
import com.huawei.fit.jober.aipp.common.exception.AippParamException;
import com.huawei.fit.jober.aipp.condition.AippQueryCondition;
import com.huawei.fit.jober.aipp.condition.PaginationCondition;
import com.huawei.fit.jober.aipp.dto.AippCreateDto;
import com.huawei.fit.jober.aipp.dto.AippDetailDto;
import com.huawei.fit.jober.aipp.dto.AippDto;
import com.huawei.fit.jober.aipp.dto.AippOverviewRspDto;
import com.huawei.fit.jober.aipp.dto.AippVersionDto;

import java.util.List;

/**
 * aipp编排服务层接口
 *
 * @author l00611472
 * @since 2023-12-12
 */
public interface AippFlowService {
    /**
     * 查询aipp详情
     *
     * @param aippId aippId
     * @param version aipp版本
     * @param context 操作上下文
     * @return aipp 详情
     */
    Rsp<AippDetailDto> queryAippDetail(String aippId, String version, OperationContext context);

    /**
     * 查询aipp列表
     *
     * @param cond 过滤条件
     * @param page 分页
     * @param context 操作上下文
     * @return aipp 概况
     */
    PageResponse<AippOverviewRspDto> listAipp(AippQueryCondition cond, PaginationCondition page,
            OperationContext context);

    /**
     * 查询指定aipp的版本列表
     *
     * @param aippId aippId
     * @param context 操作上下文
     * @return aipp 版本概况
     */
    List<AippVersionDto> listAippVersions(String aippId, OperationContext context);

    /**
     * 删除aipp
     *
     * @param aippId aippId
     * @param version aipp版本
     * @param context 操作上下文
     * @throws AippForbiddenException 禁止删除aipp异常
     */
    void deleteAipp(String aippId, String version, OperationContext context) throws AippForbiddenException;

    /**
     * 创建aipp
     *
     * @param aippDto aipp定义
     * @param context 操作上下文
     * @return aipp id和版本信息
     * @throws AippParamException 入参异常
     * @throws AippException 创建aipp异常
     */
    AippCreateDto create(AippDto aippDto, OperationContext context) throws AippException;

    /**
     * 更新aipp
     *
     * @param aippDto aipp定义
     * @param context 操作上下文
     * @return aipp id信息
     * @throws AippForbiddenException 禁止更新aipp异常
     * @throws AippParamException 入参异常
     */
    AippCreateDto update(AippDto aippDto, OperationContext context) throws AippForbiddenException, AippParamException;

    /**
     * 预览aipp
     *
     * @param baselineVersion aipp 的基线版本
     * @param aippDto aipp定义
     * @param context 操作上下文
     * @return 创建预览aipp的id和version
     * @throws AippException 预览aipp异常
     */
    AippCreateDto previewAipp(String baselineVersion, AippDto aippDto, OperationContext context);

    /**
     * 退出预览aipp的清理
     *
     * @param previewAippId 预览版本的aippId
     * @param previewVersion 预览版本号
     * @param context 操作上下文
     */
    void cleanPreviewAipp(String previewAippId, String previewVersion, OperationContext context);

    /**
     * 升级aipp
     *
     * @param baselineVersion 基线版本
     * @param aippDto aipp定义
     * @param context 操作上下文
     * @return aipp id信息
     */
    AippCreateDto upgrade(String baselineVersion, AippDto aippDto, OperationContext context);

    /**
     * 发布aipp
     *
     * @param aippDto aipp定义
     * @param context 操作上下文
     * @return 发布aipp概况
     * @throws AippForbiddenException 禁止更新aipp异常
     * @throws AippParamException 入参异常
     * @throws AippException 发布aipp异常
     */
    Rsp<AippCreateDto> publish(AippDto aippDto, OperationContext context) throws AippException;
}
