/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.store.repository.pgsql.service;

import static com.huawei.fitframework.inspection.Validation.notNull;
import static com.huawei.jade.carver.util.SerializeUtils.json2obj;

import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.annotation.Fit;
import com.huawei.fitframework.annotation.Fitable;
import com.huawei.fitframework.serialization.ObjectSerializer;
import com.huawei.fitframework.transaction.Transactional;
import com.huawei.fitframework.util.CollectionUtils;
import com.huawei.fitframework.util.MapBuilder;
import com.huawei.fitframework.util.StringUtils;
import com.huawei.jade.store.entity.query.ModelQuery;
import com.huawei.jade.store.entity.transfer.ModelData;
import com.huawei.jade.store.repository.pgsql.entity.ModelDo;
import com.huawei.jade.store.repository.pgsql.mapper.ModelMapper;
import com.huawei.jade.store.repository.pgsql.mapper.TaskMapper;
import com.huawei.jade.store.service.HuggingFaceModelService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 任务的 Http 请求的服务层实现。
 *
 * @author 鲁为 l00839724
 * @since 2024-06-07
 */
@Component
public class DefaultHuggingFaceModelService implements HuggingFaceModelService {
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
    @Fitable(id = "store-repository-pgsql")
    @Transactional
    public List<ModelData> getModels(ModelQuery modelQuery) {
        List<ModelDo> dos = this.modelMapper.getModels(modelQuery);
        ArrayList<ModelData> modelDataList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(dos)) {
            for (ModelDo modelDo : dos) {
                ModelData modelData = new ModelData();
                modelData.setCreatedTime(modelDo.getCreatedTime());
                modelData.setUpdatedTime(modelDo.getUpdatedTime());
                modelData.setTaskName(modelDo.getTaskName());
                modelData.setName(modelDo.getName());
                modelData.setUrl(modelDo.getUrl());
                Map<String, Object> context = json2obj(modelDo.getContext(), this.serializer);
                modelData.setContext(MapBuilder.<String, Object>get()
                        .put("likes", context.containsKey("likes") ? context.get("likes") : 0)
                        .put("downloads", context.containsKey("downloads") ? context.get("downloads") : 0)
                        .put("description",
                                context.containsKey("description") ? context.get("description") : StringUtils.EMPTY)
                        .build());
                modelDataList.add(modelData);
            }
        }
        return modelDataList;
    }

    @Override
    @Fitable(id = "store-repository-pgsql")
    @Transactional
    public int getCount(String taskName) {
        return this.modelMapper.getCount(taskName);
    }
}
