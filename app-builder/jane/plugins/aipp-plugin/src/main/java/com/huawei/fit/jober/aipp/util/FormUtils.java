/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.util;

import com.huawei.fit.dynamicform.entity.DynamicFormDetailEntity;
import com.huawei.fit.dynamicform.entity.DynamicFormEntity;
import com.huawei.fit.dynamicform.entity.FormMetaInfo;
import com.huawei.fit.dynamicform.entity.FormMetaItem;
import com.huawei.fit.dynamicform.entity.FormMetaQueryParameter;
import com.huawei.fit.jane.common.entity.OperationContext;
import com.huawei.fit.jober.aipp.domain.AppBuilderForm;
import com.huawei.fit.jober.aipp.domain.AppBuilderFormProperty;
import com.huawei.fit.jober.aipp.entity.AippLogData;
import com.huawei.fit.jober.aipp.repository.AppBuilderFormPropertyRepository;
import com.huawei.fit.jober.aipp.repository.AppBuilderFormRepository;
import com.huawei.fitframework.log.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * AppBuilder Form 操作工具类
 *
 * @author f00881613
 * @since 2024/6/14
 */
public class FormUtils {
    private static final Logger log = Logger.get(FormUtils.class);

    /**
     * 使用主键formId查询表单元信息和数据
     *
     * @param formId form id
     * @param version 版本号
     * @param context 操作上下文{@link OperationContext}
     * @param formRepository 使用的{@link AppBuilderFormRepository}
     * @param formPropRepos 使用的{@link AppBuilderFormPropertyRepository}
     * @return 获得的表单元信息和数据，当查询formId返回null时，返回null
     */
    public static DynamicFormDetailEntity queryFormDetailByPrimaryKey(String formId, String version,
                                                                      OperationContext context,
                                                                      AppBuilderFormRepository formRepository,
                                                                      AppBuilderFormPropertyRepository formPropRepos) {
        log.debug("TenantId {} user {} query form {} version {}",
                context.getTenantId(),
                context.getName(),
                formId,
                version);
        AppBuilderForm builderForm = formRepository.selectWithId(formId);
        DynamicFormDetailEntity nullReturn = null;
        if (builderForm == null) {
            log.debug("in detail sql query form id {} version {} returns null", formId, version);
            return nullReturn;
        }
        builderForm.setFormPropertyRepository(formPropRepos);
        String data = buildData(builderForm.getFormProperties());
        return new DynamicFormDetailEntity(convertToDynamicFormEntity(builderForm), data);
    }

    private static String buildData(List<AppBuilderFormProperty> formProperties) {
        Map<String, String> map = formProperties.stream()
                .collect(Collectors.toMap(AppBuilderFormProperty::getName,
                        appBuilderFormProperty -> JsonUtils.toJsonString(appBuilderFormProperty.getDefaultValue())));
        return JsonUtils.toJsonString(map);
    }

    private static DynamicFormEntity convertToDynamicFormEntity(AppBuilderForm builderForm) {
        return DynamicFormEntity.builder()
                .id(builderForm.getId())
                .formName(builderForm.getName())
                .tenantId(builderForm.getTenantId())
                .build();
    }

    /**
     * 使用表单数据构造Aipp日志{@link AippLogData}
     *
     * @param formRepository 使用的{@link AppBuilderFormRepository}
     * @param formId form id
     * @param formVersion form版本号
     * @param businessData business data
     * @return 构造得到的Aipp日志
     */
    public static AippLogData buildLogDataWithFormData(AppBuilderFormRepository formRepository, String formId,
                                                       String formVersion, Map<String, Object> businessData) {
        List<FormMetaQueryParameter> parameter =
                Collections.singletonList(new FormMetaQueryParameter(formId, formVersion));

        Map<String, Object> formArgs = buildFormMetaInfos(parameter, formRepository)
                .stream()
                .flatMap(item -> item.getFormMetaItems().stream().map(FormMetaItem::getKey))
                .filter(businessData::containsKey)
                .collect(Collectors.toMap(Function.identity(), businessData::get));

        return AippLogData.builder()
                .formId(formId)
                .formVersion(formVersion)
                .formArgs(JsonUtils.toJsonString(formArgs))
                .build();
    }

    /**
     * 构造form元数据信息{@link FormMetaInfo}列表
     *
     * @param parameters form元数据查询参数列表
     * @param formRepository 使用的{@link AppBuilderFormRepository}
     * @return 返回form元数据信息列表
     */
    public static List<FormMetaInfo> buildFormMetaInfos(List<FormMetaQueryParameter> parameters,
                                                        AppBuilderFormRepository formRepository) {
        return parameters.stream()
                .map(parameter -> buildFormMetaInfo(parameter, formRepository))
                .collect(Collectors.toList());
    }

    private static FormMetaInfo buildFormMetaInfo(FormMetaQueryParameter parameter,
                                                  AppBuilderFormRepository formRepository) {
        FormMetaInfo formMetaInfo = new FormMetaInfo(parameter.getFormId(), parameter.getVersion());
        formMetaInfo.setFormMetaItems(buildFormMetaItems(parameter, formRepository));
        return formMetaInfo;
    }

    private static List<FormMetaItem> buildFormMetaItems(FormMetaQueryParameter parameter,
                                                         AppBuilderFormRepository formRepository) {
        AppBuilderForm form = formRepository.selectWithId(parameter.getFormId());
        if (form == null) {
            return new ArrayList<>();
        }
        List<AppBuilderFormProperty> formProperties = form.getFormProperties();
        return formProperties.stream()
                .map(FormUtils::buildFormMetaItem)
                .collect(Collectors.toList());
    }

    private static FormMetaItem buildFormMetaItem(AppBuilderFormProperty formProperty) {
        return FormMetaItem.builder()
                .key(formProperty.getId())
                .name(formProperty.getName())
                .type(formProperty.getDataType())
                .defaultValue(formProperty.getDefaultValue())
                .build();
    }
}
