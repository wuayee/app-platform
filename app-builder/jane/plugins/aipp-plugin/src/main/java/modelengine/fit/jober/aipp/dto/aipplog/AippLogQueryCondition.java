/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fit.jober.aipp.dto.aipplog;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import modelengine.fitframework.annotation.Property;

import java.time.LocalDateTime;

/**
 * aipp实例历史记录查询条件
 *
 * @author 刘信宏
 * @since 2024-01-08
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AippLogQueryCondition {
    @Property(name = "aipp_id")
    private String aippId;

    @Property(name = "instance_id")
    private String instanceId;

    @Property(name = "after_at")
    private LocalDateTime afterAt;
}
