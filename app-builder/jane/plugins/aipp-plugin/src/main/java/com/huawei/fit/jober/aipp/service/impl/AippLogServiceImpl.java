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
import com.huawei.fit.jane.meta.multiversion.instance.Instance;
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
import com.huawei.fit.jober.aipp.mapper.AippLogMapper;
import com.huawei.fit.jober.aipp.service.AippLogService;
import com.huawei.fit.jober.aipp.service.DistributedMapService;
import com.huawei.fit.jober.aipp.service.UploadedFileManageService;
import com.huawei.fit.jober.common.RangedResultSet;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.log.Logger;
import com.huawei.fitframework.util.ObjectUtils;
import com.huawei.fitframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
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
    private final DynamicFormService dynamicFormService;
    private final MetaInstanceService metaInstanceService;
    private final DistributedMapService mapService;
    private final UploadedFileManageService uploadedFileManageService;
    private final MetaService metaService;

    public AippLogServiceImpl(AippLogMapper aippLogMapper, DynamicFormService dynamicFormService,
            MetaInstanceService metaInstanceService, DistributedMapService mapService,
            UploadedFileManageService uploadedFileManageService, MetaService metaService) {
        this.aippLogMapper = aippLogMapper;
        this.dynamicFormService = dynamicFormService;
        this.metaInstanceService = metaInstanceService;
        this.mapService = mapService;
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
     * @param aippId 指定aipp的id
     * @param version 指定aipp的版本
     * @return log数据
     */
    @Override
    public List<AippInstLogDataDto> queryAippRecentInstLog(String aippId, String version, OperationContext context) {
        String aippType = this.getAippType(aippId, version, context);
        if (version == null) {
            throw new AippParamException(context, AippErrCode.INPUT_PARAM_IS_INVALID, "version is null");
        }
        List<AippInstLogDataDto> recentLogData = this.queryAippRecentInstLog(aippId, aippType, 5, context);
        // 只对最后一个记录查询状态
        if (!recentLogData.isEmpty()) {
            AippInstLogDataDto lastLogData = recentLogData.get(recentLogData.size() - 1);
            Meta meta = MetaUtils.getAnyMeta(metaService, aippId, version, context);
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

    private String getAippType(String aippId, String version, OperationContext context) {
        Meta meta = MetaUtils.getAnyMeta(metaService, aippId, version, context);
        return ObjectUtils.cast(meta.getAttributes()
                .getOrDefault(AippConst.ATTR_AIPP_TYPE_KEY, AippTypeEnum.NORMAL.name()));
    }

    @Override
    public List<AippInstLogDataDto> queryAippRecentInstLog(String aippId, String aippType, Integer count,
            OperationContext context) {
        List<String> instanceIds =
                aippLogMapper.selectRecentInstanceId(aippId, aippType, count, context.getW3Account());
        return queryRecentLogByInstanceIds(instanceIds, context).values()
                .stream()
                .map(AippInstLogDataDto::fromAippInstLogList)
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
                .filter(log1 -> !(AippInstLogType.FORM.name().equals(log1.getLogType())
                        || AippInstLogType.HIDDEN_MSG.name().equals(log1.getLogType())))
                .collect(Collectors.toList());
    }

    /**
     * 流式查询指定aipp instance的历史记录
     *
     * @param instanceId 指定aipp instance的id
     * @param timeString 开始的时间范围, 可能为空
     * @return log数据
     */
    @Override
    public List<AippInstLog> queryInstanceLogSinceStreaming(String instanceId, String timeString) {
        List<AippInstLog> aippInstLogs = queryInstanceLogSince(instanceId, timeString);
        addCacheLog(instanceId, aippInstLogs);
        return aippInstLogs;
    }

    private void addCacheLog(String instanceId, List<AippInstLog> aippInstLogs) {
        String lastLog = getLastLog(aippInstLogs);
        addCacheLogHandle(instanceId, aippInstLogs, lastLog);
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

    private void addCacheLogHandle(String instanceId, List<AippInstLog> aippInstLogs, String lastLog) {
        Map<Object, Object> mapCache = mapService.getMapCache(instanceId);
        mapCache.forEach((key, value) -> {
            if (!checkLogValid(lastLog, (String) value)) {
                return;
            }
            AippLogData logDataCache = AippLogData.builder().msg((String) value).build();
            AippInstLog cacheLog = AippInstLog.builder()
                    .logType(AippInstLogType.MSG.name())
                    .logData(JsonUtils.toJsonString(logDataCache))
                    .instanceId(instanceId)
                    .createAt(LocalDateTime.now())
                    .build();
            aippInstLogs.add(cacheLog);
        });
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
     * @param aippId 指定aipp的id
     * @param context 登录信息
     */
    public void deleteAippInstLog(String aippId, String version, OperationContext context) {
        String aippType = this.getAippType(aippId, version, context);
        List<String> instanceIdList =
                aippLogMapper.selectNormalInstanceIdOrderByTimeDesc(aippId, aippType, context.getW3Account());
        if (!instanceIdList.isEmpty()) {
            // check最后的实例是不是还在运行
            Meta meta = MetaUtils.getAnyMeta(metaService, aippId, version, context);
            String versionId = meta.getVersionId();
            RangedResultSet<Instance> instances =
                    Utils.getInstances(versionId, instanceIdList.get(0), context, metaInstanceService);
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
                this.uploadedFileManageService.cleanAippFiles(aippId, context.getW3Account());
            }
            this.aippLogMapper.delete(aippId, version, context.getW3Account(), instanceIdExclude);
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
}
