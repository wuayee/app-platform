/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.dto.aipplog;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.jane.meta.multiversion.MetaInstanceService;
import modelengine.fit.jane.meta.multiversion.instance.Instance;
import modelengine.fit.jober.aipp.constants.AippConst;
import modelengine.fit.jober.aipp.entity.AippInstLog;
import modelengine.fit.jober.aipp.enums.AippInstLogType;
import modelengine.fit.jober.aipp.enums.MetaInstStatusEnum;
import modelengine.fit.jober.aipp.util.MetaInstanceUtils;
import modelengine.fitframework.log.Logger;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
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
    private static final Logger log = Logger.get(AippInstLogDataDto.class);

    private static final int LOG_COUNT_AFTER_TZ_TOOL = 3;

    private static final HashSet<String> QUESTION_TYPE = new HashSet<>(
            Arrays.asList(AippInstLogType.QUESTION.name(), AippInstLogType.HIDDEN_QUESTION.name(),
                    AippInstLogType.QUESTION_WITH_FILE.name()));

    private String aippId;

    private String version;

    private String instanceId;

    private String status;

    private String appName;

    private String appIcon;

    private LocalDateTime createAt;

    private AippInstanceLogBody question;

    private List<AippInstanceLogBody> instanceLogBodies;

    /**
     * 从原始日志列表中获取实例历史记录
     *
     * @param rawLogs 表示日志列表的 {@link List}{@code <}{@link AippInstLog}{@code >}
     * @param context 表示操作上下文的 {@link OperationContext}
     * @param metaInstanceService 表示元数据实例服务的 {@link MetaInstanceService}
     * @return 实例历史记录
     */
    public static AippInstLogDataDto fromAippInstLogList(List<AippInstLog> rawLogs, OperationContext context,
            MetaInstanceService metaInstanceService) {
        List<AippInstLog> instanceLogs = rawLogs.stream()
                .sorted((d1, d2) -> Math.toIntExact(d1.getLogId() - d2.getLogId()))
                .collect(Collectors.toList());

        final String inAippId = instanceLogs.get(0).getAippId();
        final String inAippVersion = instanceLogs.get(0).getVersion();
        final String inInstanceId = instanceLogs.get(0).getInstanceId();
        final LocalDateTime inCreateAt = instanceLogs.get(0).getCreateAt();

        List<AippInstLogDataDto.AippInstanceLogBody> logBodies = instanceLogs.stream()
                .filter(log -> !(QUESTION_TYPE.contains(log.getLogType())))
                .map(AippInstLogDataDto::convert)
                .collect(Collectors.toList());
        AippInstLog questionInfo = instanceLogs.stream()
                .filter(log -> QUESTION_TYPE.contains(log.getLogType()))
                .findFirst()
                .orElse(null);
        String status = MetaInstStatusEnum.ARCHIVED.name();
        try {
            String metaVersionId = metaInstanceService.getMetaVersionId(inInstanceId);
            Instance instance = MetaInstanceUtils.getInstanceDetail(metaVersionId, inInstanceId, context,
                    metaInstanceService);
            status = instance.getInfo().getOrDefault(AippConst.INST_STATUS_KEY, MetaInstStatusEnum.ARCHIVED.name());
        } catch (Exception e) {
            log.error("Failed to get status through meta. [instanceId]", inInstanceId);
        }
        return new AippInstLogDataDto(inAippId, inAippVersion, inInstanceId, status, null, null, inCreateAt,
                convert(questionInfo), logBodies);
    }

    /**
     * 获取经过天舟AI提示词拼接工具处理后的历史记录
     *
     * @param rawLogs 表示日志列表的 {@link List}{@code <}{@link AippInstLog}{@code >}
     * @param context 表示操作上下文的 {@link OperationContext}
     * @param metaInstanceService 表示元数据实例服务的 {@link MetaInstanceService}
     * @return 实例历史记录
     */
    public static AippInstLogDataDto fromAippInstLogListAfterSplice(List<AippInstLog> rawLogs,
            MetaInstanceService metaInstanceService, OperationContext context) {
        List<AippInstLog> updatedLogs = rawLogs.stream()
                .filter(log -> !isUserQuestionLogBeforeTzTool(rawLogs, log))
                .collect(Collectors.toList());

        return fromAippInstLogList(updatedLogs, context, metaInstanceService);
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
        return new AippInstanceLogBody(instanceLog.getLogId(), instanceLog.getLogData(), instanceLog.getLogType(),
                instanceLog.getCreateAt(), instanceLog.getCreateUserAccount());
    }
}
