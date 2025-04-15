/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package modelengine.fit.jober.aipp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import modelengine.fitframework.annotation.Property;

/**
 * 创建Aipp响应体
 *
 * @author 刘信宏
 * @since 2023-12-08
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AippCreateDto {
    @Property(description = "aipp id", name = "aipp_id")
    private String aippId;

    @Property(description = "aipp version", name = "version")
    private String version;

    @Property(description = "tool unique name", name = "tool_unique_name")
    private String toolUniqueName;

    @Property(description = "app id", name = "app_id")
    private String appId;
}
