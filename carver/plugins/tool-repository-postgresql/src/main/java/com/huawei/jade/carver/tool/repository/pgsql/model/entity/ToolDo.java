/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.carver.tool.repository.pgsql.model.entity;

import com.huawei.fitframework.serialization.ObjectSerializer;
import com.huawei.fitframework.util.TypeUtils;
import com.huawei.jade.carver.tool.Tool;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.reflect.Type;
import java.util.Map;

/**
 * 存入数据库的工具的实体类。
 *
 * @author 李金绪 l00878072
 * @since 2024/5/10
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ToolDo {
    /**
     * 表示工具的自增主键。
     */
    private Long id;

    /**
     * 表示工具的创建者。
     */
    private String creator;

    /**
     * 表示工具的修改者。
     */
    private String modifier;

    /**
     * 表示工具的名字。
     */
    private String name;

    /**
     * 表示工具的描述。
     */
    private String description;

    /**
     * 表示工具的结构。
     */
    private String schema;

    /**
     * 表示工具的运行描述。
     */
    private String runnables;

    /**
     * 表示工具的来源。
     */
    private String source;

    /**
     * 表示工具的图标。
     */
    private String icon;

    /**
     * 表示工具的唯一标识。
     */
    private String uniqueName;

    /**
     * 将领域对象转换为数据对象实体类。
     *
     * @param serializer 表示序列化对象的 {@link ObjectSerializer}。
     * @param info 表示领域类的 {@link Tool.Info}。
     * @return 工具实体类的 {@link ToolDo}。
     */
    public static ToolDo info2Do(Tool.Info info, ObjectSerializer serializer) {
        ToolDo toolDo = new ToolDo();
        toolDo.setCreator(info.creator());
        toolDo.setModifier(info.modifier());
        toolDo.setName(info.name());
        toolDo.setSchema(serializer.serialize(info.schema()));
        toolDo.setRunnables(serializer.serialize(info.runnables()));
        toolDo.setSource(info.source());
        toolDo.setIcon(info.icon());
        toolDo.setUniqueName(info.uniqueName());
        toolDo.setDescription(info.description());
        return toolDo;
    }

    /**
     * 将数据对象实体类象转换为领域对象。
     *
     * @param toolDo 表示工具实体类的 {@link ToolDo}。
     * @param serializer 表示序列化对象的 {@link ObjectSerializer}。
     * @return 领域类的 {@link Tool.Info}。
     */
    public static Tool.Info do2Info(ToolDo toolDo, ObjectSerializer serializer) {
        return Tool.Info.custom()
                .creator(toolDo.getCreator())
                .modifier(toolDo.getModifier())
                .name(toolDo.getName())
                .schema(json2obj(toolDo.getSchema(), serializer))
                .runnables(json2obj(toolDo.getRunnables(), serializer))
                .source(toolDo.getSource())
                .icon(toolDo.getIcon())
                .uniqueName(toolDo.getUniqueName())
                .description(toolDo.getDescription())
                .build();
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
