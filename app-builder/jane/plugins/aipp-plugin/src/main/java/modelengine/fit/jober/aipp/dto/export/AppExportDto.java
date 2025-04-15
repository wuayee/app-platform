/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fit.jober.aipp.dto.export;

import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.jober.aipp.enums.AppTypeEnum;
import modelengine.fit.jober.aipp.util.AppImExportUtil;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import modelengine.fitframework.annotation.Property;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.StringUtils;

import java.util.Map;

/**
 * 应用导出配置类。
 *
 * @author 方誉州
 * @since 2024-10-30
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AppExportDto {
    @Property(description = "应用配置版本号")
    String version;

    @Property(description = "应用基础信息")
    AppExportApp app;

    @Property(description = "应用 configUI 配置信息")
    AppExportConfig config;

    @Property(description = "应用流程图配置信息")
    AppExportFlowGraph flowGraph;

    /**
     * 获取icon路径.
     *
     * @param context 操作人上下文信息.
     * @return {@link String} icon路径.
     */
    public String getIconPath(OperationContext context) {
        Object iconAttr = this.app.getAttributes().get("icon");
        String iconContent = iconAttr instanceof Map ? ObjectUtils.cast(
                ObjectUtils.<Map<String, Object>>cast(iconAttr).get("content")) : StringUtils.EMPTY;
        if (StringUtils.isBlank(iconContent)) {
            return iconContent;
        }
        String iconExtension = ObjectUtils.cast(ObjectUtils.<Map<String, Object>>cast(iconAttr).get("type"));
        return AppImExportUtil.saveIconFile(iconContent, iconExtension, context.getTenantId());
    }

    /**
     * 获取类型.
     *
     * @return 类型.
     */
    public String getType() {
        return ObjectUtils.cast(this.app.getAttributes().getOrDefault("appType", AppTypeEnum.APP.code()));
    }
}
