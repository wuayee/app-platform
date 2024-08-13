/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.dto.aipplog;

import com.huawei.fit.jober.aipp.entity.AippInstLog;
import com.huawei.fit.jober.aipp.enums.AippInstLogType;
import com.huawei.fit.jober.aipp.enums.MetaInstStatusEnum;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * aipp实例历史记录数据
 *
 * @author 刘信宏
 * @since 2024-01-08
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class AippInstLogDataDto {
    private static final int LOG_COUNT_AFTER_TZ_TOOL = 3;

    private String aippId;
    private String version;
    private String instanceId;
    private String status;
    private String appName;
    private String appIcon;
    private AippInstanceLogBody question;
    private List<AippInstanceLogBody> instanceLogBodies;

    /**
     * 从原始日志列表中获取实例历史记录
     *
     * @param rawLogs 日志列表
     * @return 实例历史记录
     */
    public static AippInstLogDataDto fromAippInstLogList(List<AippInstLog> rawLogs) {
        List<AippInstLog> instanceLogs = rawLogs.stream()
                .sorted((d1, d2) -> Math.toIntExact(d1.getLogId() - d2.getLogId()))
                .collect(Collectors.toList());

        final String inAippId = instanceLogs.get(0).getAippId();
        final String inAippVersion = instanceLogs.get(0).getVersion();
        final String inInstanceId = instanceLogs.get(0).getInstanceId();

        List<AippInstLogDataDto.AippInstanceLogBody> logBodies = instanceLogs.stream()
                .filter(log -> !(log.getLogType().equals(AippInstLogType.QUESTION.name())
                        || log.getLogType().equals(AippInstLogType.HIDDEN_QUESTION.name())))
                .map(AippInstLogDataDto::convert)
                .collect(Collectors.toList());
        AippInstLog questionInfo = instanceLogs.stream()
                .filter(log -> (log.getLogType().equals(AippInstLogType.QUESTION.name())
                        || log.getLogType().equals(AippInstLogType.HIDDEN_QUESTION.name())))
                .findFirst().orElse(null);
        return new AippInstLogDataDto(inAippId, inAippVersion, inInstanceId, MetaInstStatusEnum.ARCHIVED.name(),
                null, null, convert(questionInfo), logBodies);
    }

    /**
     * 获取经过天舟AI提示词拼接工具处理后的历史记录
     *
     * @param rawLogs 日志列表
     * @return 实例历史记录
     */
    public static AippInstLogDataDto fromAippInstLogListAfterSplice(List<AippInstLog> rawLogs) {
        List<AippInstLog> updatedLogs = rawLogs.stream()
            .filter(log -> !isUserQuestionLogBeforeTzTool(rawLogs, log))
            .collect(Collectors.toList());

        return fromAippInstLogList(updatedLogs);
    }

    private static boolean isUserQuestionLogBeforeTzTool(List<AippInstLog> rawLogs, AippInstLog log) {
        return rawLogs.size() == LOG_COUNT_AFTER_TZ_TOOL && AippInstLogType.QUESTION.name().equals(log.getLogType());
    }

    /**
     * 转换实例日志为实例日志体
     */
    @AllArgsConstructor
    @Getter
    public static class AippInstanceLogBody {
        private long logId;
        private String logData;
        private String logType;
        private LocalDateTime createAt;
        private String createUserAccount;
    }

    static AippInstLogDataDto.AippInstanceLogBody convert(AippInstLog instanceLog) {
        if (instanceLog == null) {
            return null;
        }
        return new AippInstanceLogBody(instanceLog.getLogId(),
            instanceLog.getLogData(),
            instanceLog.getLogType(),
            instanceLog.getCreateAt(),
            instanceLog.getCreateUserAccount());
    }
}
