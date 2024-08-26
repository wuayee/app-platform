/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.aipp.dto;

import com.huawei.fit.jane.common.validation.Size;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import modelengine.fitframework.annotation.Property;

import java.util.Map;

/**
 * Aipp创建/更新参数
 *
 * @author 刘信宏
 * @since 2023-12-08
 */
@SuperBuilder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AippDto {
    @Property(description = "aipp 名称", example = "aipp")
    @Size(min = 1, max = 255, message = "名称长度范围[1, 255]")
    private String name;

    @Property(description = "aipp 描述", example = "aipp 编排应用")
    @Size(min = 0, max = 1024, message = "描述长度范围[0, 1024]")
    private String description;

    @Property(description = "流程视图定义")
    @JsonProperty("flow_view_data")
    private Map<String, Object> flowViewData;

    @Property(description = "aipp 发布链接")
    @JsonProperty("publish_url")
    private String publishUrl;

    @Property(description = "aipp 头像")
    private String icon;

    @Property(description = "aipp 版本号")
    private String version;

    @Property(description = "aipp 发布到store的唯一标识")
    private String uniqueName;

    @Property(description = "aipp 唯一标识")
    private String id;

    @Property(description = "app 唯一标识")
    private String appId;

    @Property(description = "app 类型")
    private String type;

    @Property(description = "小海app分类")
    private String xiaohaiClassification;

    @Property(description = "aipp 发布描述", example = "该发布的作用是生成稳定版本")
    private String publishedDescription;

    @Property(description = "aipp 发布更新日志", example = "该发布更新了流程")
    private String publishedUpdateLog;
}
