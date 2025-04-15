/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package modelengine.fit.jober.aipp.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import modelengine.fitframework.annotation.Property;

import java.util.Map;

/**
 * 对话VO
 *
 * @author 姚江
 * @since 2024-07-27
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageVo {
    private String data;

    private String type;

    @Property(name = "message_id")
    private String messageId;

    private String status;

    private Map<String, Object> extension;
}
