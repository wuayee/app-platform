/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.knowledge.service;

import com.huawei.fitframework.annotation.Genericable;
import com.huawei.jade.app.engine.knowledge.dto.KTableDto;

import java.util.List;

/**
 * 知识表服务的管理接口。
 *
 * @since 2024-05-18
 */
public interface KTableService {
    /**
     * 创建知识表。
     *
     * @param kTableDto 表示知识表的 {@link KTableDto}
     */
    @Genericable(id = "com.huawei.jade.app.engine.knowledge.service.KTableService.create")
    void create(KTableDto kTableDto);

    /**
     * 获取知识表。
     *
     * @param id 表示知识表ID的 {@link Long}。
     * @return 返回知识表。
     */
    @Genericable(id = "com.huawei.jade.app.engine.knowledge.service.KTableService.getById")
    KTableDto getById(Long id);

    /**
     * 获取某个知识库下的所有知识表。
     *
     * @param repoId 表示知识库ID的 {@link Long}。
     * @return 返回知识表列表。
     */
    @Genericable(id = "com.huawei.jade.app.engine.knowledge.service.KTableService.getByRepoId")
    List<KTableDto> getByRepoId(Long repoId);

    /**
     * 更新知识表。
     *
     * @param id 表示知识表ID的 {@link Long}。
     * @param kTableDto 表示知识表的 {@link KTableDto}。
     */
    @Genericable(id = "com.huawei.jade.app.engine.knowledge.service.KTableService.update")
    void update(Long id, KTableDto kTableDto);

    /**
     * 删除知识表。
     *
     * @param id 表示知识表ID的 {@link Long}。
     */
    @Genericable(id = "com.huawei.jade.app.engine.knowledge.service.KTableService.delete")
    void delete(Long id);
}
