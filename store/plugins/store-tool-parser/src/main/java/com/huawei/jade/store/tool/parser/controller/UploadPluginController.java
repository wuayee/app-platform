/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.store.tool.parser.controller;

import static com.huawei.fitframework.inspection.Validation.notBlank;
import static com.huawei.fitframework.inspection.Validation.notNull;

import com.huawei.fit.http.annotation.DeleteMapping;
import com.huawei.fit.http.annotation.GetMapping;
import com.huawei.fit.http.annotation.PathVariable;
import com.huawei.fit.http.annotation.PostMapping;
import com.huawei.fit.http.annotation.RequestBody;
import com.huawei.fit.http.annotation.RequestMapping;
import com.huawei.fit.http.annotation.RequestParam;
import com.huawei.fit.http.entity.NamedEntity;
import com.huawei.fit.http.entity.PartitionedEntity;
import com.huawei.fitframework.annotation.Component;
import com.huawei.jade.common.Result;
import com.huawei.jade.store.entity.transfer.PluginData;
import com.huawei.jade.store.service.support.DeployStatus;
import com.huawei.jade.store.tool.parser.code.PluginDeployRetCode;
import com.huawei.jade.store.tool.parser.exception.PluginDeployException;
import com.huawei.jade.store.tool.parser.param.DeployParam;
import com.huawei.jade.store.tool.parser.service.PluginDeployService;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 表示上传文件的控制器。
 *
 * @author 杭潇
 * @since 2024-07-11
 */
@Component
@RequestMapping("/plugins")
public class UploadPluginController {
    private final PluginDeployService pluginDeployService;

    /**
     * 通过插件服务来初始化 {@link UploadPluginController} 的新实例。
     *
     * @param pluginDeployService 表示插件部署服务的 {@link PluginDeployService}。
     */
    public UploadPluginController(PluginDeployService pluginDeployService) {
        this.pluginDeployService = notNull(pluginDeployService, "The plugin deploy service cannot be null.");
    }

    /**
     * 表示保存上传工具文件的请求。
     *
     * @param receivedFiles 表示分块的消息体数据的 {@link PartitionedEntity}。
     * @param toolNames 表示工具名的列表的 {@link String}。
     * @return 表示格式化之后的返回消息的 {@link Result}{@code <}{@link String}{@code >}。
     */
    @PostMapping(path = "/save", description = "保存上传工具文件")
    public Result<String> saveUploadFile(PartitionedEntity receivedFiles, @RequestParam("toolNames") String toolNames) {
        notNull(receivedFiles, "The file to be uploaded cannot be null.");
        notNull(toolNames, "The tools name cannot be null.");
        List<NamedEntity> entityList = receivedFiles.entities()
            .stream()
            .filter(NamedEntity::isFile)
            .collect(Collectors.toList());
        if (entityList.isEmpty()) {
            throw new PluginDeployException(PluginDeployRetCode.NO_FILE_UPLOADED_ERROR);
        }
        this.pluginDeployService.uploadPlugins(entityList, toolNames);
        return Result.ok(null, 1);
    }

    /**
     * 删除插件的请求。
     *
     * @param pluginId 表示插件唯一标识的 {@link String}。
     * @return 表示格式化之后的返回消息的 {@link Result}{@code <}{@link String}{@code >}。
     */
    @DeleteMapping(value = "/delete/{pluginId}", description = "删除插件")
    public Result<String> deletePlugin(@PathVariable("pluginId") String pluginId) {
        notBlank(pluginId, "The plugin id cannot be blank.");
        int deleteNum = this.pluginDeployService.deletePlugin(pluginId);
        return Result.ok(null, deleteNum);
    }

    /**
     * 部署插件。
     *
     * @param deployParam 表示部署参数的 {@link DeployParam}。
     * @return 表示格式化之后的返回消息的 {@link Result}{@code <}{@link String}{@code >}。
     */
    @PostMapping(path = "/deploy", description = "部署插件")
    public Result<String> deployPlugin(@RequestBody DeployParam deployParam) {
        notNull(deployParam, "The deploy param cannot be null.");
        List<String> pluginIds = deployParam.getPluginIds();
        this.pluginDeployService.deployPlugins(pluginIds);
        return Result.ok(null, pluginIds.size());
    }

    /**
     * 查询部署中的插件。
     *
     * @param status 表示插件部署状态的 {@link String}。
     * @return 表示格式化之后的返回消息的 {@link Result}{@code <}{@link List}{@code <}{@link PluginData}{@code >}{@code >}。
     */
    @GetMapping(path = "/by-status/{deploy-status}", description = "查询部署中的插件")
    public Result<List<PluginData>> queryDeployingCount(@PathVariable("deploy-status") String status) {
        DeployStatus deployStatus = DeployStatus.from(status);
        return Result.ok(this.pluginDeployService.queryPluginsByDeployStatus(deployStatus),
            this.pluginDeployService.queryCountByDeployStatus(deployStatus));
    }
}