/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.carver.tool.repository.pgsql.repository.support;

import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.jade.carver.tool.Tool;
import modelengine.jade.carver.tool.model.entity.ToolIdentifier;
import modelengine.jade.carver.tool.repository.pgsql.mapper.ToolMapper;
import modelengine.jade.carver.tool.repository.pgsql.model.entity.ToolDo;
import modelengine.jade.carver.tool.repository.pgsql.repository.ToolRepository;

import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.serialization.ObjectSerializer;
import modelengine.fitframework.transaction.Transactional;

import java.util.List;
import java.util.Optional;
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

    /**
     * 通过 mapper 接口来初始化 {@link DefaultToolRepository} 的实例。
     *
     * @param serializer 表示序列化对象的 {@link ObjectSerializer}。
     * @param toolMapper 标识操作工具的 mapper 接口的 {@link ToolMapper}。
     */
    public DefaultToolRepository(@Fit(alias = "json") ObjectSerializer serializer, ToolMapper toolMapper) {
        this.serializer = notNull(serializer, "The serializer cannot be null.");
        this.toolMapper = notNull(toolMapper, "The tool mapper cannot be null.");
    }

    @Override
    @Transactional
    public void addTool(Tool.ToolInfo info) {
        ToolDo toolDo = ToolDo.info2Do(info.definitionGroupName(), info.groupName(), info, this.serializer);
        this.toolMapper.addTool(toolDo);
    }

    @Override
    @Transactional
    public void addTools(List<Tool.ToolInfo> infos) {
        List<ToolDo> toolDoList = infos.stream()
                .map(info -> ToolDo.info2Do(info.definitionGroupName(), info.groupName(), info, this.serializer))
                .collect(Collectors.toList());
        this.toolMapper.addTools(toolDoList);
    }

    @Override
    @Transactional
    public void addTools(String definitionGroupName, String groupName, List<Tool.ToolInfo> infos) {
        List<ToolDo> toolDoList = infos.stream()
                .map(info -> ToolDo.info2Do(definitionGroupName, groupName, info, this.serializer))
                .collect(Collectors.toList());
        this.toolMapper.addTools(toolDoList);
    }

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
    public void deleteTools(String definitionGroupName) {
        this.toolMapper.deleteToolsByDefinitionGroupName(definitionGroupName);
    }

    @Override
    @Transactional
    public void deleteTools(String definitionGroupName, String groupName) {
        this.toolMapper.deleteToolsByGroupName(definitionGroupName, groupName);
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

    @Override
    @Transactional
    public void setNotLatest(String toolUniqueName) {
        this.toolMapper.setNotLatest(toolUniqueName);
    }

    @Override
    public Optional<Tool.ToolInfo> getTool(String uniqueName) {
        ToolDo toolDo = this.toolMapper.getToolByUniqueName(uniqueName);
        if (toolDo != null) {
            return Optional.of(ToolDo.do2Info(toolDo, this.serializer));
        }
        return Optional.empty();
    }

    @Override
    public List<Tool.ToolInfo> getTools(String definitionGroupName) {
        return this.getToolInfos(this.toolMapper.getToolsByDefinitionGroupName(definitionGroupName));
    }

    private List<Tool.ToolInfo> getToolInfos(List<ToolDo> toolDos) {
        return toolDos.stream().map(toolDo -> ToolDo.do2Info(toolDo, this.serializer)).collect(Collectors.toList());
    }

    @Override
    public List<Tool.ToolInfo> getTools(String definitionGroupName, String groupName) {
        return this.getToolInfos(this.toolMapper.getToolsByGroupName(definitionGroupName, groupName));
    }

    @Override
    public Optional<Tool.ToolInfo> getToolByVersion(String toolUniqueName, String version) {
        ToolDo toolDo = this.toolMapper.getToolByVersion(toolUniqueName, version);
        return Optional.ofNullable(toolDo).map(tool -> ToolDo.do2Info(tool, this.serializer));
    }

    @Override
    public List<Tool.ToolInfo> getAllToolVersions(String toolUniqueName) {
        List<ToolDo> toolDos = this.toolMapper.getAllToolVersions(toolUniqueName);
        return toolDos.stream().map(toolDo -> ToolDo.do2Info(toolDo, this.serializer)).collect(Collectors.toList());
    }

    @Override
    public int getAllToolVersionsCount(String toolUniqueName) {
        return this.toolMapper.getAllToolVersionsCount(toolUniqueName);
    }

    @Override
    public List<Tool.ToolInfo> getToolsByIdentifier(List<ToolIdentifier> toolIdentifiers) {
        List<ToolDo> toolDos = this.toolMapper.getToolsByIdentifier(toolIdentifiers);
        return toolDos.stream().map(toolDo -> ToolDo.do2Info(toolDo, this.serializer)).collect(Collectors.toList());
    }
}
