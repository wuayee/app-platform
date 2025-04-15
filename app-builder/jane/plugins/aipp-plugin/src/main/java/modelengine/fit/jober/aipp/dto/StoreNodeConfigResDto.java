/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fit.jober.aipp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import modelengine.fitframework.annotation.Property;

import java.util.List;

/**
 * Store 节点配置对象
 *
 * @author 邬涨财
 * @since 2024-05-13
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StoreNodeConfigResDto {
    @Property(name = "basic")
    private List<StoreNodeInfoDto> basicList;

    @Property(name = "evaluation")
    private List<StoreNodeInfoDto> evaluationList;

    @Property(name = "tool")
    private List<ToolModelDto> toolList;
}
