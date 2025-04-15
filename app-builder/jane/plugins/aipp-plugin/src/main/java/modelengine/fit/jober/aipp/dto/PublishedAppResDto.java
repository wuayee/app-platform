/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fit.jober.aipp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import modelengine.fitframework.annotation.Property;

import java.time.LocalDateTime;

/**
 * 应用历史记录的返回 Dto 对象。
 *
 * @author 邬涨财
 * @since 2024-06-07
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PublishedAppResDto {
    @Property(description = "应用唯一标识", name = "appId")
    private String appId;

    @Property(description = "应用版本", name = "appVersion")
    private String appVersion;

    @Property(description = "发布时间", name = "publishedAt")
    private LocalDateTime publishedAt;

    @Property(description = "发布人", name = "publishedBy")
    private String publishedBy;

    @Property(description = "发布描述信息", name = "publishedDescription")
    private String publishedDescription;

    @Property(description = "发布更新日志", name = "publishedUpdateLog")
    private String publishedUpdateLog;
}
