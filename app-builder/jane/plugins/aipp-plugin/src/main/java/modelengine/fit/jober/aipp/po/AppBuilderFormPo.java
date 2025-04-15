/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fit.jober.aipp.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * AppBuilder表单结构体
 *
 * @author 邬涨财
 * @since 2024-04-16
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppBuilderFormPo {
    private String id;
    private String name;
    private String tenantId;
    private String appearance;
    private String createBy;
    private String updateBy;
    private String type;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;
    private String version;
    private String formSuiteId;
}
