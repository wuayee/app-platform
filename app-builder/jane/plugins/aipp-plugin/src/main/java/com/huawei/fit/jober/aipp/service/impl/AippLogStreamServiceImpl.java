/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.service.impl;

import com.huawei.fit.jane.meta.multiversion.MetaInstanceService;
import com.huawei.fit.jane.meta.multiversion.MetaService;
import com.huawei.fit.jane.meta.multiversion.definition.Meta;
import com.huawei.fit.jane.meta.multiversion.instance.Instance;
import com.huawei.fit.jober.aipp.constants.AippConst;
import com.huawei.fit.jober.aipp.dto.chat.AppChatRsp;
import com.huawei.fit.jober.aipp.enums.AippInstLogType;
import com.huawei.fit.jober.aipp.service.AippLogStreamService;
import com.huawei.fit.jober.aipp.service.AppChatSseService;
import com.huawei.fit.jober.aipp.util.JsonUtils;
import com.huawei.fit.jober.aipp.util.MetaInstanceUtils;
import com.huawei.fit.jober.aipp.vo.AippLogVO;
import com.huawei.fit.waterflow.domain.enums.FlowTraceStatus;
import com.huawei.fitframework.annotation.Component;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * log流式服务实现，单进程实现方案.
 *
 * @author 张越
 * @since 2024-05-23
 */
@Component
public class AippLogStreamServiceImpl implements AippLogStreamService {
    private final MetaService metaService;
    private final MetaInstanceService metaInstanceService;
    private final AppChatSseService appChatSseService;

    public AippLogStreamServiceImpl(MetaService metaService, MetaInstanceService metaInstanceService,
            AppChatSseService appChatSseService) {
        this.metaService = metaService;
        this.metaInstanceService = metaInstanceService;
        this.appChatSseService = appChatSseService;
    }

    @Override
    public void send(AippLogVO log) {
        if (!log.displayable()) {
            return;
        }
        List<String> ancestors = log.getAncestors();
        Collections.reverse(ancestors);
        Optional<String> ancestorOpt =
                ancestors.stream().filter(anc -> this.appChatSseService.getEmitter(anc).isPresent()).findFirst();
        ancestorOpt.ifPresent(id -> {
            AppChatRsp appChatRsp = this.buildData(log);
            if (!appChatRsp.getStatus().equalsIgnoreCase(FlowTraceStatus.RUNNING.name()) && !appChatRsp.getStatus()
                    .equalsIgnoreCase(FlowTraceStatus.READY.name())) {
                this.appChatSseService.sendLastData(id, appChatRsp);
            } else {
                this.appChatSseService.send(id, appChatRsp);
            }
        });
    }

    private AppChatRsp buildData(AippLogVO log) {
        String instanceId = log.getInstanceId();
        String metaVersionId = this.metaInstanceService.getMetaVersionId(instanceId);
        Meta meta = this.metaService.retrieve(metaVersionId, null);
        Instance instance =
                MetaInstanceUtils.getInstanceDetail(meta.getVersionId(), instanceId, null, metaInstanceService);

        // 在当前某些情况下，会出现插入log日志，但是不修改instance状态的情况.
        // 参考com.huawei.fit.jober.aipp.fitable.agent.AippFlowAgent.fetchAgentErrorMsgToMain
        String status = log.getLogType().equals(AippInstLogType.ERROR.name())
                ? FlowTraceStatus.ERROR.name()
                : instance.getInfo().get(AippConst.INST_STATUS_KEY);

        AppChatRsp.Answer answer = this.buildAnswer(log);
        return AppChatRsp.builder()
                .chatId(log.getChatId())
                .atChatId(log.getAtChatId())
                .status(status)
                .instanceId(instanceId)
                .answer(Collections.singletonList(answer))
                .logId(log.getLogId())
                .build();
    }

    private AppChatRsp.Answer buildAnswer(AippLogVO log) {
        AppChatRsp.Answer.AnswerBuilder builder =
                AppChatRsp.Answer.builder().type(log.getLogType()).msgId(log.getMsgId());
        if (log.getLogType().equals(AippInstLogType.MSG.name()) || log.getLogType()
                .equalsIgnoreCase(AippInstLogType.ERROR.name())) {
            builder.content(JsonUtils.parseObject(log.getLogData()).get("msg"));
        } else if (JsonUtils.isValidJson(log.getLogData())) {
            builder.content(JsonUtils.parseObject(log.getLogData()));
        } else {
            builder.content(log.getLogData());
        }
        return builder.build();
    }
}
