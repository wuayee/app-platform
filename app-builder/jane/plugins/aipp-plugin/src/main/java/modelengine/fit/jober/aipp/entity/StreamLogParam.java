/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fit.jober.aipp.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 流式日志的参数类.
 *
 * @author 张越
 * @since 2024-05-16
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StreamLogParam {
    private String aippInstanceId;
}