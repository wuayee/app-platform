/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fit.actuator.entity;

import lombok.Data;

/**
 * 表示连接端点的信息。
 *
 * @author 季聿阶
 * @since 2024-07-05
 */
@Data
public class EndpointVo {
    private String protocol;
    private int code;
    private int port;
}
