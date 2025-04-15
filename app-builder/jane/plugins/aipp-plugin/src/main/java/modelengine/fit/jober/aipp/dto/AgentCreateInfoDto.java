/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fit.jober.aipp.dto;

import lombok.Data;

/**
 * 表示用于生成智能体信息的信息，
 *
 * @author 兰宇晨
 * @since 2024-12-3
 */
@Data
public class AgentCreateInfoDto {
    /**
     * 用于生成智能体信息的描述。
     */
    private String description;
}