/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2025. All rights reserved.
 */

package modelengine.fit.jober.aipp.condition;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import modelengine.fit.http.annotation.RequestQuery;

import java.util.List;

/**
 * 应用模板查询条件类。
 *
 * @author 方誉州
 * @since 2024-12-30
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TemplateQueryCondition {
    @RequestQuery(name = "name", required = false)
    private String name;

    @RequestQuery(name = "categories", required = false)
    private List<String> categories;

    @RequestQuery(name = "app_type", required = false)
    private List<String> appType;

    @RequestQuery(name = "limit")
    private int limit;

    @RequestQuery(name = "offset")
    private int offset;

    @RequestQuery(name = "order_by", required = false)
    private String orderBy;
}
