/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fit.jober.aipp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 保存应用配置的传输类
 *
 * @author 鲁为
 * @since 2024-10-28
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppBuilderSaveConfigDto {
    private List<AppBuilderConfigFormPropertyDto> input;
    private String graph;
}
