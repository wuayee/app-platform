/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.carver.tool.repository.pgsql.support;

import static com.huawei.fitframework.inspection.Validation.notNull;

import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.annotation.Fit;
import com.huawei.fitframework.log.Logger;
import com.huawei.fitframework.serialization.ObjectSerializer;
import com.huawei.fitframework.transaction.Transactional;
import com.huawei.fitframework.util.CollectionUtils;
import com.huawei.jade.carver.tool.Tool;
import com.huawei.jade.carver.tool.model.query.ToolQuery;
import com.huawei.jade.carver.tool.repository.pgsql.ToolRepository;
import com.huawei.jade.carver.tool.repository.pgsql.mapper.TagMapper;
import com.huawei.jade.carver.tool.repository.pgsql.mapper.ToolMapper;
import com.huawei.jade.carver.tool.repository.pgsql.model.entity.TagDo;
import com.huawei.jade.carver.tool.repository.pgsql.model.entity.ToolDo;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 存入数据库的工具的实体类。
 *
 * @author 李金绪
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
    @Transactional
    public void addTool(Tool.Info info) {
        ToolDo toolDo = ToolDo.info2Do(info, this.serializer);
        this.toolMapper.addTool(toolDo);
    }

    @Override
    @Transactional
    public void addTools(List<Tool.Info> infos) {
        List<ToolDo> toolDoList = infos.stream()
                .map(info -> ToolDo.info2Do(info, this.serializer))
                .collect(Collectors.toList());
        this.toolMapper.addTools(toolDoList);
    }

    /**
     * 仓储层删除工具。
     *
     * @param uniqueName 表示待删除工具唯一标识的 {@link String}。
     */
    @Override
    @Transactional
    public void deleteTool(String uniqueName) {
        this.toolMapper.deleteTool(uniqueName);
    }

    @Override
    @Transactional
    public void deleteTools(List<String> uniqueNames) {
        this.toolMapper.deleteTools(uniqueNames);
    }

    @Override
    @Transactional
    public String deleteToolByVersion(String uniqueName, String version) {
        this.toolMapper.deleteToolByVersion(uniqueName, version);
        return uniqueName;
    }

    @Override
    @Transactional
    public void setLatest(String uniqueName, String version) {
        this.toolMapper.setLatest(uniqueName, version);
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
     * @param toolQuery 表示动态查询条件的 {@link ToolQuery}。
     * @return 表示工具领域对象列表的 {@link List}{@code <}{@link Tool.Info}{@code >}。
     */
    @Override
    public List<Tool.Info> getTools(ToolQuery toolQuery) {
        List<ToolDo> toolDos = this.toolMapper.getTools(toolQuery);
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
     * @param toolQuery 表示动态查询条件的 {@link ToolQuery}。
     * @return 表示工具领域对象列表的 {@link List}{@code <}{@link Tool.Info}{@code >}。
     */
    @Override
    public List<Tool.Info> searchTools(ToolQuery toolQuery) {
        List<ToolDo> toolDos = this.toolMapper.searchTools(toolQuery);
        List<Tool.Info> infos = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(toolDos)) {
            infos.addAll(toolDos.stream()
                    .map(toolDo -> ToolDo.do2Info(toolDo, this.serializer))
                    .collect(Collectors.toList()));
        }
        return infos;
    }

    /**
     * 仓储层动态条件准确查询工具总数。
     *
     * @param toolQuery 表示动态查询条件的 {@link ToolQuery}。
     * @return 表示工具总数的 {@code int}。
     */
    @Override
    public int getToolsCount(ToolQuery toolQuery) {
        return toolMapper.getToolsCount(toolQuery);
    }

    /**
     * 仓储层动态条件模糊查询工具总数。
     *
     * @param toolQuery 表示动态查询条件的 {@link ToolQuery}。
     * @return 表示工具总数的 {@code int}。
     */
    @Override
    public int searchToolsCount(ToolQuery toolQuery) {
        return toolMapper.searchToolsCount(toolQuery);
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

    @Override
    @Transactional
    public void setNotLatest(String toolUniqueName) {
        this.toolMapper.setNotLatest(toolUniqueName);
    }

    @Override
    public Optional<Tool.Info> getToolByVersion(String toolUniqueName, String version) {
        ToolDo toolDo = this.toolMapper.getToolByVersion(toolUniqueName, version);
        return Optional.ofNullable(toolDo).map(tool -> ToolDo.do2Info(tool, this.serializer));
    }

    @Override
    public List<Tool.Info> getAllToolVersions(ToolQuery toolQuery) {
        List<ToolDo> toolDos = this.toolMapper.getAllToolVersions(toolQuery);
        List<Tool.Info> infos = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(toolDos)) {
            infos.addAll(toolDos.stream()
                    .filter(Objects::nonNull)
                    .map(toolDo -> ToolDo.do2Info(toolDo, this.serializer))
                    .collect(Collectors.toList()));
        }
        return infos;
    }

    @Override
    public int getAllToolVersionsCount(ToolQuery toolQuery) {
        return this.toolMapper.getAllToolVersionsCount(toolQuery);
    }
}
