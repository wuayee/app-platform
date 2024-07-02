/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.aipp.fitable.agent;

import com.huawei.fit.jane.common.entity.OperationContext;
import com.huawei.fit.jane.common.utils.SleepUtil;
import com.huawei.fit.jane.meta.multiversion.MetaInstanceService;
import com.huawei.fit.jane.meta.multiversion.instance.InstanceDeclarationInfo;
import com.huawei.fit.jober.FlowableService;
import com.huawei.fit.jober.aipp.constants.AippConst;
import com.huawei.fit.jober.aipp.dto.AippInstanceCreateDto;
import com.huawei.fit.jober.aipp.dto.AippInstanceDto;
import com.huawei.fit.jober.aipp.dto.aipplog.AippLogCreateDto;
import com.huawei.fit.jober.aipp.entity.AippInstLog;
import com.huawei.fit.jober.aipp.enums.AippInstLogType;
import com.huawei.fit.jober.aipp.enums.MetaInstStatusEnum;
import com.huawei.fit.jober.aipp.service.AippLogService;
import com.huawei.fit.jober.aipp.service.AippRunTimeService;
import com.huawei.fit.jober.aipp.util.DataUtils;
import com.huawei.fit.jober.aipp.util.MetaInstanceUtils;
import com.huawei.fit.jober.common.ErrorCodes;
import com.huawei.fit.jober.common.exceptions.JobberException;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.annotation.Fitable;
import com.huawei.fitframework.annotation.Value;
import com.huawei.fitframework.inspection.Validation;
import com.huawei.fitframework.log.Logger;

import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * aipp agent启动逻辑
 *
 * @author l00611472
 * @since 2023-12-27
 */
@Component
public class AippFlowAgent implements FlowableService {
    private static final String AGENT_INST_URL_FORMAT = "%s/#/aippRun/%s/detail/%s/%s/?fullScreen=true";
    private static final Logger log = Logger.get(AippFlowAgent.class);

    private final AippRunTimeService aippRunTimeService;
    private final MetaInstanceService metaInstanceService;
    private final AippLogService aippLogService;
    private final String endpoint;

    public AippFlowAgent(AippRunTimeService aippRunTimeService, MetaInstanceService metaInstanceService,
            AippLogService aippLogService, @Value("${jane.endpoint}") String endpoint) {
        this.aippRunTimeService = aippRunTimeService;
        this.metaInstanceService = metaInstanceService;
        this.aippLogService = aippLogService;
        this.endpoint = endpoint;
    }

    public String getInstUrlFormat() {
        return AGENT_INST_URL_FORMAT;
    }

    void syncAgent(String agentAippId, AippInstanceCreateDto agentInstDto, Map<String, Object> agentParams,
            OperationContext context) {
        int times = 0;
        while (true) {
            AippInstanceDto inst = aippRunTimeService.getInstanceByVersionId(agentInstDto.getVersionId(),
                    agentInstDto.getInstanceId(),
                    context);
            MetaInstStatusEnum status = MetaInstStatusEnum.getMetaInstStatus(inst.getStatus());
            if (status.getValue() <= MetaInstStatusEnum.RUNNING.getValue()) {
                if (times >= AippConst.RETRY_TIMES) {
                    throw new JobberException(ErrorCodes.UN_EXCEPTED_ERROR,
                            String.format(Locale.ROOT, "agent %s timeout %d", agentAippId, times));
                }
                SleepUtil.sleep(AippConst.RETRY_INTERVAL);
                times++;
                continue;
            }
            if (status.getValue() == MetaInstStatusEnum.ARCHIVED.getValue()) {
                String resultKey = (String) agentParams.get(AippConst.BS_AGENT_RESULT_LINK_KEY);
                String result = inst.getFormArgs().get(resultKey);
                agentParams.put(resultKey, result); // end 节点持久化
                break;
            }
            throw new JobberException(ErrorCodes.UN_EXCEPTED_ERROR,
                    String.format(Locale.ROOT, "agent %s result %s", agentAippId, status.name()));
        }
    }

    /**
     * 启动agent实例
     *
     * @param flowData 流程执行上下文数据
     * @return 流程数据
     */
    @Fitable("com.huawei.fit.jober.aipp.fitable.AppFlowAgent")
    @Override
    public List<Map<String, Object>> handleTask(List<Map<String, Object>> flowData) {
        Map<String, Object> businessData = DataUtils.getBusiness(flowData);
        log.debug("AippAgent businessData {}", businessData);

        Map<String, Object> agentParams = DataUtils.getAgentParams(flowData);
        if (!agentParams.containsKey(AippConst.BS_AGENT_RESULT_LINK_KEY)) {
            agentParams.put(AippConst.BS_AGENT_RESULT_LINK_KEY, AippConst.INST_AGENT_RESULT_KEY);
        }
        String agentAippId = getAgentAippId(flowData, agentParams);

        Map<String, Object> initContext = Collections.singletonMap(AippConst.BS_INIT_CONTEXT_KEY, agentParams);
        OperationContext context = DataUtils.getOpContext(businessData);
        AippInstanceCreateDto agentInstDto =
                aippRunTimeService.createAippInstanceLatest(agentAippId, initContext, context);
        // 同步执行agent
        try {
            syncAgent(agentAippId, agentInstDto, agentParams, context);
        } catch (JobberException e) {
            fetchAgentErrorMsgToMain(agentInstDto.getInstanceId(), businessData, context.getW3Account());
            throw e;
        }

        // 获取agent结果到主aipp
        String resultKey = (String) agentParams.get(AippConst.BS_AGENT_RESULT_LINK_KEY);
        businessData.put(resultKey, agentParams.get(resultKey));

        // 保存agent实例url到主aipp
        saveAgentResultUrl(businessData, agentParams, agentAippId, context, agentInstDto.getInstanceId());

        return flowData;
    }

    private void saveAgentResultUrl(Map<String, Object> businessData, Map<String, Object> agentParams,
            String agentAippId, OperationContext context, String agentInstId) {
        String instUrlKey = (String) agentParams.get(AippConst.BS_AGENT_INST_URL_LINK_KEY);
        if (StringUtils.isNotBlank(instUrlKey)) {
            String instUrl = String.format(Locale.ROOT,
                    AGENT_INST_URL_FORMAT,
                    endpoint,
                    context.getTenantId(),
                    agentAippId,
                    agentInstId);
            InstanceDeclarationInfo info = InstanceDeclarationInfo.custom().putInfo(instUrlKey, instUrl).build();
            MetaInstanceUtils.persistInstance(metaInstanceService, info, businessData, context);
        }
    }

    private String getAgentAippId(List<Map<String, Object>> flowData, Map<String, Object> agentParams) {
        String agentAippId = DataUtils.getAgentId(DataUtils.getContextData(flowData));
        if (agentAippId.isEmpty()) {
            agentAippId = (String) agentParams.get(AippConst.BS_AGENT_ID_KEY);
        }
        Validation.notNull(agentAippId, "agentAippId can not be null");
        return agentAippId;
    }

    private void fetchAgentErrorMsgToMain(String agentInstId, Map<String, Object> businessData, String w3Account) {
        List<AippInstLog> instLogs = aippLogService.queryInstanceLogSince(agentInstId, null);
        if (!instLogs.isEmpty() && AippInstLogType.ERROR.name()
                .equals(instLogs.get(instLogs.size() - 1).getLogType())) {
            String aippId = (String) businessData.get(AippConst.BS_AIPP_ID_KEY);
            String version = (String) businessData.get(AippConst.BS_AIPP_VERSION_KEY);
            String aippType = (String) businessData.get(AippConst.ATTR_AIPP_TYPE_KEY);
            String instId = (String) businessData.get(AippConst.BS_AIPP_INST_ID_KEY);
            aippLogService.insertLog(AippLogCreateDto.builder()
                    .aippId(aippId)
                    .version(version)
                    .aippType(aippType)
                    .instanceId(instId)
                    .logType(AippInstLogType.ERROR.name())
                    .logData(instLogs.get(instLogs.size() - 1).getLogData())
                    .createUserAccount(w3Account)
                    .build());
        }
    }
}
