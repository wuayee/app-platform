/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.knowledge.service;

import com.huawei.fitframework.annotation.Genericable;
import com.huawei.jade.app.engine.knowledge.dto.KRepoDto;

import java.util.List;

/**
 * 知识库服务的管理接口。
 *
 * @since 2024-05-18
 */
public interface KRepoService {
    /**
     * 获取所有知识库。
     *
     * @return 返回知识库列表。
     */
    @Genericable(id = "com.huawei.jade.app.engine.knowledge.service.KRepoService.getAllRepos")
    List<KRepoDto> getAllRepos();

    /**
     * 根据ID获取知识库。
     *
     * @param id 表示id的 {@link Long}。
     * @return 返回对应的知识库记录
     */
    @Genericable(id = "com.huawei.jade.app.engine.knowledge.service.KRepoService.getById")
    KRepoDto getById(Long id);

    /**
     * 创建知识库。
     *
     * @param kRepoDto 表示知识库记录的 {@link KRepoDto}。
     */
    @Genericable(id = "com.huawei.jade.app.engine.knowledge.service.KRepoService.create")
    void create(KRepoDto kRepoDto);

    /**
     * 删除知识库。
     *
     * @param id 表示知识库ID的 {@link Long}。
     */
    @Genericable(id = "com.huawei.jade.app.engine.knowledge.service.KRepoService.delete")
    void delete(Long id);

    /**
     * 更新知识库
     *
     * @param id 表示知识库ID的 {@link Long}。
     * @param kRepoDto 表示知识库记录的 {@link KRepoDto}。
     */
    @Genericable(id = "com.huawei.jade.app.engine.knowledge.service.KRepoService.update")
    void update(Long id, KRepoDto kRepoDto);
}
