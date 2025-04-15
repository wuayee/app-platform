/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fit.jober.aipp.condition;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 为Form提供查询条件
 *
 * @author 陈潇文
 * @since 2024-11-25
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FormQueryCondition {
    private String tenantId;
    private String type;
    private Long offset;
    private int limit;
    private String name;
    private String id;
    private String createBy;
}
