/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fit.jober.aipp.fitable;

import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.jober.aipp.constants.AippConst;
import modelengine.fit.jober.aipp.domain.AppBuilderRuntimeInfo;
import modelengine.fit.jober.aipp.domains.business.RunContext;
import modelengine.fit.jober.aipp.dto.chat.AppChatRsp;
import modelengine.fit.jober.aipp.repository.AppBuilderRuntimeInfoRepository;
import modelengine.fit.jober.aipp.service.AppChatSessionService;
import modelengine.fit.jober.aipp.service.AppChatSseService;
import modelengine.fit.jober.aipp.service.RuntimeInfoService;
import modelengine.fit.jober.aipp.util.ConvertUtils;

import modelengine.fit.waterflow.domain.enums.FlowTraceStatus;
import modelengine.fit.waterflow.entity.FlowErrorInfo;
import modelengine.fit.waterflow.entity.FlowNodePublishInfo;
import modelengine.fit.waterflow.entity.FlowPublishContext;
import modelengine.fit.waterflow.spi.FlowPublishService;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.annotation.Fitable;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 订阅节点运行时信息.
 *
 * @author 张越
 * @since 2024-05-23
 */
@Component
public class FlowPublishSubscriber implements FlowPublishService {
    private final AppBuilderRuntimeInfoRepository runtimeInfoRepository;
    private final RuntimeInfoService runtimeInfoService;
    private final ToolExceptionHandle toolExceptionHandle;
    private final AppChatSessionService appChatSessionService;
    private final AppChatSseService appChatSSEService;

    /**
     * 构造函数.
     *
     * @param runtimeInfoRepository {@link AppBuilderRuntimeInfoRepository} 对象.
     * @param toolExceptionHandle toolExceptionHandle
     * @param appChatSessionService {@link AppChatSessionService} 对象。
     * @param appChatSSEService {@link AppChatSseService} 对象。
     * @param runtimeInfoService {@link RuntimeInfoService} 对象.
     */
    public FlowPublishSubscriber(AppBuilderRuntimeInfoRepository runtimeInfoRepository,
            @Fit ToolExceptionHandle toolExceptionHandle, AppChatSessionService appChatSessionService,
            AppChatSseService appChatSSEService, RuntimeInfoService runtimeInfoService) {
        this.runtimeInfoRepository = runtimeInfoRepository;
        this.toolExceptionHandle = toolExceptionHandle;
        this.appChatSessionService = appChatSessionService;
        this.appChatSSEService = appChatSSEService;
        this.runtimeInfoService = runtimeInfoService;
    }

    @Fitable("modelengine.fit.jober.aipp.fitable.FlowPublishSubscriber")
    @Override
    public void publishNodeInfo(FlowNodePublishInfo flowNodePublishInfo) {
        Map<String, Object> businessData = flowNodePublishInfo.getBusinessData();
        RunContext runContext = new RunContext(businessData, new OperationContext());
        String aippInstId = runContext.getTaskInstanceId();
        String chatId = runContext.getOriginChatId();
        String atChatId = runContext.getAtChatId();
        String stage = flowNodePublishInfo.getFlowContext() == null
                ? StringUtils.EMPTY
                : flowNodePublishInfo.getFlowContext().getStage();
        if (StringUtils.equalsIgnoreCase("before", stage)) {
            // 考虑性能问题，当前对 enableStageDesc 为 true 的场景做特殊处理；后续考虑通用方案
            this.stageBeforeHandle(flowNodePublishInfo, aippInstId, chatId, atChatId);
        } else {
            this.stageProcessedHandle(flowNodePublishInfo, businessData, aippInstId);
        }
    }

    private void stageProcessedHandle(FlowNodePublishInfo flowNodePublishInfo, Map<String, Object> businessData,
            String aippInstId) {
        FlowPublishContext context = flowNodePublishInfo.getFlowContext();
        String traceId = context.getTraceId();
        String nodeId = flowNodePublishInfo.getNodeId();
        String nodeType = flowNodePublishInfo.getNodeType();
        FlowErrorInfo errorInfo = flowNodePublishInfo.getErrorMsg();
        AtomicReference<Locale> locale = new AtomicReference<>(Locale.getDefault());
        this.appChatSessionService.getSession(aippInstId).ifPresent(e -> locale.set(e.getLocale()));
        ToolExceptionHandle.handleFitException(errorInfo);
        String finalErrorMsg = this.toolExceptionHandle.getFixErrorMsg(errorInfo, locale.get(), false);
        if (StringUtils.isBlank(finalErrorMsg)) {
            finalErrorMsg = errorInfo.getErrorMessage();
        }
        AppBuilderRuntimeInfo runtimeInfo = AppBuilderRuntimeInfo.builder()
                .traceId(traceId)
                .flowDefinitionId(flowNodePublishInfo.getFlowDefinitionId())
                .instanceId(ObjectUtils.cast(businessData.get(AippConst.BS_AIPP_INST_ID_KEY)))
                .nodeId(nodeId)
                .nodeType(nodeType)
                .startTime(ConvertUtils.toLong(context.getCreateAt()))
                .endTime(this.getEndTime(context))
                .published(this.runtimeInfoService.isPublished(businessData))
                .parameters(this.runtimeInfoService.buildParameters(businessData, nodeId))
                .errorMsg(finalErrorMsg)
                .status(context.getStatus())
                .nextPositionId(flowNodePublishInfo.getNextPositionId())
                .createBy("system")
                .updateBy("system")
                .createAt(LocalDateTime.now())
                .updateAt(LocalDateTime.now())
                .build();
        this.runtimeInfoRepository.insertOne(runtimeInfo);
    }

    private void stageBeforeHandle(FlowNodePublishInfo flowNodePublishInfo, String aippInstId, String chatId,
            String atChatId) {
        Map<String, Object> nodeProperties = flowNodePublishInfo.getNodeProperties();
        if (nodeProperties.containsKey("enableStageDesc") && ObjectUtils.<Boolean>cast(nodeProperties.get(
                "enableStageDesc"))) {
            AppChatRsp appChatRsp = AppChatRsp.builder()
                    .chatId(chatId)
                    .atChatId(atChatId)
                    .status(FlowTraceStatus.RUNNING.name())
                    .extension(nodeProperties)
                    .instanceId(aippInstId)
                    .build();
            this.appChatSSEService.sendToAncestor(aippInstId, appChatRsp);
        }
    }

    private long getEndTime(FlowPublishContext context) {
        LocalDateTime time = Optional.ofNullable(context.getArchivedAt()).orElseGet(LocalDateTime::now);
        return ConvertUtils.toLong(time);
    }
}
