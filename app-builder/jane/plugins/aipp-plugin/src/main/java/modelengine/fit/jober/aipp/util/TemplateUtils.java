/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.util;

import modelengine.fit.jober.aipp.domain.AppBuilderApp;
import modelengine.fit.jober.aipp.domain.AppTemplate;
import modelengine.fit.jober.aipp.dto.template.TemplateInfoDto;
import modelengine.fit.jober.aipp.enums.AppState;
import modelengine.fit.jober.aipp.enums.AppTypeEnum;
import modelengine.fitframework.util.ObjectUtils;

/**
 * 应用模板相关的工具类。
 *
 * @author 方誉州
 * @since 2025-01-07
 */
public class TemplateUtils {
    /**
     * 应用简介的键值。
     */
    public static final String DESCRIPTION_ATTR_KEY = "description";

    /**
     * 应用头像的键值。
     */
    public static final String ICON_ATTR_KEY = "icon";

    private static final String INIT_VERSION = "1.0.0";

    private static final int ZERO_COUNT = 0;

    /**
     * 将 {@link AppTemplate} 对象转换为 {@link TemplateInfoDto}。
     *
     * @param template 表示应用模板领域类的 {@link AppTemplate}。
     * @return 表述用于传输的应用模板数据对象的 {@link TemplateInfoDto}。
     */
    public static TemplateInfoDto convertToTemplateDto(AppTemplate template) {
        return TemplateInfoDto.builder()
                .id(template.getId())
                .category(template.getCategory())
                .description(ObjectUtils.cast(template.getAttributes().get(DESCRIPTION_ATTR_KEY)))
                .appType(template.getAppType())
                .icon(ObjectUtils.cast(template.getAttributes().get(ICON_ATTR_KEY)))
                .name(template.getName())
                .creator(template.getCreateBy())
                .build();
    }

    /**
     * 将应用领域对象 {@link AppBuilderApp} 转换为应用模板领域对象 {@link AppTemplate}。
     *
     * @param app 表示应用的 {@link AppBuilderApp}。
     * @return 表示应用模板的 {@link AppTemplate}。
     */
    public static AppTemplate convertToAppTemplate(AppBuilderApp app) {
        return AppTemplate.builder()
                .id(app.getId())
                .name(app.getName())
                .builtType(app.getAppBuiltType())
                .appType(app.getAppType())
                .category(app.getAppCategory())
                .attributes(app.getAttributes())
                .like(ZERO_COUNT)
                .collection(ZERO_COUNT)
                .usage(ZERO_COUNT)
                .version(INIT_VERSION)
                .configId(app.getConfigId())
                .flowGraphId(app.getFlowGraphId())
                .build();
    }

    /**
     * 将应用模板领域对象 {@link AppTemplate} 转换为应用领域对象 {@link AppBuilderApp}。
     *
     * @param template 表示应用模板的 {@link AppTemplate}。
     * @return 表示应用的 {@link AppBuilderApp}。
     */
    public static AppBuilderApp convertToAppBuilderApp(AppTemplate template) {
        return AppBuilderApp.builder()
                .id(template.getId())
                .name(template.getName())
                .configId(template.getConfigId())
                .flowGraphId(template.getFlowGraphId())
                .type(AppTypeEnum.TEMPLATE.code())
                .appType(template.getAppType())
                .version(INIT_VERSION)
                .attributes(template.getAttributes())
                .state(AppState.INACTIVE.getName())
                .appBuiltType(template.getBuiltType())
                .appCategory(template.getCategory())
                .build();
    }
}
