/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.store.support;

import static com.huawei.fitframework.inspection.Validation.notNull;

import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.annotation.Fit;
import com.huawei.fitframework.log.Logger;
import com.huawei.fitframework.serialization.ObjectSerializer;
import com.huawei.fitframework.util.CollectionUtils;
import com.huawei.jade.store.Tool;
import com.huawei.jade.store.mapper.TagMapper;
import com.huawei.jade.store.mapper.ToolMapper;
import com.huawei.jade.store.model.entity.TagDo;
import com.huawei.jade.store.model.entity.ToolDo;
import com.huawei.jade.store.model.query.ToolTagQuery;
import com.huawei.jade.store.repository.ToolRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 存入数据库的工具的实体类。
 *
 * @author 李金绪 l00878072
 * @since 2024/5/11
 */
@Component
public class DefaultToolRepository implements ToolRepository {
    private static final Logger log = Logger.get(DefaultToolRepository.class);

    private final ObjectSerializer serializer;
    private final ToolMapper toolMapper;
    private final TagMapper tagMapper;

    /**
     * 通过 mapper 接口来初始化 {@link DefaultToolRepository} 的实例。
     *
     * @param serializer 表示序列化对象的 {@link ObjectSerializer}。
     * @param toolMapper 标识操作工具的 mapper 接口的 {@link ToolMapper}。
     * @param tagMapper 标识操作标签的 mapper 接口的 {@link TagMapper}。
     */
    public DefaultToolRepository(@Fit(alias = "json") ObjectSerializer serializer, ToolMapper toolMapper,
            TagMapper tagMapper) {
        this.serializer = notNull(serializer, "The serializer cannot be null.");
        this.toolMapper = notNull(toolMapper, "The tool mapper cannot be null.");
        this.tagMapper = notNull(tagMapper, "The tag mapper cannot be null.");
    }

    /**
     * 仓储层添加工具。
     *
     * @param info 表示待增加的工具信息的 {@link Tool.Info}。
     */
    @Override
    public void addTool(Tool.Info info) {
        ToolDo toolDo = ToolDo.info2Do(info, this.serializer);
        this.toolMapper.addTool(toolDo);
    }

    /**
     * 仓储层删除工具。
     *
     * @param uniqueName 表示待删除工具唯一标识的 {@link String}。
     */
    @Override
    public void deleteTool(String uniqueName) {
        this.toolMapper.deleteTool(uniqueName);
    }

    /**
     * 仓储层根据唯一标识查询工具。
     *
     * @param uniqueName 表示工具的唯一标识的 {@link String}。
     * @return 表示工具领域对象的 {@link Optional}{@code <}{@link Tool.Info}{@code >}。
     */
    @Override
    public Optional<Tool.Info> getTool(String uniqueName) {
        ToolDo toolDo = this.toolMapper.getToolByUniqueName(uniqueName);
        if (toolDo != null) {
            return Optional.of(ToolDo.do2Info(toolDo, this.serializer));
        }
        return Optional.empty();
    }

    /**
     * 仓储层动态条件准确查询工具列表。
     *
     * @param toolTagQuery 表示动态查询条件的 {@link ToolTagQuery}。
     * @return 表示工具领域对象列表的 {@link List}{@code <}{@link Tool.Info}{@code >}。
     */
    @Override
    public List<Tool.Info> getTools(ToolTagQuery toolTagQuery) {
        List<ToolDo> toolDos = this.toolMapper.getTools(toolTagQuery);
        List<Tool.Info> infos = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(toolDos)) {
            infos.addAll(toolDos.stream()
                    .map(toolDo -> ToolDo.do2Info(toolDo, this.serializer))
                    .collect(Collectors.toList()));
        }
        return infos;
    }

    /**
     * 仓储层动态条件模糊查询工具列表。
     *
     * @param toolTagQuery 表示动态查询条件的 {@link ToolTagQuery}。
     * @return 表示工具领域对象列表的 {@link List}{@code <}{@link Tool.Info}{@code >}。
     */
    @Override
    public List<Tool.Info> searchTools(ToolTagQuery toolTagQuery) {
        List<ToolDo> toolDos = this.toolMapper.searchTools(toolTagQuery);
        List<Tool.Info> infos = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(toolDos)) {
            infos.addAll(toolDos.stream()
                    .map(toolDo -> ToolDo.do2Info(toolDo, this.serializer))
                    .collect(Collectors.toList()));
        }
        return infos;
    }

    /**
     * 仓储层添加标签。
     *
     * @param uniqueName 表示工具的唯一标识的 {@link String}。
     * @param tagName 表示工具的标签的 {@link String}。
     */
    @Override
    public void addTag(String uniqueName, String tagName) {
        TagDo tagDo = new TagDo();
        tagDo.setName(tagName);
        tagDo.setToolUniqueName(uniqueName);
        this.tagMapper.addTag(tagDo);
    }

    /**
     * 仓储层删除标签。
     *
     * @param uniqueName 表示工具的唯一标识的 {@link String}。
     * @param tagName 表示工具的标签的 {@link String}。
     */
    @Override
    public void deleteTag(String uniqueName, String tagName) {
        this.tagMapper.deleteTag(uniqueName, tagName);
    }

    /**
     * 仓储层根据工具唯一标识删除标签。
     *
     * @param uniqueName 表示商品的唯一标识的 {@link String}。
     */
    @Override
    public void deleteTagByUniqueName(String uniqueName) {
        this.tagMapper.deleteTagByUniqueName(uniqueName);
    }

    /**
     * 仓储层获取标签列表。
     *
     * @param uniqueName 表示待删除商品信息的 {@link String}。
     * @return 表示标签列表的 {@link Set}{@code <}{@link String}{@code >}。
     */
    @Override
    public Set<String> getTags(String uniqueName) {
        List<TagDo> tagDos = this.tagMapper.getTags(uniqueName);
        return tagDos.stream().map(TagDo::getName).collect(Collectors.toSet());
    }
}
