/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.carver.tool.repository.pgsql.service;

import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.annotation.Fitable;
import com.huawei.fitframework.transaction.Transactional;
import com.huawei.fitframework.util.CollectionUtils;
import com.huawei.fitframework.util.StringUtils;
import com.huawei.jade.carver.ListResult;
import com.huawei.jade.carver.tool.Tool;
import com.huawei.jade.carver.tool.model.query.ToolQuery;
import com.huawei.jade.carver.tool.model.transfer.ToolData;
import com.huawei.jade.carver.tool.repository.ToolRepository;
import com.huawei.jade.carver.tool.service.ToolService;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 工具的 Http 请求的服务层实现。
 *
 * @author 李金绪 l00878072
 * @since 2024/5/10
 */
@Component
public class DefaultToolService implements ToolService {
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
        toolData.setUniqueName(UUID.randomUUID().toString());
        toolData.setName(Objects.toString(toolData.getSchema().get("name")));
        if (toolData.getSchema().containsKey("description")) {
            toolData.setDescription(Objects.toString(toolData.getSchema().get("description")));
        }
        Tool.Info info = ToolData.convertToInfo(toolData);
        this.toolRepo.addTool(info);
        String uniqueName = toolData.getUniqueName();
        Set<String> tagNames = toolData.getTags();
        if (CollectionUtils.isNotEmpty(tagNames)) {
            tagNames.forEach(tagName -> this.addTag(uniqueName, tagName));
        }
        return uniqueName;
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
        this.toolRepo.deleteTagByUniqueName(toolUniqueName);
        return toolUniqueName;
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
        if ((toolQuery.getPageNum() != null && toolQuery.getPageNum() < 0) || (toolQuery.getLimit() != null
                && toolQuery.getLimit() < 0)) {
            return ListResult.empty();
        }
        Set<String> includeTags = toolQuery.getIncludeTags();
        toolQuery.setIncludeTags(includeTags.stream().map(StringUtils::toUpperCase).collect(Collectors.toSet()));
        Set<String> excludeTags = toolQuery.getExcludeTags();
        toolQuery.setExcludeTags(excludeTags.stream().map(StringUtils::toUpperCase).collect(Collectors.toSet()));
        List<Tool.Info> infos = this.toolRepo.getTools(toolQuery);
        ArrayList<ToolData> toolDataList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(infos)) {
            for (Tool.Info info : infos) {
                ToolData toolData = ToolData.from(info);
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
        if ((toolQuery.getPageNum() != null && toolQuery.getPageNum() < 0) || (toolQuery.getLimit() != null
                && toolQuery.getLimit() < 0)) {
            return ListResult.empty();
        }
        Set<String> includeTags = toolQuery.getIncludeTags();
        toolQuery.setIncludeTags(includeTags.stream().map(StringUtils::toUpperCase).collect(Collectors.toSet()));
        Set<String> excludeTags = toolQuery.getExcludeTags();
        toolQuery.setExcludeTags(excludeTags.stream().map(StringUtils::toUpperCase).collect(Collectors.toSet()));
        List<Tool.Info> infos = this.toolRepo.searchTools(toolQuery);
        ArrayList<ToolData> toolDataList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(infos)) {
            for (Tool.Info info : infos) {
                ToolData toolData = ToolData.from(info);
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

    /**
     * 服务层添加标签
     *
     * @param toolUniqueName 表示工具的唯一标识的 {@link String}。
     * @param tag 表示待添加的工具标签的 {@link String}。
     */
    @Override
    @Fitable(id = "tool-repository-pgsql")
    public void addTag(String toolUniqueName, String tag) {
        if (StringUtils.isBlank(tag)) {
            return;
        }
        this.toolRepo.addTag(toolUniqueName, tag);
    }

    /**
     * 服务层删除标签
     *
     * @param toolUniqueName 表示工具的唯一标识的 {@link String}。
     * @param tagName 表示待删除的工具标签的 {@link String}。
     */
    @Override
    @Fitable(id = "tool-repository-pgsql")
    public void deleteTag(String toolUniqueName, String tagName) {
        this.toolRepo.deleteTag(toolUniqueName, tagName);
    }
}
