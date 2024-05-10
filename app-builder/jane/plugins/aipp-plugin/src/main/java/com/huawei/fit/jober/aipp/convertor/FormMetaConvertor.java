/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.aipp.convertor;

import com.huawei.fit.dynamicform.entity.FormMetaItem;
import com.huawei.fit.jane.meta.property.MetaPropertyDeclarationInfo;
import com.huawei.fit.jober.entity.task.TaskProperty;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * FormMetaConvertor
 *
 * @author l00611472
 * @since 2023-12-15
 */
@Mapper
public interface FormMetaConvertor {
    FormMetaConvertor INSTANCE = Mappers.getMapper(FormMetaConvertor.class);

    /**
     * 将FormMetaItem转换为MetaPropertyDeclarationInfo
     *
     * @param formMeta 表单Meta数据
     * @return MetaPropertyDeclarationInfo
     */
    @Mapping(target = "name", expression = "java(com.huawei.fit.jane.Undefinable.defined(formMeta.getKey()))")
    @Mapping(target = "dataType", expression = "java(com.huawei.fit.jane.Undefinable.defined(formMeta.getType()))")
    @Mapping(target = "description", expression = "java(com.huawei.fit.jane.Undefinable.defined(formMeta.getName()))")
    @Mapping(target = "required", ignore = true)
    @Mapping(target = "identifiable", ignore = true)
    @Mapping(target = "scope", ignore = true)
    @Mapping(target = "attribute", ignore = true)
    MetaPropertyDeclarationInfo toMetaPropertyDeclarationInfo(FormMetaItem formMeta);


    /**
     * 将FormMetaItem转换为TaskProperty
     *
     * @param formMeta FormMetaItem数据
     * @return TaskProperty
     */
    @Mapping(target = "name", expression = "java(formMeta.getKey())")
    @Mapping(target = "dataType", expression = "java(formMeta.getType())")
    @Mapping(target = "description", expression = "java(formMeta.getName())")
    @Mapping(target = "scope", ignore = true)
    @Mapping(target = "required", ignore = true)
    @Mapping(target = "identifiable", ignore = true)
    @Mapping(target = "appearance", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "sequence", ignore = true)
    @Mapping(target = "categories", ignore = true)
    TaskProperty toTaskProperty(FormMetaItem formMeta);
}
