/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.service.impl;

import com.huawei.fit.dynamicform.DynamicFormService;
import com.huawei.fit.jane.common.entity.OperationContext;
import com.huawei.fit.jane.meta.multiversion.MetaInstanceService;
import com.huawei.fit.jane.meta.multiversion.MetaService;
import com.huawei.fit.jane.meta.multiversion.definition.Meta;
import com.huawei.fit.jane.meta.multiversion.definition.MetaFilter;
import com.huawei.fit.jane.meta.multiversion.instance.Instance;
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
import com.huawei.fit.jober.aipp.service.AopAippLogService;
import com.huawei.fit.jober.aipp.service.UploadedFileManageService;
import com.huawei.fit.jober.aipp.util.AippLogUtils;
import com.huawei.fit.jober.aipp.util.DataUtils;
import com.huawei.fit.jober.aipp.util.JsonUtils;
import com.huawei.fit.jober.aipp.util.MetaInstanceUtils;
import com.huawei.fit.jober.aipp.util.MetaUtils;
import com.huawei.fit.jober.common.RangedResultSet;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.util.CollectionUtils;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
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
 * @author 刘信宏
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
    private final AopAippLogService aopAippLogService;

    public AippLogServiceImpl(AippLogMapper aippLogMapper, DynamicFormService dynamicFormService,
            MetaInstanceService metaInstanceService, UploadedFileManageService uploadedFileManageService,
            MetaService metaService, AippChatMapper aippChatMapper, AopAippLogService aopAippLogService) {
        this.aippLogMapper = aippLogMapper;
        this.aippChatMapper = aippChatMapper;
        this.dynamicFormService = dynamicFormService;
        this.metaInstanceService = metaInstanceService;
        this.uploadedFileManageService = uploadedFileManageService;
        this.metaService = metaService;
        this.aopAippLogService = aopAippLogService;
    }

    private AippInstLog completeFormDataJson(AippInstLog instanceLog, OperationContext context) {
        if (AippInstLogType.FORM.name().equals(instanceLog.getLogType())) {
            AippLogData form = JsonUtils.parseObject(instanceLog.getLogData(), AippLogData.class);
            if (form == null) {
                return instanceLog;
            }
            Map<String, String> newLogData = new HashMap<String, String>() {
                {
                    put("formData", form.getFormData());
                    put("formAppearance", form.getFormAppearance());
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
     * @param type 指定aipp的类型
     * @param context 登录信息
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
                    MetaInstanceUtils.getInstances(
                            versionId, lastLogData.getInstanceId(), context, metaInstanceService);
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
        List<String> chatIds = aippChatMapper.selectChatByAppId(appId, aippType, 1);
        if (chatIds.isEmpty()) {
            return new ArrayList<>();
        }
        return this.queryChatRecentChatLog(chatIds.get(0), appId, context);
    }

    private List<AippInstLogDataDto> getAippLogWithAppInfo(List<AippInstLogDataDto> logData, String appId,
            OperationContext context) {
        // 获取被@应用的头像、名称
        List<String> originAippId = MetaUtils.getAllMetasByAppId(this.metaService, appId, context)
                .stream()
                .filter(Objects::nonNull)
                .map(Meta::getId)
                .distinct()
                .collect(Collectors.toList());
        List<String> atAippIds = logData.stream()
                .map(AippInstLogDataDto::getAippId)
                .filter(aippId -> !originAippId.contains(aippId))
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(atAippIds)) {
            return logData;
        }
        RangedResultSet<Meta> metas =
                metaService.list(this.buildAippIdFilter(atAippIds), true, 0, atAippIds.size(), context);
        if (CollectionUtils.isEmpty(metas.getResults())) {
            return logData;
        }
        Map<String, Meta> metaMap =
                metas.getResults().stream().collect(Collectors.toMap(Meta::getId, Function.identity()));
        return logData.stream()
                .peek(aippInstLogDataDto -> setLogDataWithIcon(aippInstLogDataDto, metaMap))
                .collect(Collectors.toList());
    }

    private void setLogDataWithIcon(AippInstLogDataDto aippInstLogDataDto, Map<String, Meta> metaMap) {
        if (!metaMap.containsKey(aippInstLogDataDto.getAippId())) {
            return;
        }
        Meta metaData = metaMap.get(aippInstLogDataDto.getAippId());
        Object metaIcon = metaData.getAttributes().get("meta_icon");
        if (metaIcon instanceof String) {
            aippInstLogDataDto.setAppIcon((String) metaIcon);
        }
        aippInstLogDataDto.setAppName(metaData.getName());
    }

    private MetaFilter buildAippIdFilter(List<String> aippIds) {
        MetaFilter filter = new MetaFilter();
        filter.setMetaIds(aippIds);
        return filter;
    }

    private List<AippInstLogDataDto> queryAndSortLogs(List<String> instanceIds, OperationContext context) {
        return queryRecentLogByInstanceIds(instanceIds, context).values()
                .stream()
                .filter(CollectionUtils::isNotEmpty)
                .map(AippInstLogDataDto::fromAippInstLogList)
                .sorted(Comparator.comparing(AippInstLogDataDto::getCreateAt))
                .collect(Collectors.toList());
    }

    @Override
    public List<AippInstLogDataDto> queryChatRecentChatLog(String chatId, String appId,
            OperationContext context) {
        List<String> instanceIds = aippChatMapper.selectInstanceByChat(chatId, 5);
        List<AippInstLogDataDto> logData = queryAndSortLogs(instanceIds, context);
        return this.getAippLogWithAppInfo(logData, appId, context);
    }

    @Override
    public List<AippInstLogDataDto> queryRecentLogsSinceResume(String aippId, String aippType,
            OperationContext context) {
        List<String> instanceIds = aippLogMapper.selectRecentAfterResume(aippId, aippType, context.getW3Account());
        // 该功能未上线，待测试
        return queryRecentLogByInstanceIds(instanceIds, context).values()
                .stream()
                .filter(CollectionUtils::isNotEmpty)
                .map(AippInstLogDataDto::fromAippInstLogList)
                .sorted(Comparator.comparing(AippInstLogDataDto::getCreateAt))
                .collect(Collectors.toList());
    }

    private Map<String, List<AippInstLog>> queryRecentLogByInstanceIds(List<String> instanceIds,
            OperationContext context) {
        if (instanceIds == null || instanceIds.isEmpty()) {
            return new HashMap<>();
        }
        List<String> filterLogTypes =
                new ArrayList<>(Arrays.asList(AippInstLogType.HIDDEN_MSG.name(), AippInstLogType.HIDDEN_FORM.name()));
        List<AippInstLog> aippInstLogs = this.queryBatchAndFilterFullLogsByLogType(instanceIds, filterLogTypes);
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
     * @param type 指定aipp的类型
     * @param context 登录信息
     */
    @Override
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
                    MetaInstanceUtils.getInstances(versionId, instanceId, context, this.metaInstanceService);
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
     * @param logType 日志类型
     * @param logData 日志数据
     * @param businessData 业务数据
     */
    @Override
    public String insertLog(String logType, AippLogData logData, Map<String, Object> businessData) {
        String aippId = ObjectUtils.cast(businessData.get(AippConst.BS_AIPP_ID_KEY));
        String instId = ObjectUtils.cast(businessData.get(AippConst.BS_AIPP_INST_ID_KEY));
        String parentInstId = ObjectUtils.cast(businessData.get(AippConst.PARENT_INSTANCE_ID));
        String version = ObjectUtils.cast(businessData.get(AippConst.BS_AIPP_VERSION_KEY));
        String aippType = ObjectUtils.cast(businessData.get(AippConst.ATTR_AIPP_TYPE_KEY));
        String w3Account = DataUtils.getOpContext(businessData).getW3Account();
        if (!AippLogUtils.validFormMsg(logData, logType)) {
            log.warn("invalid logData {}, logType {}, aippId {}, instId {}", logData, logType, aippId, instId);
            return null;
        }
        String path = buildPath(instId, parentInstId);
        String chatId = ObjectUtils.cast(businessData.get(AippConst.BS_CHAT_ID));
        String atChatId = ObjectUtils.cast(businessData.get(AippConst.BS_AT_CHAT_ID));
        return this.aopAippLogService
                .insertLog(AippLogCreateDto.builder()
                .aippId(aippId).version(version)
                .aippType(aippType).instanceId(instId)
                .logType(logType).logData(JsonUtils.toJsonString(logData))
                .createUserAccount(w3Account).path(path)
                .chatId(chatId).atChatId(atChatId).build());
    }

    /**
     * 插入MSG类型的历史记录
     *
     * @param msg MSG日志内容
     * @param flowData 流程执行上下文数据。
     */
    @Override
    public void insertMsgLog(String msg, List<Map<String, Object>> flowData) {
        AippLogData logData = AippLogData.builder().msg(msg).build();
        insertLog(AippInstLogType.MSG.name(), logData, DataUtils.getBusiness(flowData));
    }

    /**
     * 插入ERROR类型的历史记录
     *
     * @param msg ERROR日志内容
     * @param flowData 流程执行上下文数据。
     */
    @Override
    public void insertErrorLog(String msg, List<Map<String, Object>> flowData) {
        AippLogData logData = AippLogData.builder().msg(msg).build();
        insertLog(AippInstLogType.ERROR.name(), logData, DataUtils.getBusiness(flowData));
    }

    /**
     * 更新指定log id的记录
     *
     * @param logId 需要更新的记录的id
     * @param newLogData 新的记录数据
     * @throws IllegalArgumentException 如果logId为空，则抛出此异常
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

    /**
     * 根据父Instance的路径构建当前Instance的路径。
     *
     * @param instId 表示当前instance的id的 {@link String}。
     * @param parentInstId 表示父instance的id的 {@link String}。
     * @return 表示当前instId的路径的 {@link String}。
     */
    @Override
    public String buildPath(String instId, String parentInstId) {
        String path;
        if (parentInstId == null) {
            path = AippLogUtils.PATH_DELIMITER + instId;
        } else {
            String parentPath = getParentPath(parentInstId);
            path = StringUtils.isEmpty(parentPath)
                    ? AippLogUtils.PATH_DELIMITER + instId
                    : String.join(AippLogUtils.PATH_DELIMITER, parentPath, instId);
        }
        return path;
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
        // 该功能未上线，待测试
        return queryRecentLogByInstanceIds(instanceIds, context).values()
            .stream()
            .filter(CollectionUtils::isNotEmpty)
            .map(AippInstLogDataDto::fromAippInstLogListAfterSplice)
            .sorted(Comparator.comparing(AippInstLogDataDto::getCreateAt))
            .collect(Collectors.toList());
    }

    @Override
    public List<AippInstLog> queryBatchAndFilterFullLogsByLogType(List<String> instanceIds,
            List<String> filterLogTypes) {
        if (CollectionUtils.isEmpty(instanceIds)) {
            log.error("Instance id list is null or empty.");
            throw new AippParamException(AippErrCode.INPUT_PARAM_IS_INVALID);
        }
        return this.aippLogMapper.getFullLogsByInstanceIds(instanceIds)
                .stream()
                .filter(log -> !filterLogTypes.contains(log.getLogType()))
                .collect(Collectors.toList());
    }

    @Override
    public List<AippInstLog> queryAndFilterLogsByLogType(String instanceId, List<String> filterLogTypes) {
        if (StringUtils.isEmpty(instanceId)) {
            log.error("Instance id is null or empty.");
            throw new AippParamException(AippErrCode.INPUT_PARAM_IS_INVALID);
        }
        return this.aippLogMapper.getLogsByInstanceId(instanceId)
                .stream()
                .filter(log -> !filterLogTypes.contains(log.getLogType()))
                .collect(Collectors.toList());
    }

    @Override
    public List<AippInstLog> queryLogsByInstanceIdAndLogTypes(String instanceId, List<String> logTypes) {
        if (StringUtils.isEmpty(instanceId)) {
            log.error("When queryLogsByInstanceIdAndLogTypes input instance id is empty.");
            throw new AippParamException(AippErrCode.INPUT_PARAM_IS_INVALID);
        }
        return this.aippLogMapper.getLogsByInstanceIdAndLogTypes(instanceId, logTypes);
    }

    @Override
    public void deleteLogs(List<Long> logIds) {
        if (CollectionUtils.isEmpty(logIds)) {
            log.error("logIds is null or empty.");
            return;
        }
        this.aippLogMapper.deleteInstanceLogs(logIds);
    }
}
