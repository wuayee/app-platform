/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.domain;

import static modelengine.fit.jober.aipp.domain.BaseDomain.lazyGet;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import modelengine.fit.jober.aipp.dto.AppBuilderConfigFormPropertyDto;
import modelengine.fit.jober.aipp.repository.AppBuilderFormRepository;

import java.util.ArrayList;

/**
 * 应用构建器表单属性实体类
 *
 * @author 邬涨财
 * @since 2024-04-16
 */
@Data
@NoArgsConstructor
@Builder
public class AppBuilderFormProperty {
    private String id;

    private String formId;

    private String name;

    private String dataType;

    private Object defaultValue;

    private String from;

    private String group;

    private String description;

    private int index;

    private String appId;

    private AppBuilderForm form;

    private AppBuilderFormRepository formRepository;

    public AppBuilderFormProperty(String id, String formId, String name, String dataType, Object defaultValue,
            String from, String group, String description, int index, String appId, AppBuilderForm form,
            AppBuilderFormRepository formRepository) {
        this.id = id;
        this.formId = formId;
        this.name = name;
        this.dataType = dataType;
        this.defaultValue = defaultValue;
        this.from = from;
        this.group = group;
        this.description = description;
        this.index = index;
        this.appId = appId;
        this.form = form;
        this.formRepository = formRepository;
    }

    public AppBuilderForm getForm() {
        return lazyGet(this.form, this::loadForm, this::setForm);
    }

    /**
     * 将数据对象转换成传输层对象。
     *
     * @param formProperty 表示数据对象的 {@link AppBuilderFormProperty}。
     * @return 表示传输层对象的 {@link AppBuilderConfigFormPropertyDto}。
     */
    public static AppBuilderConfigFormPropertyDto toAppBuilderConfigFormPropertyDto(
            AppBuilderFormProperty formProperty) {
        return AppBuilderConfigFormPropertyDto.builder()
                .id(formProperty.getId())
                .name(formProperty.getName())
                .dataType(formProperty.getDataType())
                .defaultValue(formProperty.getDefaultValue())
                .from(formProperty.getFrom())
                .group(formProperty.getGroup())
                .description(formProperty.getDescription())
                .children(new ArrayList<>())
                .build();
    }

    private AppBuilderForm loadForm() {
        return this.formRepository.selectWithId(this.formId);
    }
}
