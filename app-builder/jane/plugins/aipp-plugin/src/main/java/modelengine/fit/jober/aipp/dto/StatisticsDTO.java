/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package modelengine.fit.jober.aipp.dto;

import modelengine.fitframework.annotation.Property;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * appengine统计数据
 *
 * @author 陈潇文
 * @since 2024-12-26
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StatisticsDTO {
    @Property(description = "已发布的应用数量")
    private long publishedAppNum;

    @Property(description = "未发布的应用数量")
    private long unpublishedAppNum;

    @Property(description = "表单数量")
    private long formNum;

    @Property(description = "插件数量")
    private int pluginNum;
}
