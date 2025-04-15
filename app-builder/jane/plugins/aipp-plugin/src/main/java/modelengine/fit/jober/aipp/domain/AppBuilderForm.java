/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fit.jober.aipp.domain;

import modelengine.fit.jober.aipp.repository.AppBuilderFormPropertyRepository;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * 表单构建器实体类
 *
 * @author 邬涨财
 * @since 2024-04-16
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@SuperBuilder
@Slf4j
public class AppBuilderForm extends BaseDomain {
    private String id;
    private String name;
    private String tenantId;
    private Map<String, Object> appearance;
    private String type;
    private String version;
    private String formSuiteId;
    private AppBuilderFormPropertyRepository formPropertyRepository;

    public AppBuilderForm(AppBuilderFormPropertyRepository formPropertyRepository) {
        this.formPropertyRepository = formPropertyRepository;
    }
}
