/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.service.impl;

import static com.huawei.fit.jober.aipp.constants.AippConst.INST_CREATE_TIME_KEY;
import static com.huawei.fit.jober.aipp.constants.AippConst.INST_CURR_FORM_ID_KEY;
import static com.huawei.fit.jober.aipp.constants.AippConst.INST_CURR_FORM_VERSION_KEY;
import static com.huawei.fit.jober.aipp.constants.AippConst.INST_FINISH_TIME_KEY;
import static com.huawei.fit.jober.aipp.constants.AippConst.INST_NAME_KEY;

import com.huawei.fit.dynamicform.entity.DynamicFormDetailEntity;
import com.huawei.fit.jane.common.entity.OperationContext;
import com.huawei.fit.jane.meta.multiversion.MetaInstanceService;
import com.huawei.fit.jane.meta.multiversion.MetaService;
import com.huawei.fit.jane.meta.multiversion.definition.Meta;
import com.huawei.fit.jane.meta.multiversion.instance.Instance;
import com.huawei.fit.jober.aipp.constants.AippConst;
import com.huawei.fit.jober.aipp.enums.AippInstLogType;
import com.huawei.fit.jober.aipp.repository.AppBuilderFormPropertyRepository;
import com.huawei.fit.jober.aipp.repository.AppBuilderFormRepository;
import com.huawei.fit.jober.aipp.service.AippLogStreamService;
import com.huawei.fit.jober.aipp.service.AippStreamService;
import com.huawei.fit.jober.aipp.util.FormUtils;
import com.huawei.fit.jober.aipp.util.MetaInstanceUtils;
import com.huawei.fit.jober.aipp.vo.AippInstanceVO;
import com.huawei.fit.jober.aipp.vo.AippLogVO;
import com.huawei.fit.jober.aipp.vo.ChatMessageVo;
import com.huawei.fit.waterflow.domain.enums.FlowTraceStatus;
import com.huawei.fitframework.annotation.Component;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * log流式服务实现，单进程实现方案.
 *
 * @author z00559346 张越
 * @since 2024-05-23
 */
@Component
public class AippLogStreamServiceImpl implements AippLogStreamService {
    private final MetaService metaService;
    private final MetaInstanceService metaInstanceService;
    private final AppBuilderFormRepository formRepository;
    private final AppBuilderFormPropertyRepository formPropertyRepository;
    private final AippStreamService aippStreamService;

    public AippLogStreamServiceImpl(MetaService metaService,
            MetaInstanceService metaInstanceService,
            AppBuilderFormRepository formRepository,
            AppBuilderFormPropertyRepository formPropertyRepository,
            AippStreamService aippStreamService) {
        this.metaService = metaService;
        this.metaInstanceService = metaInstanceService;
        this.formRepository = formRepository;
        this.formPropertyRepository = formPropertyRepository;
        this.aippStreamService = aippStreamService;
    }

    @Override
    public void send(AippLogVO log) {
        if (!log.displayable()) {
            return;
        }
        List<String> ancestors = log.getAncestors();
        Collections.reverse(ancestors);
        Optional<String> ancestorOpt = ancestors.stream()
                .filter(anc -> this.aippStreamService.getSession(anc).isPresent()).findFirst();
        ancestorOpt.ifPresent(id -> {
            AippInstanceVO aippInstanceVO = this.buildData(log);
            if (this.aippStreamService.isNewChat(id)) {
                this.aippStreamService.send(id, this.buildMessage(aippInstanceVO, log));
            } else {
                this.aippStreamService.send(id, aippInstanceVO);
            }
        });
    }

    private ChatMessageVo buildMessage(AippInstanceVO instance, AippLogVO log) {
        return ChatMessageVo.builder()
                .status(instance.getStatus())
                .messageId(log.getMsgId())
                .data(log.getLogData())
                .type(log.getLogType())
                .extension(new HashMap<>())
                .build();
    }

    private AippInstanceVO buildData(AippLogVO log) {
        String instanceId = log.getInstanceId();
        String metaVersionId = this.metaInstanceService.getMetaVersionId(instanceId);
        Meta meta = this.metaService.retrieve(metaVersionId, null);
        Instance instance = MetaInstanceUtils.getInstanceDetail(
                meta.getVersionId(), instanceId, null, metaInstanceService);
        Map<String, String> info = instance.getInfo();
        DynamicFormDetailEntity entity = FormUtils.queryFormDetailByPrimaryKey(
                info.get(INST_CURR_FORM_ID_KEY),
                info.get(INST_CURR_FORM_VERSION_KEY),
                new OperationContext(),
                this.formRepository,
                this.formPropertyRepository);

        // 在当前某些情况下，会出现插入log日志，但是不修改instance状态的情况.
        // 参考com.huawei.fit.jober.aipp.fitable.agent.AippFlowAgent.fetchAgentErrorMsgToMain
        String status = log.getLogType().equals(AippInstLogType.ERROR.name())
                ? FlowTraceStatus.ERROR.name() : info.get(AippConst.INST_STATUS_KEY);

        // 构建instanceVO，和之前返回给前端的数据结构保持一致.
        return AippInstanceVO.builder()
                .ancestors(log.getAncestors())
                .aippInstanceId(instanceId)
                .tenantId(meta.getTenant())
                .aippInstanceName(info.get(INST_NAME_KEY))
                .status(status)
                .formMetadata(entity == null ? null : entity.getData())
                .formArgs(info)
                .startTime(info.get(INST_CREATE_TIME_KEY))
                .endTime(info.getOrDefault(INST_FINISH_TIME_KEY, null))
                .aippInstanceLogs(Collections.singletonList(log))
                .build();
    }
}
