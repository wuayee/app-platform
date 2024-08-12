/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.carver.tool.repository.pgsql.service;

import static com.huawei.fitframework.util.ObjectUtils.cast;
import static com.huawei.jade.carver.validation.ValidateTagMode.validateTagMode;

import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.annotation.Fitable;
import com.huawei.fitframework.transaction.Transactional;
import com.huawei.fitframework.util.CollectionUtils;
import com.huawei.fitframework.util.StringUtils;
import com.huawei.jade.carver.ListResult;
import com.huawei.jade.carver.tool.Tool;
import com.huawei.jade.carver.tool.model.query.ToolQuery;
import com.huawei.jade.carver.tool.model.transfer.ToolData;
import com.huawei.jade.carver.tool.repository.pgsql.ToolRepository;
import com.huawei.jade.carver.tool.service.ToolService;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 工具的 Http 请求的服务层实现。
 *
 * @author 李金绪
 * @since 2024/5/10
 */
@Component
public class DefaultToolService implements ToolService {
    private static final String TOOL_DESCRIPTION = "description";

    private final ToolRepository toolRepo;

    /**
     * 通过持久层接口来初始化 {@link DefaultToolService} 的实例。
     *
     * @param toolRepo 表示持久层实例的 {@link ToolRepository}。
     */
    public DefaultToolService(ToolRepository toolRepo) {
        this.toolRepo = toolRepo;
    }

    /**
     * 服务层添加工具。
     *
     * @param toolData 表示待增加的工具信息的 {@link ToolData}。
     * @return 工具的唯一标识名。 {@link String}。
     */
    @Override
    @Fitable(id = "tool-repository-pgsql")
    @Transactional
    public String addTool(ToolData toolData) {
        toolData.setName(Objects.toString(toolData.getSchema().get("name")));
        if (toolData.getSchema().containsKey("description")) {
            toolData.setDescription(Objects.toString(toolData.getSchema().get("description")));
        }
        Tool.Info info = ToolData.convertToInfo(toolData);
        this.toolRepo.addTool(info);
        return toolData.getUniqueName();
    }

    @Override
    @Fitable(id = "tool-repository-pgsql")
    @Transactional
    public void addTools(List<ToolData> toolDataList) {
        for (ToolData toolData : toolDataList) {
            toolData.setName(Objects.toString(toolData.getSchema().get("name")));
            if (toolData.getSchema().containsKey("description")) {
                toolData.setDescription(Objects.toString(toolData.getSchema().get("description")));
            }
        }
        List<Tool.Info> infos = toolDataList.stream().map(toolData -> ToolData.convertToInfo(toolData)).collect(
                Collectors.toList());
        this.toolRepo.addTools(infos);
    }

    /**
     * 服务层删除工具。
     *
     * @param toolUniqueName 表示待删除工具唯一标识的 {@link String}。
     * @return 工具的唯一标识名或提示。 {@link String}。
     */
    @Override
    @Fitable(id = "tool-repository-pgsql")
    @Transactional
    public String deleteTool(String toolUniqueName) {
        this.toolRepo.deleteTool(toolUniqueName);
        return toolUniqueName;
    }

    @Override
    @Fitable(id = "tool-repository-pgsql")
    @Transactional
    public void deleteTools(List<String> uniqueNames) {
        this.toolRepo.deleteTools(uniqueNames);
    }

    @Override
    @Fitable(id = "tool-repository-pgsql")
    @Transactional
    public String deleteToolByVersion(String uniqueName, String version) {
        this.toolRepo.deleteToolByVersion(uniqueName, version);
        return uniqueName;
    }

    /**
     * 服务层根据唯一标识名查询工具。
     *
     * @param toolUniqueName 表示工具的唯一标识的 {@link String}。
     * @return 工具的传输对象 {@link ToolData}。
     */
    @Override
    @Fitable(id = "tool-repository-pgsql")
    @Transactional
    public ToolData getTool(String toolUniqueName) {
        Optional<Tool.Info> info = this.toolRepo.getTool(toolUniqueName);
        if (!info.isPresent()) {
            return null;
        }
        ToolData toolData = ToolData.from(info.get());
        String description = cast(toolData.getSchema().get(this.TOOL_DESCRIPTION));
        toolData.setDescription(description);
        Set<String> tagNames = this.toolRepo.getTags(toolUniqueName);
        toolData.setTags(tagNames);
        return toolData;
    }

    /**
     * 服务层动态条件准确查询工具列表
     *
     * @param toolQuery 表示动态查询条件的 {@link ToolQuery}
     * @return 工具列表 {@link List}{@code <}{@link ToolData}{@code >}。
     */
    @Override
    @Fitable(id = "tool-repository-pgsql")
    @Transactional
    public ListResult<ToolData> getTools(ToolQuery toolQuery) {
        if (toolQuery == null) {
            return ListResult.empty();
        }
        if ((toolQuery.getOffset() != null && toolQuery.getOffset() < 0) || (toolQuery.getLimit() != null
                && toolQuery.getLimit() < 0)) {
            return ListResult.empty();
        }
        toolQuery.setMode(validateTagMode(toolQuery.getMode()));
        Set<String> includeTags = toolQuery.getIncludeTags();
        toolQuery.setIncludeTags(includeTags.stream().map(StringUtils::toUpperCase).collect(Collectors.toSet()));
        Set<String> excludeTags = toolQuery.getExcludeTags();
        toolQuery.setExcludeTags(excludeTags.stream().map(StringUtils::toUpperCase).collect(Collectors.toSet()));
        List<Tool.Info> infos = this.toolRepo.getTools(toolQuery);
        ArrayList<ToolData> toolDataList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(infos)) {
            for (Tool.Info info : infos) {
                ToolData toolData = ToolData.from(info);
                String description = cast(toolData.getSchema().get(this.TOOL_DESCRIPTION));
                toolData.setDescription(description);
                Set<String> tags = this.toolRepo.getTags(toolData.getUniqueName());
                toolData.setTags(tags);
                toolDataList.add(toolData);
            }
        }

        toolQuery.setLimit(null);
        toolQuery.setOffset(null);
        int count = this.toolRepo.getToolsCount(toolQuery);
        return ListResult.create(toolDataList, count);
    }

    /**
     * 服务层动态条件模糊查询工具列表
     *
     * @param toolQuery 表示动态查询条件的 {@link ToolQuery}
     * @return 工具列表 {@link List}{@code <}{@link ToolData}{@code >}。
     */
    @Override
    @Fitable(id = "tool-repository-pgsql")
    @Transactional
    public ListResult<ToolData> searchTools(ToolQuery toolQuery) {
        if (toolQuery == null) {
            return ListResult.empty();
        }
        if ((toolQuery.getOffset() != null && toolQuery.getOffset() < 0) || (toolQuery.getLimit() != null
                && toolQuery.getLimit() < 0)) {
            return ListResult.empty();
        }
        toolQuery.setMode(validateTagMode(toolQuery.getMode()));
        Set<String> includeTags = toolQuery.getIncludeTags();
        toolQuery.setIncludeTags(includeTags.stream().map(StringUtils::toUpperCase).collect(Collectors.toSet()));
        Set<String> excludeTags = toolQuery.getExcludeTags();
        toolQuery.setExcludeTags(excludeTags.stream().map(StringUtils::toUpperCase).collect(Collectors.toSet()));
        List<Tool.Info> infos = this.toolRepo.searchTools(toolQuery);
        ArrayList<ToolData> toolDataList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(infos)) {
            for (Tool.Info info : infos) {
                ToolData toolData = ToolData.from(info);
                String description = cast(toolData.getSchema().get(this.TOOL_DESCRIPTION));
                toolData.setDescription(description);
                Set<String> tags = this.toolRepo.getTags(toolData.getUniqueName());
                toolData.setTags(tags);
                toolDataList.add(toolData);
            }
        }

        toolQuery.setLimit(null);
        toolQuery.setOffset(null);
        int count = this.toolRepo.searchToolsCount(toolQuery);
        return ListResult.create(toolDataList, count);
    }

    @Override
    @Fitable(id = "tool-repository-pgsql")
    @Transactional
    public void setNotLatest(String toolUniqueName) {
        this.toolRepo.setNotLatest(toolUniqueName);
    }

    @Override
    @Fitable(id = "tool-repository-pgsql")
    public ToolData getToolByVersion(String toolUniqueName, String version) {
        Optional<Tool.Info> info = this.toolRepo.getToolByVersion(toolUniqueName, version);
        if (!info.isPresent()) {
            return null;
        }
        ToolData toolData = ToolData.from(info.get());
        String description = cast(toolData.getSchema().get(this.TOOL_DESCRIPTION));
        toolData.setDescription(description);
        Set<String> tagNames = this.toolRepo.getTags(toolUniqueName);
        toolData.setTags(tagNames);
        return toolData;
    }

    @Override
    @Fitable(id = "tool-repository-pgsql")
    @Transactional
    public void setLatest(String toolUniqueName, String version) {
        this.toolRepo.setLatest(toolUniqueName, version);
    }

    @Override
    @Fitable(id = "tool-repository-pgsql")
    public ListResult<ToolData> getAllToolVersions(ToolQuery toolQuery) {
        List<Tool.Info> infos = this.toolRepo.getAllToolVersions(toolQuery);
        ArrayList<ToolData> toolDataList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(infos)) {
            Set<String> tags = this.toolRepo.getTags(infos.get(0).uniqueName());
            for (Tool.Info info : infos) {
                ToolData toolData = ToolData.from(info);
                String description = cast(toolData.getSchema().get(this.TOOL_DESCRIPTION));
                toolData.setDescription(description);
                toolData.setTags(tags);
                toolDataList.add(toolData);
            }
        }

        toolQuery.setLimit(null);
        toolQuery.setOffset(null);
        int count = this.toolRepo.getAllToolVersionsCount(toolQuery);
        return ListResult.create(toolDataList, count);
    }
}
