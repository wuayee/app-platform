/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.store.repository.pgsql.repository.support;

import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.transaction.Transactional;
import modelengine.jade.store.entity.query.PluginToolQuery;
import modelengine.jade.store.entity.transfer.PluginToolData;
import modelengine.jade.store.repository.pgsql.entity.PluginToolDo;
import modelengine.jade.store.repository.pgsql.mapper.PluginToolMapper;
import modelengine.jade.store.repository.pgsql.repository.PluginToolRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 插件工具的仓库。
 *
 * @author 鲁为
 * @since 2024-07-18
 */
@Component
public class DefaultPluginToolRepository implements PluginToolRepository {
    private final PluginToolMapper pluginToolMapper;

    /**
     * 通过 Mapper 来初始化 {@link DefaultPluginToolRepository} 的实例。
     *
     * @param pluginToolMapper 表示持久层实例的 {@link PluginToolMapper}。
     */
    public DefaultPluginToolRepository(PluginToolMapper pluginToolMapper) {
        this.pluginToolMapper = pluginToolMapper;
    }

    @Override
    @Transactional
    public void addPluginTool(PluginToolData pluginToolData) {
        PluginToolDo pluginToolDo = PluginToolDo.fromPluginToolData(pluginToolData);
        this.pluginToolMapper.addPluginTool(pluginToolDo);
    }

    @Override
    @Transactional
    public void addPluginTools(List<PluginToolData> pluginToolDataList) {
        List<PluginToolDo> pluginToolDoList =
                pluginToolDataList.stream().map(PluginToolDo::fromPluginToolData).collect(Collectors.toList());
        this.pluginToolMapper.addPluginTools(pluginToolDoList);
    }

    @Override
    @Transactional
    public void deletePluginTool(String toolUniqueName) {
        this.pluginToolMapper.deletePluginTool(toolUniqueName);
    }

    @Override
    public List<PluginToolDo> getPluginTools(PluginToolQuery pluginToolQuery) {
        return this.pluginToolMapper.getPluginTools(pluginToolQuery);
    }

    @Override
    public List<PluginToolDo> getPluginTools(String pluginId) {
        return this.pluginToolMapper.getPluginToolsByPluginId(pluginId);
    }

    @Override
    public List<PluginToolDo> getPluginTools(List<String> uniqueNames) {
        return this.pluginToolMapper.getPluginToolsByUniqueNames(uniqueNames);
    }

    @Override
    public int getPluginToolsCount(PluginToolQuery pluginToolQuery) {
        return this.pluginToolMapper.getPluginToolsCount(pluginToolQuery);
    }

    @Override
    public List<Boolean> hasPluginTools(List<String> uniqueNames) {
        List<Boolean> checkList = new ArrayList<>();
        for (String uniqueName : uniqueNames) {
            if (this.pluginToolMapper.getPluginToolByUniqueName(uniqueName) != null) {
                checkList.add(true);
            } else {
                checkList.add(false);
            }
        }
        return checkList;
    }

    @Override
    public PluginToolDo getPluginToolByUniqueName(String toolUniqueName) {
        return this.pluginToolMapper.getPluginToolByUniqueName(toolUniqueName);
    }
}
