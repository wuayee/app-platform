/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fit.jober.aipp.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 查询instance id对应Msg的结构体
 *
 * @author 孙怡菲
 * @since 2024-12-09
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MsgInfoPO {
    private String instanceId;
    private String logData;
}
