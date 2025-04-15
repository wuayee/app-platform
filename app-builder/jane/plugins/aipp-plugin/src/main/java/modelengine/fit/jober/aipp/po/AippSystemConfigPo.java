/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fit.jober.aipp.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 系统配置结构体
 *
 * @author 张越
 * @since 2024-11-30
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AippSystemConfigPo {
    private Long id;
    private String configKey;
    private String configValue;
    private String configGroup;
    private String configParent;
}
