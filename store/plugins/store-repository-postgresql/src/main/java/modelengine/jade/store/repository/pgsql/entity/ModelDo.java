/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.store.repository.pgsql.entity;

import static modelengine.jade.carver.util.SerializeUtils.json2obj;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import modelengine.fitframework.serialization.ObjectSerializer;
import modelengine.fitframework.util.MapBuilder;
import modelengine.fitframework.util.StringUtils;
import modelengine.jade.carver.entity.CommonDo;
import modelengine.jade.store.entity.transfer.ModelData;

import java.util.Map;

/**
 * 存入数据库的模型的实体类。
 *
 * @author 鲁为
 * @since 2024-06-07
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ModelDo extends CommonDo {
    /**
     * 表示任务的唯一标识。
     */
    private String taskName;

    /**
     * 表示模型的名字。
     */
    private String name;

    /**
     * 表示模型的跳转链接。
     */
    private String url;

    /**
     * 表示任务的上下文。
     */
    private String context;

    /**
     * 将 {@link ModelDo} 转换为 {@link ModelData}。
     *
     * @param modelDo 表示模型数据库实体类的 {@link ModelDo}。
     * @param serializer 表示序列化器的 {@link ObjectSerializer}。
     * @return 表示模型传输层数据类的 {@link ModelData}。
     */
    public static ModelData convertToModelData(ModelDo modelDo, ObjectSerializer serializer) {
        ModelData modelData = new ModelData();
        modelData.setTaskName(modelDo.getTaskName());
        modelData.setName(modelDo.getName());
        modelData.setUrl(modelDo.getUrl());
        Map<String, Object> modelDoContext = json2obj(modelDo.getContext(), serializer);
        if (modelDoContext != null) {
            modelData.setContext(MapBuilder.<String, Object>get()
                    .put("likes", modelDoContext.getOrDefault("likes", 0))
                    .put("downloads", modelDoContext.getOrDefault("downloads", 0))
                    .put("description", modelDoContext.getOrDefault("description", StringUtils.EMPTY))
                    .build());
        }
        return modelData;
    }
}
