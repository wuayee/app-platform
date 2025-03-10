/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.store.repository.pgsql.service;

import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.annotation.Fitable;
import modelengine.fitframework.serialization.ObjectSerializer;
import modelengine.jade.store.entity.query.ModelQuery;
import modelengine.jade.store.entity.query.QueryUtils;
import modelengine.jade.store.entity.transfer.ModelData;
import modelengine.jade.store.repository.pgsql.entity.ModelDo;
import modelengine.jade.store.repository.pgsql.mapper.ModelMapper;
import modelengine.jade.store.repository.pgsql.mapper.TaskMapper;
import modelengine.jade.store.service.HuggingFaceModelService;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 任务的 Http 请求的服务层实现。
 *
 * @author 鲁为
 * @since 2024-06-07
 */
@Component
public class DefaultHuggingFaceModelService implements HuggingFaceModelService {
    private static final String FITABLE_ID = "store-repository-pgsql";

    private final ObjectSerializer serializer;
    private final ModelMapper modelMapper;

    /**
     * 通过持久层接口来初始化 {@link DefaultHuggingFaceModelService} 的实例。
     *
     * @param serializer 表示序列化器实例的 {@link ObjectSerializer}。
     * @param modelMapper 表示持久层实例的 {@link TaskMapper}。
     */
    public DefaultHuggingFaceModelService(@Fit(alias = "json") ObjectSerializer serializer, ModelMapper modelMapper) {
        this.serializer = notNull(serializer, "The json serializer cannot be null.");
        this.modelMapper = notNull(modelMapper, "The model mapper cannot be null.");
    }

    @Override
    @Fitable(id = FITABLE_ID)
    public List<ModelData> getModels(ModelQuery modelQuery) {
        if (modelQuery == null || QueryUtils.isPageInvalid(modelQuery.getOffset(), modelQuery.getLimit())) {
            return Collections.emptyList();
        }
        List<ModelDo> dos = this.modelMapper.getModels(modelQuery);
        return dos.stream()
                .map(modelDo -> ModelDo.convertToModelData(modelDo, this.serializer))
                .collect(Collectors.toList());
    }

    @Override
    @Fitable(id = FITABLE_ID)
    public int getCount(String taskName) {
        return this.modelMapper.getCount(taskName);
    }
}
