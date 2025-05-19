/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.knowledge;

import modelengine.fitframework.annotation.Genericable;
import modelengine.fitframework.annotation.Property;
import modelengine.fel.tool.annotation.Group;
import modelengine.fel.tool.annotation.ToolMethod;
import modelengine.jade.knowledge.dto.KnowledgeConfigDto;
import modelengine.jade.knowledge.dto.KnowledgeDto;

import java.util.List;

/**
 * 表示用户知识库配置信息的接口
 *
 * @author 陈潇文
 * @since 2025-04-22
 */
@Group(name = "Knowledge_Center_Service")
public interface KnowledgeCenterService {
    /**
     * 增加用户的知识库配置信息。
     *
     * @param knowledgeConfigDto 表示用户知识库配置dto的 {@link KnowledgeConfigDto}。
     */
    @ToolMethod(name = "add_user_knowledge_config", description = "增加用户的知识库配置信息")
    @Genericable(id = "knowledge.center.service.addUserKnowledgeConfig")
    void add(@Property(description = "知识库配置dto", required = true) KnowledgeConfigDto knowledgeConfigDto);

    /**
     * 修改用户的知识库配置信息。
     *
     * @param knowledgeConfigDto 表示用户知识库配置dto的 {@link KnowledgeConfigDto}。
     */
    @ToolMethod(name = "edit_user_knowledge_config", description = "修改用户的知识库配置信息")
    @Genericable(id = "knowledge.center.service.editUserKnowledgeConfig")
    void edit(@Property(description = "知识库配置dto", required = true) KnowledgeConfigDto knowledgeConfigDto);

    /**
     * 删除用户的知识库配置信息。
     *
     * @param id 表示知识库配置id的 {@link String}。
     */
    @ToolMethod(name = "delete_user_knowledge_config", description = "删除用户的知识库配置信息")
    @Genericable(id = "knowledge.center.service.deleteUserKnowledgeConfig")
    void delete(@Property(description = "知识库配置id", required = true) Long id);

    /**
     * 查询用户的知识库配置信息。
     *
     * @param userId 表示用户 id 的 {@link String}。
     * @return 表示用户的知识库配置信息列表的 {@link List}{@code <}{@link KnowledgeConfigDto}{@code >}。
     */
    @ToolMethod(name = "list_user_knowledge_config", description = "查询用户的知识库配置信息")
    @Genericable(id = "knowledge.center.service.listUserKnowledgeConfig")
    List<KnowledgeConfigDto> list(@Property(description = "用户id", required = true) String userId);

    /**
     * 获取支持使用的知识库集列表。
     *
     * @return 表示支持使用的知识库集列表的 {@link List}{@code <}{@link KnowledgeDto}{@code >}。
     */
    @ToolMethod(name = "list_support_knowledges", description = "获取支持使用的知识库集列表")
    @Genericable(id = "knowledge.center.service.listSupportKnowledges")
    List<KnowledgeDto> getSupportKnowledges(@Property(description = "用户id", required = false) String userId);

    /**
     * 基于用户名，知识库平台获取 api Key。
     *
     * @param knowledgeConfigId 表示知识库配置唯一 id 的 {@link String}。
     * @param defaultValue 表示 api key 默认值的 {@link String}。
     * @return 表示 api key 的 {@link String}。
     */
    @Genericable("knowledge.center.service.getApiKey")
    String getApiKey(String knowledgeConfigId, String defaultValue);

    /**
     * 基于用户名，知识库平台获取 config 唯一 id。
     *
     * @param userId 表示用户 id 的 {@link String}。
     * @param groupId 表示知识库平台 groupId 的 {@link String}。
     * @return 表示 config 唯一 id 的 {@link String}。
     */
    @Genericable("knowledge.center.service.getKnowledgeConfigId")
    String getKnowledgeConfigId(String userId, String groupId);
}
