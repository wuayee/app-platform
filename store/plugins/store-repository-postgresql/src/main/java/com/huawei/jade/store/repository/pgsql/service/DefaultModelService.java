/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.store.repository.pgsql.service;

import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.annotation.Fit;
import com.huawei.fitframework.annotation.Fitable;
import com.huawei.fitframework.serialization.ObjectSerializer;
import com.huawei.fitframework.util.CollectionUtils;
import com.huawei.fitframework.util.TypeUtils;
import com.huawei.jade.store.entity.query.ModelQuery;
import com.huawei.jade.store.entity.transfer.ModelData;
import com.huawei.jade.store.repository.pgsql.entity.ModelDo;
import com.huawei.jade.store.repository.pgsql.mapper.ModelMapper;
import com.huawei.jade.store.repository.pgsql.mapper.TaskMapper;
import com.huawei.jade.store.service.ModelService;

import java.lang.reflect.Type;
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
public class DefaultModelService implements ModelService {
    private final ObjectSerializer serializer;
    private final ModelMapper modelMapper;

    /**
     * 通过持久层接口来初始化 {@link DefaultTaskService} 的实例。
     *
     * @param serializer 表示序列化器实例的 {@link ObjectSerializer}。
     * @param modelMapper 表示持久层实例的 {@link TaskMapper}。
     */
    public DefaultModelService(@Fit(alias = "json") ObjectSerializer serializer, ModelMapper modelMapper) {
        this.serializer = serializer;
        this.modelMapper = modelMapper;
    }

    @Override
    @Fitable(id = "store-repository-pgsql")
    public List<ModelData> getModels(ModelQuery modelQuery) {
        List<ModelDo> dos = this.modelMapper.getModels(modelQuery);
        ArrayList<ModelData> modelDataList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(dos)) {
            for (ModelDo modelDo : dos) {
                ModelData modelData = new ModelData();
                modelData.setCreatedTime(modelDo.getCreatedTime());
                modelData.setUpdatedTime(modelDo.getUpdatedTime());
                modelData.setTaskId(modelDo.getTaskId());
                modelData.setName(modelDo.getName());
                modelData.setUrl(modelDo.getUrl());
                modelData.setContext(json2obj(modelDo.getContext(), serializer));
                modelDataList.add(modelData);
            }
        }
        return modelDataList;
    }

    /**
     * 反序列化。
     *
     * @param schema 表示待序列化的字符串 {@link String}。
     * @param serializer 表示序列化对象的 {@link ObjectSerializer}。
     * @return 序列化的结果的 {@link Map}{@code <}{@link String}{@code ,}{@link Object}{@code >}。
     */
    public static Map<String, Object> json2obj(String schema, ObjectSerializer serializer) {
        Map<String, Object> res = null;
        if (schema != null) {
            res = serializer.deserialize(schema,
                    TypeUtils.parameterized(Map.class, new Type[] {String.class, Object.class}));
        }
        return res;
    }
}
