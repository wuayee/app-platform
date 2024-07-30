/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.vo;

import com.huawei.fit.jober.aipp.dto.aipplog.AippLogCreateDto;
import com.huawei.fit.jober.aipp.enums.AippInstLogType;
import com.huawei.fit.jober.aipp.util.AippLogUtils;
import com.huawei.fitframework.util.StringUtils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;

/**
 * aipp展示类.
 *
 * @author z00559346
 * @since 2024-05-15
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AippLogVO {
    private String aippId;

    private String version;

    private String aippType;

    private String instanceId;

    private String logData;

    private String logType;

    private String path;

    // 通过msgId区分哪些日志信息是一个整体的日志;如果为null，代表当前log对象本身即是一个整体的日志.
    private String msgId;

    /**
     * 通过 {@link AippLogCreateDto} 对象创建 {@link  AippLogVO} 对象.
     *
     * @param dto {@link AippLogCreateDto} 对象.
     * @return {@link  AippLogVO} 对象.
     */
    public static AippLogVO fromCreateDto(AippLogCreateDto dto) {
        return AippLogVO.builder()
                .aippId(dto.getAippId())
                .version(dto.getVersion())
                .aippType(dto.getAippType())
                .instanceId(dto.getInstanceId())
                .logData(dto.getLogData())
                .logType(dto.getLogType())
                .path(dto.getPath())
                .build();
    }

    /**
     * 是否需要展示.
     *
     * @return true/false.true,代表需要在前端展示;false,表示不需要在前端展示.
     */
    public boolean displayable() {
        return !(StringUtils.equals(AippInstLogType.FORM.name(), this.logType)
                || StringUtils.equals(AippInstLogType.HIDDEN_MSG.name(), this.logType) || StringUtils.equals(
                AippInstLogType.HIDDEN_QUESTION.name(),
                this.logType) || StringUtils.equals(AippInstLogType.HIDDEN_FORM.name(), this.logType));
    }

    /**
     * 获取所有祖先实例的id列表.
     *
     * @return 祖先实例的id列表.
     */
    public List<String> getAncestors() {
        return Collections.singletonList(this.path.split(AippLogUtils.PATH_DELIMITER)[1]);
    }
}
