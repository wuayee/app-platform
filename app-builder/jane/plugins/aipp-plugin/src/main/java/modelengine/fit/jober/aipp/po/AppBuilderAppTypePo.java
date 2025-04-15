/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2025-2025. All rights reserved.
 */

package modelengine.fit.jober.aipp.po;

import modelengine.fit.jober.aipp.aop.LocaleField;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 应用的业务分类类型.
 *
 * @author songyongtan
 * @since 2025-01-04
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AppBuilderAppTypePo {
    private String id;
    @LocaleField
    private String name;
    private String tenantId;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;
}
