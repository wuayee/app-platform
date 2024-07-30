/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.dto.aipplog;

import com.huawei.fitframework.annotation.Property;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;
import java.util.stream.Stream;

/**
 * 创建aipp实例历史记录参数
 *
 * @author l00611472
 * @since 2024-01-08
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AippLogCreateDto {
    @Property(description = "aipp id")
    private String aippId;

    @Property(description = "aipp version")
    private String version;

    @Property(description = "aipp type(normal/preview)")
    private String aippType;

    @Property(description = "aipp实例id")
    private String instanceId;

    @Property(description = "aipp实例历史数据")
    private String logData;

    @Property(description = "历史数据类型 {@link AippInstLogType}")
    private String logType;

    @Property(description = "创建人w3账号")
    private String createUserAccount;

    @Property(description = "log path")
    private String path;

    @Property(description = "chat id")
    private String chatId;

    @Property(description = "at chat id")
    private String atChatId;

    /**
     * 判断所有字段是否为空。
     *
     * @return 表示所有字段是否为空的 {@code boolean}。
     */
    public boolean allFieldsNotNull() {
        return Stream.of(aippId, version, aippType, instanceId, logData, logType, createUserAccount, path)
                .allMatch(Objects::nonNull);
    }
}
