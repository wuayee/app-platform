/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.aipp.service;

import com.huawei.fit.jane.common.entity.OperationContext;
import com.huawei.fit.jober.aipp.common.PageResponse;
import com.huawei.fit.jober.aipp.condition.AippInstanceQueryCondition;
import com.huawei.fit.jober.aipp.condition.PaginationCondition;
import com.huawei.fit.jober.aipp.dto.AippInstanceCreateDto;
import com.huawei.fit.jober.aipp.dto.AippInstanceDto;
import com.huawei.fit.jober.aipp.dto.AppBuilderAppDto;
import com.huawei.fit.jober.aipp.dto.AppBuilderAppStartDto;
import com.huawei.fit.jober.aipp.dto.form.AippFormRsp;
import com.huawei.fit.jober.aipp.vo.MetaVo;

import modelengine.fitframework.flowable.Choir;
import modelengine.fitframework.model.Tuple;

import java.util.List;
import java.util.Map;

/**
 * aipp运行时服务层接口
 *
 * @author 刘信宏
 * @since 2023-12-15
 */
public interface AippRunTimeService {
    /**
     * 查询aipp id的node节点对应的表单
     *
     * @param aippId aipp id
     * @param version aipp version
     * @param startOrEnd 开始或结束节点信息
     * @param context 操作上下文
     * @return 表单信息
     */
    AippFormRsp queryEdgeSheetData(String aippId, String version, String startOrEnd, OperationContext context);

    /**
     * 指定版本启动一个Aipp
     *
     * @param context 操作上下文
     * @param aippId aippId
     * @param version aipp 版本
     * @param initContext 表示start表单填充的内容，作为流程初始化的businessData。 例如 图片url, 文本输入, prompt
     * @return 实例id
     */
    String createAippInstance(String aippId, String version, Map<String, Object> initContext, OperationContext context);

    /**
     * 指定版本启动一个App
     *
     * @param appId appId
     * @param question 对话提问
     * @param businessData 表示start表单填充的内容，作为流程初始化的businessData。 例如 图片url, 文本输入, prompt
     * @param context 操作上下文
     * @param isDebug 是否是调试对话
     * @return 实例id
     */
    Tuple createInstanceByApp(String appId, String question, Map<String, Object> businessData,
            OperationContext context, boolean isDebug);

    /**
     * 查询app对应的metaVo
     *
     * @param appId app的id
     * @param isDebug 是否查询debug阶段的meta
     * @param context 操作上下文
     * @return meta的id和version
     */
    MetaVo queryLatestMetaVoByAppId(String appId, boolean isDebug, OperationContext context);

    /**
     * 指定版本，启动一个流程
     *
     * @param metaInstId 实例id
     * @param initContext 表示start表单填充的内容，作为流程初始化的businessData。 例如 图片url, 文本输入, prompt
     * @param context 操作上下文
     * @return 实例id
     */
    Choir<Object> startFlowWithUserSelectMemory(String metaInstId, Map<String, Object> initContext,
            OperationContext context);

    /**
     * 启动一个最新版本的Aipp
     *
     * @param context 操作上下文
     * @param aippId aippId
     * @param initContext 表示start表单填充的内容，作为流程初始化的businessData。 例如 图片url, 文本输入, prompt
     * @return 实例响应
     */
    AippInstanceCreateDto createAippInstanceLatest(String aippId, Map<String, Object> initContext,
            OperationContext context);

    /**
     * 删除应用实例
     *
     * @param context 操作上下文
     * @param aippId aippId
     * @param version aipp 版本
     * @param instanceId 实例id
     */
    void deleteAippInstance(String aippId, String version, String instanceId, OperationContext context);

    /**
     * 查询单个应用实例信息
     *
     * @param context 操作上下文
     * @param aippId aippId
     * @param version aipp 版本
     * @param instanceId 实例id
     * @return AIPP 实例
     */
    AippInstanceDto getInstance(String aippId, String version, String instanceId, OperationContext context);

    /**
     * 通过versionId唯一标识查询单个应用实例信息
     *
     * @param context 操作上下文
     * @param versionId aipp 版本id
     * @param instanceId 实例id
     * @return AIPP 实例
     */
    AippInstanceDto getInstanceByVersionId(String versionId, String instanceId, OperationContext context);

    /**
     * 查询应用实例信息列表
     *
     * @param context 操作上下文
     * @param aippId aippId
     * @param version aipp 版本
     * @param cond 查询条件
     * @param page 分页条件
     * @return AIPP 实例列表
     */
    PageResponse<AippInstanceDto> listInstance(String aippId, String version, AippInstanceQueryCondition cond,
            PaginationCondition page, OperationContext context);

    /**
     * 更新表单数据并上传到小海
     *
     * @param context 操作上下文
     * @param aippId aippId
     * @param instanceId 实例id
     * @param formArgs 用于填充表单的数据
     */
    void updateAndUploadAippInstance(String aippId, String instanceId, Map<String, Object> formArgs,
            OperationContext context);

    /**
     * 更新表单数据，并恢复实例任务执行
     *
     * @param instanceId 实例id
     * @param formArgs 用于填充表单的数据
     * @param context 操作上下文
     * @return SSE流
     */
    Choir<Object> resumeAndUpdateAippInstance(String instanceId, Map<String, Object> formArgs,
            OperationContext context);

    /**
     * 终止aipp实例
     *
     * @param context 操作上下文
     * @param instanceId 实例id
     * @param msgArgs 用于终止时返回的信息
     * @return 终止对话后返回的信息
     */
    String terminateInstance(String instanceId, Map<String, Object> msgArgs, OperationContext context);

    /**
     * 终止aipp全部实例
     *
     * @param aippId aipp id
     * @param versionId versionId
     * @param isDeleteLog 是否删除aipp log
     * @param context 操作上下文
     */
    void terminateAllPreviewInstances(String aippId, String versionId, boolean isDeleteLog, OperationContext context);

    /**
     * 分享对话
     *
     * @param chats 表示需要分享的对话
     * @return 表示分享后的结果
     */
    Map<String, Object> shared(List<Map<String, Object>> chats);

    /**
     * 获取分享内容
     *
     * @param shareId 分享唯一标识
     * @return 分享内容
     */
    Map<String, Object> getShareData(String shareId);

    /**
     * 启动对话实例
     *
     * @param appDto      app信息
     * @param initContext 表示start表单填充的内容，作为流程初始化的businessData。 例如 图片url, 文本输入, prompt
     * @param context     操作上下文
     * @return 实例id
     */
    AppBuilderAppStartDto startInstance(AppBuilderAppDto appDto, Map<String, Object> initContext,
        OperationContext context);
}
