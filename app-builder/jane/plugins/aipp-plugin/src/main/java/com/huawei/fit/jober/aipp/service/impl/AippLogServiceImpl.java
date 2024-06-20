/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.service.impl;

import com.huawei.fit.dynamicform.DynamicFormService;
import com.huawei.fit.dynamicform.entity.DynamicFormDetailEntity;
import com.huawei.fit.jane.common.entity.OperationContext;
import com.huawei.fit.jane.meta.multiversion.MetaInstanceService;
import com.huawei.fit.jane.meta.multiversion.MetaService;
import com.huawei.fit.jane.meta.multiversion.definition.Meta;
import com.huawei.fit.jane.meta.multiversion.definition.MetaFilter;
import com.huawei.fit.jane.meta.multiversion.instance.Instance;
import com.huawei.fit.jober.aipp.aop.AippLogInsert;
import com.huawei.fit.jober.aipp.common.JsonUtils;
import com.huawei.fit.jober.aipp.common.MetaUtils;
import com.huawei.fit.jober.aipp.common.Utils;
import com.huawei.fit.jober.aipp.common.exception.AippErrCode;
import com.huawei.fit.jober.aipp.common.exception.AippParamException;
import com.huawei.fit.jober.aipp.constants.AippConst;
import com.huawei.fit.jober.aipp.dto.aipplog.AippInstLogDataDto;
import com.huawei.fit.jober.aipp.dto.aipplog.AippLogCreateDto;
import com.huawei.fit.jober.aipp.dto.aipplog.AippLogQueryCondition;
import com.huawei.fit.jober.aipp.entity.AippInstLog;
import com.huawei.fit.jober.aipp.entity.AippLogData;
import com.huawei.fit.jober.aipp.enums.AippInstLogType;
import com.huawei.fit.jober.aipp.enums.AippTypeEnum;
import com.huawei.fit.jober.aipp.enums.MetaInstStatusEnum;
import com.huawei.fit.jober.aipp.mapper.AippChatMapper;
import com.huawei.fit.jober.aipp.mapper.AippLogMapper;
import com.huawei.fit.jober.aipp.service.AippLogService;
import com.huawei.fit.jober.aipp.service.UploadedFileManageService;
import com.huawei.fit.jober.common.RangedResultSet;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.log.Logger;
import com.huawei.fitframework.util.StringUtils;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * aipp实例历史记录服务接口实现
 *
 * @author l00611472
 * @since 2024-01-08
 */
@Component
public class AippLogServiceImpl implements AippLogService {
    private static final Logger log = Logger.get(AippLogServiceImpl.class);
    private final AippLogMapper aippLogMapper;
    private final AippChatMapper aippChatMapper;
    private final DynamicFormService dynamicFormService;
    private final MetaInstanceService metaInstanceService;
    private final UploadedFileManageService uploadedFileManageService;
    private final MetaService metaService;

    public AippLogServiceImpl(AippLogMapper aippLogMapper, DynamicFormService dynamicFormService,
            MetaInstanceService metaInstanceService, UploadedFileManageService uploadedFileManageService,
            MetaService metaService, AippChatMapper aippChatMapper) {
        this.aippLogMapper = aippLogMapper;
        this.aippChatMapper = aippChatMapper;
        this.dynamicFormService = dynamicFormService;
        this.metaInstanceService = metaInstanceService;
        this.uploadedFileManageService = uploadedFileManageService;
        this.metaService = metaService;
    }

    private AippInstLog completeFormDataJson(AippInstLog instanceLog, OperationContext context) {
        if (AippInstLogType.FORM.name().equals(instanceLog.getLogType())) {
            AippLogData form = JsonUtils.parseObject(instanceLog.getLogData(), AippLogData.class);
            DynamicFormDetailEntity formEntity =
                    dynamicFormService.queryFormDetailByPrimaryKey(form.getFormId(), form.getFormVersion(), context);
            String formData = formEntity == null ? "" : formEntity.getData();
            Map<String, String> newLogData = new HashMap<String, String>() {
                {
                    put("form_args", form.getFormArgs());
                    put("form_data", formData);
                }
            };
            instanceLog.setLogData(JsonUtils.toJsonString(newLogData));
        }
        return instanceLog;
    }

    /**
     * 查询指定aipp最近5个的历史记录
     *
     * @param appId 指定aipp的id
     * @return log数据
     */
    @Override
    public List<AippInstLogDataDto> queryAippRecentInstLog(String appId, String type, OperationContext context) {
        String aippType = AippTypeEnum.getType(type).type();
        List<String> metaIds = getMetaIds(appId, context, aippType);
        if (metaIds.isEmpty()) {
            return Collections.emptyList();
        }
        List<AippInstLogDataDto> recentLogData = this.queryAippRecentInstLog(metaIds, aippType, 5, context);
        // 只对最后一个记录查询状态
        if (!recentLogData.isEmpty()) {
            AippInstLogDataDto lastLogData = recentLogData.get(recentLogData.size() - 1);
            Meta meta = MetaUtils.getAnyMeta(metaService, lastLogData.getAippId(), lastLogData.getVersion(), context);
            if (meta == null) {
                return Collections.emptyList();
            }
            String versionId = meta.getVersionId();
            RangedResultSet<Instance> instances =
                    Utils.getInstances(versionId, lastLogData.getInstanceId(), context, metaInstanceService);
            if (instances.getRange().getTotal() == 0) {
                return Collections.emptyList();
            }
            String lastLogStatus = instances.getResults()
                    .get(0)
                    .getInfo()
                    .getOrDefault(AippConst.INST_STATUS_KEY, MetaInstStatusEnum.RUNNING.name());
            lastLogData.setStatus(lastLogStatus);
        }
        return recentLogData;
    }

    @NotNull
    private List<String> getMetaIds(String appId, OperationContext context, String aippType) {
        return MetaUtils.getAllMetasByAppId(this.metaService, appId, aippType, context)
                .stream()
                .filter(Objects::nonNull)
                .map(Meta::getId)
                .distinct()
                .collect(Collectors.toList());
    }

    private List<AippInstLogDataDto> queryAippRecentInstLog(List<String> aippIds, String aippType, Integer count,
            OperationContext context) {
        List<String> instanceIds =
                this.aippLogMapper.selectRecentInstanceIdByAippIds(aippIds, aippType, count, context.getW3Account());
        return this.queryAndSortLogs(instanceIds, context);
    }

    @Override
    public List<AippInstLogDataDto> queryAippRecentInstLog(String aippId, String aippType, Integer count,
            OperationContext context) {
        List<String> instanceIds =
                aippLogMapper.selectRecentInstanceId(aippId, aippType, count, context.getW3Account());
        return this.queryAndSortLogs(instanceIds, context);
    }

    @Override
    public List<AippInstLogDataDto> queryChatRecentInstLog(String aippId, String aippType, Integer count,
                                                           OperationContext context, String chatId) {
        List<String> instanceIds =
                aippChatMapper.selectInstanceByChat(chatId, count);
        return this.queryAndSortLogs(instanceIds, context);
    }

    @Override
    public List<AippInstLogDataDto> queryAppRecentChatLog(String appId, String aippType, OperationContext context) {
        List<String> chatIds = aippChatMapper.selectChatByAppId(appId, 1);
        List<AippInstLogDataDto> logData = new ArrayList<>();
        if (chatIds.isEmpty()) {
            return logData;
        }
        List<String> instanceIds = aippChatMapper.selectInstanceByChat(chatIds.get(0), 5);
        logData = queryAndSortLogs(instanceIds, context);
        return this.getAippLogWithAppInfo(logData, aippType, appId, context);
    }

    private List<AippInstLogDataDto> getAippLogWithAppInfo(
                List<AippInstLogDataDto> logData, String aippType, String appId, OperationContext context) {
        // 获取被@应用的头像、名称
        String type = AippTypeEnum.getType(aippType).type();
        List<String> originAippId = getMetaIds(appId, context, type);
        List<String> atAippIds = logData.stream()
                .filter(data -> !Objects.equals(data.getAippId(), originAippId.get(0)))
                .map(AippInstLogDataDto::getAippId)
                .collect(Collectors.toList());
        RangedResultSet<Meta> metas =
                metaService.list(this.buildAippIdFilter(atAippIds), true, 0, atAippIds.size(), context);
        if (!metas.getResults().isEmpty()) {
            List<Meta> meta = metas.getResults();
            Map<String, Meta> metaMap = meta.stream().collect(Collectors.toMap(Meta::getId, Function.identity()));
            logData.stream().forEach(aippInstLogDataDto -> setLogDataWithIcon(aippInstLogDataDto, metaMap));
        }
        return logData;
    }

    private void setLogDataWithIcon(AippInstLogDataDto aippInstLogDataDto, Map<String, Meta> metaMap) {
        if (!metaMap.containsKey(aippInstLogDataDto.getAippId())) {
            return;
        }
        Meta metaData = metaMap.get(aippInstLogDataDto.getAippId());
        aippInstLogDataDto.setAppName(metaData.getName());
        Object metaIcon = metaData.getAttributes().get("meta_icon");
        if (metaIcon instanceof String) {
            aippInstLogDataDto.setAppIcon((String) metaIcon);
        }
    }

    private MetaFilter buildAippIdFilter(List<String> aippIds) {
        MetaFilter filter = new MetaFilter();
        filter.setMetaIds(aippIds);
        return filter;
    }

    @NotNull
    private List<AippInstLogDataDto> queryAndSortLogs(List<String> instanceIds, OperationContext context) {
        return queryRecentLogByInstanceIds(instanceIds, context).values()
                .stream()
                .map(AippInstLogDataDto::fromAippInstLogList)
                .filter(dto -> dto.getQuestion() != null)
                .sorted((d1, d2) -> Math.toIntExact(d1.getQuestion().getLogId() - d2.getQuestion().getLogId()))
                .collect(Collectors.toList());
    }

    @Override
    public List<AippInstLogDataDto> queryRecentLogsSinceResume(String aippId, String aippType,
            OperationContext context) {
        List<String> instanceIds = aippLogMapper.selectRecentAfterResume(aippId, aippType, context.getW3Account());
        return queryRecentLogByInstanceIds(instanceIds, context).values()
                .stream()
                .map(AippInstLogDataDto::fromAippInstLogList)
                .sorted((d1, d2) -> Math.toIntExact(d1.getQuestion().getLogId() - d2.getQuestion().getLogId()))
                .collect(Collectors.toList());
    }

    private Map<String, List<AippInstLog>> queryRecentLogByInstanceIds(List<String> instanceIds,
            OperationContext context) {
        if (instanceIds == null || instanceIds.isEmpty()) {
            return new HashMap<>();
        }
        List<AippInstLog> aippInstLogs = aippLogMapper.getLogsByInstanceId(instanceIds);
        Map<String, List<AippInstLog>> result =
                instanceIds.stream().collect(Collectors.toMap(key -> key, key -> new ArrayList<>()));
        for (AippInstLog instLog : aippInstLogs) {
            AippInstLog newInstLog = completeFormDataJson(instLog, context);
            for (String key : instanceIds) {
                if (instLog.getPath().contains(key)) {
                    result.get(key).add(newInstLog);
                }
            }
        }
        return result;
    }

    private LocalDateTime getLocalDateTime(String timeString) {
        LocalDateTime sinceTime = null;
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.ROOT);
            sinceTime = LocalDateTime.parse(timeString, formatter);
        } catch (DateTimeParseException e) {
            log.error("queryInstanceSince invalid after_at timestamp: {}, error: {}", timeString, e.getMessage());
        }
        return sinceTime;
    }

    /**
     * 查询指定aipp instance的历史记录, 可选开始时间
     *
     * @param instanceId 指定aipp instance的id
     * @param timeString 开始的时间范围, 可能为空
     * @return log数据
     */
    @Override
    public List<AippInstLog> queryInstanceLogSince(String instanceId, String timeString) {
        LocalDateTime sinceTime = Optional.ofNullable(timeString).map(this::getLocalDateTime).orElse(null);
        AippLogQueryCondition sqlCondition =
                AippLogQueryCondition.builder().instanceId(instanceId).afterAt(sinceTime).build();
        return aippLogMapper.selectWithCondition(sqlCondition)
                .stream()
                .filter(AippLogServiceImpl::isNeededLog)
                .collect(Collectors.toList());
    }

    private static boolean isNeededLog(AippInstLog l) {
        return AippInstLogType.QUESTION.name().equals(l.getLogType())
                || AippInstLogType.MSG.name().equals(l.getLogType())
                || AippInstLogType.FILE.name().equals(l.getLogType())
                || AippInstLogType.ERROR.name().equals(l.getLogType());
    }

    private boolean checkLogValid(String lastLog, String cacheLog) {
        if (cacheLog == null) {
            return false;
        }
        if (lastLog == null) {
            return true;
        }
        if (lastLog.length() != cacheLog.length()) {
            return true;
        }
        return !lastLog.equals(cacheLog);
    }

    private String getLastLog(List<AippInstLog> aippInstLogs) {
        if (aippInstLogs.isEmpty()) {
            return null;
        }
        AippInstLog endLog = aippInstLogs.get(aippInstLogs.size() - 1);
        if (!AippInstLogType.MSG.name().equals(endLog.getLogType())) {
            return null;
        }
        AippLogData logData = JsonUtils.parseObject(endLog.getLogData(), AippLogData.class);
        return logData.getMsg();
    }

    /**
     * 查询指定aipp instance的form类型的最新一条历史记录
     *
     * @param instanceId 指定aipp instance的id
     * @return log数据
     */
    @Override
    public AippInstLog queryLastInstanceFormLog(String instanceId) {
        if (StringUtils.isBlank(instanceId)) {
            log.error("instanceId is null");
            throw new AippParamException(AippErrCode.INPUT_PARAM_IS_INVALID);
        }
        return aippLogMapper.selectLastInstanceFormLog(instanceId);
    }

    /**
     * 删除指定aipp的历史记录
     *
     * @param appId 指定aipp的id
     * @param context 登录信息
     */
    public void deleteAippInstLog(String appId, String type, OperationContext context) {
        String aippType = AippTypeEnum.getType(type).type();
        List<String> metaIds = getMetaIds(appId, context, aippType);
        if (metaIds.isEmpty()) {
            return;
        }
        List<String> instanceIdList =
                this.aippLogMapper.selectNormalInstanceIdOrderByTimeDesc(metaIds, aippType, context.getW3Account());
        if (!instanceIdList.isEmpty()) {
            // check最后的实例是不是还在运行
            String instanceId = instanceIdList.get(0);
            String versionId = this.metaInstanceService.getMetaVersionId(instanceId);
            RangedResultSet<Instance> instances =
                    Utils.getInstances(versionId, instanceId, context, this.metaInstanceService);
            if (instances.getRange().getTotal() == 0) {
                return;
            }
            String lastLogStatus = instances.getResults()
                    .get(0)
                    .getInfo()
                    .getOrDefault(AippConst.INST_STATUS_KEY, MetaInstStatusEnum.RUNNING.name());
            String instanceIdExclude = null;
            if (lastLogStatus.equals(MetaInstStatusEnum.RUNNING.name())) {
                instanceIdExclude = instanceIdList.get(0);
            } else {
                this.uploadedFileManageService.cleanAippFiles(metaIds, context.getW3Account());
            }
            this.aippLogMapper.delete(metaIds, aippType, context.getW3Account(), instanceIdExclude);
        }
    }

    /**
     * 删除指定aipp预览的历史记录
     *
     * @param previewAippId 指定aipp的id
     * @param context 登录信息
     */
    @Override
    public void deleteAippPreviewLog(String previewAippId, OperationContext context) {
        this.aippLogMapper.deleteByType(previewAippId, AippTypeEnum.PREVIEW.name(), context.getW3Account(), null);
    }

    /**
     * 插入aipp的历史记录
     *
     * @param logDto 插入数据
     */
    @Override
    @AippLogInsert
    public void insertLog(AippLogCreateDto logDto) throws IllegalArgumentException {
        if (logDto.allFieldsNotNull()) {
            aippLogMapper.insertOne(logDto);
            return;
        }
        log.error("null field exists in req {}", logDto);
        // 待各个参数独立校验
        throw new AippParamException(AippErrCode.UNKNOWN);
    }

    /**
     * 更新指定log id的记录
     *
     * @param logId 指定log的id
     * @param newLogData 新的log data
     */
    @Override
    public void updateLog(Long logId, String newLogData) throws IllegalArgumentException {
        if (logId == null) {
            log.error("logId is null");
            throw new AippParamException(AippErrCode.INPUT_PARAM_IS_INVALID);
        }
        aippLogMapper.updateOne(logId, newLogData);
    }

    @Override
    public String getParentPath(String parentInstId) {
        if (parentInstId == null) {
            return "";
        }
        return aippLogMapper.getParentPath(parentInstId);
    }

    @Override
    public void deleteInstanceLog(String instanceId) {
        if (StringUtils.isEmpty(instanceId)) {
            log.error("Instance id is null or empty.");
            throw new AippParamException(AippErrCode.INPUT_PARAM_IS_INVALID);
        }
        this.aippLogMapper.deleteInstanceLog(instanceId);
    }

    @Override
    public List<AippInstLogDataDto> queryAippRecentInstLogAfterSplice(String aippId, String aippType, Integer count,
        OperationContext context) {
        List<String> instanceIds =
            aippLogMapper.selectRecentInstanceId(aippId, aippType, count, context.getW3Account());
        return queryRecentLogByInstanceIds(instanceIds, context).values()
            .stream()
            .map(AippInstLogDataDto::fromAippInstLogListAfterSplice)
            .filter(dto -> dto.getQuestion() != null)
            .sorted((d1, d2) -> Math.toIntExact(d1.getQuestion().getLogId() - d2.getQuestion().getLogId()))
            .collect(Collectors.toList());
    }
}
