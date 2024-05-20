/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2020. All rights reserved.
 */

package com.huawei.fit.jober.form.fitable;

import static com.huawei.fitframework.util.ObjectUtils.nullIf;

import com.huawei.fit.dynamicform.DynamicFormMetaService;
import com.huawei.fit.dynamicform.entity.DynamicFormEntity;
import com.huawei.fit.dynamicform.entity.FormMetaInfo;
import com.huawei.fit.dynamicform.entity.FormMetaItem;
import com.huawei.fit.dynamicform.entity.FormMetaQueryParameter;
import com.huawei.fit.elsa.generable.GraphExposeService;
import com.huawei.fit.elsa.generable.entity.GraphParam;
import com.huawei.fit.jane.common.entity.OperationContext;
import com.huawei.fit.jober.form.dto.ElsaDataDto;
import com.huawei.fit.jober.form.dto.ShapesMetaType;
import com.huawei.fit.jober.form.exception.FormErrCode;
import com.huawei.fit.jober.form.exception.FormParamException;
import com.huawei.fit.jober.form.mapper.FormMapper;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.annotation.Fit;
import com.huawei.fitframework.annotation.Value;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 动态表单元数据解析服务
 *
 * @author x00576283
 * @since 2023/12/14
 */
@Component
public class DynamicFormMetaServiceIml implements DynamicFormMetaService {
    private final FormMapper formMapper;

    private final GraphExposeService elsaClient;

    private final String accessKey;

    public DynamicFormMetaServiceIml(@Fit FormMapper formMapper, @Fit GraphExposeService elsaClient,
            @Value("${elsa.accessKey}") String accessKey) {
        this.formMapper = formMapper;
        this.elsaClient = elsaClient;
        this.accessKey = accessKey;
    }

    private GraphParam buildGraphParam(String formId, String version) {
        GraphParam elsaParam = new GraphParam();
        elsaParam.setAccessKey(accessKey);
        elsaParam.setGraphId(formId);
        elsaParam.setVersion(version);
        elsaParam.setJson("");
        return elsaParam;
    }

    @Override
    public List<FormMetaInfo> query(List<FormMetaQueryParameter> parameter) {
        return parameter.stream()
                .map(param -> formMapper.selectByPrimaryKey(param.getFormId(), param.getVersion()))
                .filter(Objects::nonNull)
                .map(this::parseMeta)
                .collect(Collectors.toList());
    }

    private FormMetaInfo parseMeta(DynamicFormEntity formEntity) {
        String formData = queryMeta(formEntity);
        FormMetaInfo metaInfo = new FormMetaInfo(String.valueOf(formEntity.getId()), formEntity.getVersion());
        List<FormMetaItem> formMetaItems;
        try {
            ElsaDataDto elsaData = new ObjectMapper().readValue(formData, ElsaDataDto.class);
            List<ElsaDataDto.ElsaShape> elsaShapes = elsaData.getPages().get(0).getShapes();
            formMetaItems = elsaShapes.stream().flatMap(shape -> {
                List<ElsaDataDto.ElsaShapeMeta> metaArray = shape.getMeta();
                if (metaArray == null || metaArray.isEmpty()) {
                    return Stream.empty();
                }
                return metaArray.stream().map(meta -> {
                    Integer length = meta.getLength();
                    length = (length != null && length == 0) ? null : length;
                    return new FormMetaItem(meta.getKey(),
                            meta.getName(),
                            ShapesMetaType.getShapesMetaType(meta.getType()).getValue(),
                            length, null);
                });
            }).collect(Collectors.toList());
        } catch (JsonProcessingException e) {
            throw new FormParamException(FormErrCode.INPUT_PARAM_IS_INVALID, "form entity. reason: " + e.getMessage());
        }
        metaInfo.setFormMetaItems(nullIf(formMetaItems, new ArrayList<>()));
        return metaInfo;
    }

    private String queryMeta(DynamicFormEntity formEntity) {
        GraphParam elsaParam = buildGraphParam(formEntity.getId(), formEntity.getVersion());
        OperationContext context = new OperationContext();
        // todo 暂时用不到，可去掉；后期应该改为调用appBuilder的查询表单的渲染数据json的接口
        return elsaClient.get(elsaParam, context);
    }
}
