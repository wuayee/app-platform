/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2025-2025. All rights reserved.
 */

package modelengine.fit.jober.aipp.dto.chat;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

/**
 * chat 创建参数类.
 *
 * @author 张越
 * @since 2025-01-14
 */
@Data
@Builder
public class ChatCreateEntity {
    private String appId;
    private String appVersion;
    private Map<String, String> attributes;
    private String chatName;
    private String chatId;
    private String taskInstanceId;
}
