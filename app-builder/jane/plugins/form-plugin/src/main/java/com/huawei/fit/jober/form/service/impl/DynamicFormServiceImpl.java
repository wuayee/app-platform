/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.form.service.impl;

import com.huawei.fit.dynamicform.DynamicFormService;
import com.huawei.fit.dynamicform.common.PageResponse;
import com.huawei.fit.dynamicform.condition.FormQueryCondition;
import com.huawei.fit.dynamicform.condition.PaginationCondition;
import com.huawei.fit.dynamicform.dto.DynamicFormDto;
import com.huawei.fit.dynamicform.entity.DynamicFormDetailEntity;
import com.huawei.fit.dynamicform.entity.DynamicFormEntity;
import com.huawei.fit.dynamicform.entity.FormMetaQueryParameter;
import com.huawei.fit.elsa.generable.GraphExposeService;
import com.huawei.fit.elsa.generable.entity.Graph;
import com.huawei.fit.elsa.generable.entity.GraphParam;
import com.huawei.fit.jane.common.entity.OperationContext;
import com.huawei.fit.jober.form.exception.FormErrCode;
import com.huawei.fit.jober.form.exception.FormParamException;
import com.huawei.fit.jober.form.mapper.FormMapper;

import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.annotation.Value;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.util.StringUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 动态表单服务
 *
 * @author 孙怡菲
 * @since 2024/5/10
 */
@Component
public class DynamicFormServiceImpl implements DynamicFormService {
    private static final Logger log = Logger.get(DynamicFormServiceImpl.class);

    private final FormMapper formMapper;
    private final GraphExposeService elsaClient;
    private final String accessKey;

    /**
     * 构造函数
     *
     * @param formMapper 表单数据库操作接口
     * @param elsaClient 调用elsa接口服务
     * @param accessKey 权限校验key
     */
    public DynamicFormServiceImpl(@Fit FormMapper formMapper, @Fit GraphExposeService elsaClient,
            @Value("${elsa.accessKey}") String accessKey) {
        this.formMapper = formMapper;
        this.elsaClient = elsaClient;
        this.accessKey = accessKey;
    }

    private GraphParam buildGraphParam(String formId, String version, String json) {
        GraphParam elsaParam = new GraphParam();
        elsaParam.setAccessKey(accessKey);
        elsaParam.setGraphId(formId);
        elsaParam.setVersion(version);
        elsaParam.setJson(json);
        return elsaParam;
    }

    @Override
    public PageResponse<DynamicFormEntity> queryFormWithCondition(String tenantId, FormQueryCondition cond,
            PaginationCondition page) {
        long resultTotalCount = formMapper.countWithCondition(tenantId, cond);
        List<DynamicFormEntity> result = formMapper.selectWithCondition(tenantId, cond, page);
        log.info("TenantId {} queryFormWithCondition (cond {} page {})get result total: {} returning: {}",
                tenantId,
                cond,
                page,
                resultTotalCount,
                result.size());
        return new PageResponse<>(resultTotalCount, result);
    }

    @Override
    public PageResponse<DynamicFormEntity> queryFormWithCondition(FormQueryCondition cond, PaginationCondition page,
            OperationContext context) {
        return new PageResponse<>(formMapper.countWithCondition(context.getTenantId(), cond),
                formMapper.selectWithCondition(context.getTenantId(), cond, page));
    }

    @Override
    public DynamicFormDetailEntity queryFormDetailByPrimaryKey(String formId, String version,
            OperationContext context) {
        log.debug("TenantId {} user {} query form {} version {}",
                context.getTenantId(),
                context.getName(),
                formId,
                version);
        DynamicFormEntity entity = formMapper.selectByPrimaryKey(formId, version);
        if (entity == null) {
            log.debug("in detail sql query form id {} version {} returns null", formId, version);
            return null;
        }
        GraphParam elsaParam = buildGraphParam(formId, version, "");

        // 暂时用不到，可去掉；后期应该改为调用appBuilder的查询表单的渲染数据json的接口
        String data = elsaClient.get(elsaParam, context);
        return new DynamicFormDetailEntity(entity, data);
    }

    @Override
    public List<Map<FormMetaQueryParameter, DynamicFormDetailEntity>> queryFormDetailByPrimaryKeyAndMap(
            List<FormMetaQueryParameter> parameters, OperationContext context) {
        List<DynamicFormEntity> entities = this.formMapper.selectFormByPrimaryKeyList(parameters);
        if (entities.isEmpty()) {
            return Collections.emptyList();
        }
        List<GraphParam> elsaParams = entities.stream()
                .filter(Objects::nonNull)
                .map(item -> this.buildGraphParam(item.getId(), item.getVersion(), StringUtils.EMPTY))
                .collect(Collectors.toList());
        // 暂时用不到，可去掉；后期应该改为调用appBuilder的批量查询表单的渲染数据json的接口
        List<Graph> elsaGraphs = this.elsaClient.list(elsaParams, context);
        return parameters.stream()
                .map(item -> this.buildDynamicFormDetailEntityMap(item,
                        this.getDynamicFormEntityFromList(item, entities),
                        this.getElsaDataFromList(item, elsaGraphs)))
                .collect(Collectors.toList());
    }

    private Map<FormMetaQueryParameter, DynamicFormDetailEntity> buildDynamicFormDetailEntityMap(
            FormMetaQueryParameter parameter, DynamicFormEntity entity, String data) {
        if (entity == null) {
            log.debug("in detail sql query form id {} version {} returns null",
                    parameter.getFormId(),
                    parameter.getVersion());
            return new HashMap<FormMetaQueryParameter, DynamicFormDetailEntity>() {
                {
                    put(parameter, null);
                }
            };
        }
        return new HashMap<FormMetaQueryParameter, DynamicFormDetailEntity>() {
            {
                put(parameter, new DynamicFormDetailEntity(entity, data));
            }
        };
    }

    private DynamicFormEntity getDynamicFormEntityFromList(FormMetaQueryParameter parameter,
            List<DynamicFormEntity> entities) {
        return entities.stream()
                .filter(item -> Objects.equals(item.getId(), parameter.getFormId()) && Objects.equals(item.getVersion(),
                        parameter.getVersion()))
                .findFirst()
                .orElse(null);
    }

    private String getElsaDataFromList(FormMetaQueryParameter parameter, List<Graph> elsaGraphs) {
        return elsaGraphs.stream()
                .filter(item -> Objects.equals(item.getGraphId(), parameter.getFormId())
                        && Objects.equals(item.getVersion(), parameter.getVersion()))
                .map(Graph::getJson)
                .findFirst()
                .orElse(StringUtils.EMPTY);
    }

    @Override
    public boolean saveForm(DynamicFormDetailEntity formDetail, OperationContext context) {
        log.info("TenantId {} user {} save form {} version {}",
                context.getTenantId(),
                context.getName(),
                formDetail.getMeta().getId(),
                formDetail.getMeta().getVersion());
        GraphParam elsaParam =
                buildGraphParam(formDetail.getMeta().getId(), formDetail.getMeta().getVersion(), formDetail.getData());

        if (StringUtils.isEmpty(formDetail.getMeta().getFormName())) {
            throw new FormParamException(context, FormErrCode.FORM_NAME_IS_EMPTY);
        }

        // 要去掉，暂时没有用到
        if (elsaClient.save(elsaParam, context) == 0) {
            // only update if elsa successfully saved
            formMapper.insertOrUpdateByPrimaryKey(DynamicFormEntity.builder()
                    .id(formDetail.getMeta().getId())
                    .version(formDetail.getMeta().getVersion())
                    .formName(formDetail.getMeta().getFormName())
                    .tenantId(context.getTenantId())
                    .updateUser(String.join(" ", context.getName(), context.getW3Account()))
                    .build());
            return true;
        }
        log.warn("elsa save form {} version {} fail", elsaParam.getGraphId(), elsaParam.getVersion());
        return false;
    }

    @Override
    public boolean deleteForm(DynamicFormDto formDto, OperationContext context) {
        log.info("TenantId {} user {} delete form {} version {}",
                context.getTenantId(),
                context.getName(),
                formDto.getId(),
                formDto.getVersion());

        GraphParam elsaParam = buildGraphParam(formDto.getId(), formDto.getVersion(), "");
        // 暂时用不到，可去掉；后期应该改为调用appBuilder删除表单的渲染数据json的接口
        if (elsaClient.delete(elsaParam, context) == 0) {
            // only delete if elsa successfully deleted
            formMapper.deleteByPrimaryKey(formDto.getId(), formDto.getVersion());
            return true;
        }
        log.warn("elsa delete form {} version {} fail", elsaParam.getGraphId(), elsaParam.getVersion());
        return false;
    }
}
