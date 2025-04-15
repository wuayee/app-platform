/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fit.jober.aipp.condition;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import modelengine.fit.http.annotation.RequestQuery;

/**
 * 知识库查询条件集
 *
 * @author 黄夏露
 * @since 2024-04-23
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class KnowledgeQueryCondition {
    @RequestQuery(name = "name", required = false)
    private String name;
}
