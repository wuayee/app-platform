package com.huawei.fit.jober.aipp.service.impl;

import static com.huawei.fit.jober.aipp.constants.AippConst.INST_CREATE_TIME_KEY;
import static com.huawei.fit.jober.aipp.constants.AippConst.INST_CURR_FORM_ID_KEY;
import static com.huawei.fit.jober.aipp.constants.AippConst.INST_CURR_FORM_VERSION_KEY;
import static com.huawei.fit.jober.aipp.constants.AippConst.INST_FINISH_TIME_KEY;
import static com.huawei.fit.jober.aipp.constants.AippConst.INST_NAME_KEY;

import com.huawei.fit.dynamicform.entity.DynamicFormDetailEntity;
import com.huawei.fit.http.websocket.Session;
import com.huawei.fit.jane.common.entity.OperationContext;
import com.huawei.fit.jane.meta.multiversion.MetaInstanceService;
import com.huawei.fit.jane.meta.multiversion.MetaService;
import com.huawei.fit.jane.meta.multiversion.definition.Meta;
import com.huawei.fit.jane.meta.multiversion.instance.Instance;
import com.huawei.fit.jober.aipp.common.JsonUtils;
import com.huawei.fit.jober.aipp.common.Utils;
import com.huawei.fit.jober.aipp.constants.AippConst;
import com.huawei.fit.jober.aipp.repository.AppBuilderFormPropertyRepository;
import com.huawei.fit.jober.aipp.repository.AppBuilderFormRepository;
import com.huawei.fit.jober.aipp.service.AippLogStreamService;
import com.huawei.fit.jober.aipp.vo.AippInstanceVO;
import com.huawei.fit.jober.aipp.vo.AippLogVO;
import com.huawei.fitframework.annotation.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * AippLog流式服务实现，单进程实现方案.
 *
 * @author z00559346 张越
 * @since 2024-05-14
 */
@Component
public class AippLogStreamLocalService implements AippLogStreamService {
    private final Map<String, Session> sessions = new ConcurrentHashMap<>();
    private final MetaService metaService;
    private final MetaInstanceService metaInstanceService;
    private final AppBuilderFormRepository formRepository;
    private final AppBuilderFormPropertyRepository formPropertyRepository;

    public AippLogStreamLocalService(MetaService metaService, MetaInstanceService metaInstanceService,
            AppBuilderFormRepository formRepository, AppBuilderFormPropertyRepository formPropertyRepository) {
        this.metaService = metaService;
        this.metaInstanceService = metaInstanceService;
        this.formRepository = formRepository;
        this.formPropertyRepository = formPropertyRepository;
    }

    @Override
    public void addSession(String instanceId, Session session) {
        this.sessions.put(instanceId, session);
    }

    @Override
    public void removeSession(Session session) {
        this.sessions.values().removeIf(s -> s.getId().equals(session.getId()));
    }

    @Override
    public Optional<Session> getSession(String instanceId) {
        return Optional.ofNullable(this.sessions.get(instanceId));
    }

    @Override
    public void send(AippLogVO log) {
        if (!log.displayable()) {
            return;
        }
        List<String> ancestors = log.getAncestors();
        Collections.reverse(ancestors);
        Optional<Session> sessionOptional = ancestors.stream()
                .map(this::getSession)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst();
        sessionOptional.ifPresent(session -> session.send(JsonUtils.toJsonString(this.buildData(log))));
    }

    private AippInstanceVO buildData(AippLogVO log) {
        String instanceId = log.getInstanceId();
        String metaVersionId = this.metaInstanceService.getMetaVersionId(instanceId);
        Meta meta = this.metaService.retrieve(metaVersionId, null);
        Instance instance = Utils.getInstanceDetail(meta.getVersionId(), instanceId, null, metaInstanceService);
        Map<String, String> info = instance.getInfo();
        DynamicFormDetailEntity entity = Utils.queryFormDetailByPrimaryKey(
                info.get(INST_CURR_FORM_ID_KEY),
                info.get(INST_CURR_FORM_VERSION_KEY),
                new OperationContext(),
                this.formRepository,
                this.formPropertyRepository);

        // 构建instanceVO，和之前返回给前端的数据结构保持一致.
        return AippInstanceVO.builder()
                .ancestors(log.getAncestors())
                .aippInstanceId(instanceId)
                .tenantId(meta.getTenant())
                .aippInstanceName(info.get(INST_NAME_KEY))
                .status(info.get(AippConst.INST_STATUS_KEY))
                .formMetadata(entity == null ? null : entity.getData())
                .formArgs(info)
                .startTime(info.get(INST_CREATE_TIME_KEY))
                .endTime(info.getOrDefault(INST_FINISH_TIME_KEY, null))
                .aippInstanceLogs(Collections.singletonList(log))
                .build();
    }
}
