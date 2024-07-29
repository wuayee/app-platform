/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.fitable;

import com.huawei.fit.jane.common.entity.OperationContext;
import com.huawei.fit.jane.meta.multiversion.MetaInstanceService;
import com.huawei.fit.jane.meta.multiversion.instance.InstanceDeclarationInfo;
import com.huawei.fit.jober.FlowExceptionService;
import com.huawei.fit.jober.aipp.constants.AippConst;
import com.huawei.fit.jober.aipp.entity.AippInstLog;
import com.huawei.fit.jober.aipp.enums.AippInstLogType;
import com.huawei.fit.jober.aipp.enums.MetaInstStatusEnum;
import com.huawei.fit.jober.aipp.service.AippLogService;
import com.huawei.fit.jober.aipp.util.DataUtils;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.annotation.Fit;
import com.huawei.fitframework.annotation.Fitable;
import com.huawei.fitframework.log.Logger;
import com.huawei.fitframework.util.ObjectUtils;
import com.huawei.fitframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 流程异常处理服务
 *
 * @author s00664640
 * @since 2024-05-10
 */
@Component
public class AippFlowExceptionHandle implements FlowExceptionService {
    private static final Logger log = Logger.get(AippFlowExceptionHandle.class);

    private final AippLogService aippLogService;
    private final MetaInstanceService metaInstanceService;

    public AippFlowExceptionHandle(@Fit AippLogService aippLogService, @Fit MetaInstanceService metaInstanceService) {
        this.aippLogService = aippLogService;
        this.metaInstanceService = metaInstanceService;
    }

    private void addErrorLog(String aippInstId, List<Map<String, Object>> contexts, String errorMessage) {
        List<AippInstLog> instLogs = aippLogService.queryInstanceLogSince(aippInstId, null);
        if (!instLogs.isEmpty()) {
            if (AippInstLogType.ERROR.name().equals(instLogs.get(instLogs.size() - 1).getLogType())) {
                return;
            }
        }
        String msg = "很抱歉，我遇到了问题，请稍后重试。";
        if (StringUtils.isNotEmpty(errorMessage)) {
            msg += "\n提示：" + errorMessage;
        }
        this.aippLogService.insertErrorLog(msg, contexts);
    }

    /**
     * 异常回调实现
     *
     * @param nodeId 异常发生的节点Id
     * @param contexts 流程上下文
     * @param errorMessage 异常错误信息
     */
    @Fitable("com.huawei.fit.jober.aipp.fitable.AippFlowExceptionHandler")
    @Override
    public void handleException(String nodeId, List<Map<String, Object>> contexts, String errorMessage) {
        Map<String, Object> businessData = DataUtils.getBusiness(contexts);
        String versionId = ObjectUtils.cast(businessData.get(AippConst.BS_META_VERSION_ID_KEY));
        log.error("versionId {} nodeId {} errorMessage {}, handleException businessData {}",
                versionId,
                nodeId,
                errorMessage,
                businessData);

        String aippInstId = ObjectUtils.cast(businessData.get(AippConst.BS_AIPP_INST_ID_KEY));
        InstanceDeclarationInfo declarationInfo = InstanceDeclarationInfo.custom()
                .putInfo(AippConst.INST_FINISH_TIME_KEY, LocalDateTime.now())
                .putInfo(AippConst.INST_STATUS_KEY, MetaInstStatusEnum.ERROR.name())
                .build();
        OperationContext context = DataUtils.getOpContext(businessData);
        metaInstanceService.patchMetaInstance(versionId, aippInstId, declarationInfo, context);
        addErrorLog(aippInstId, contexts, errorMessage);
    }
}
