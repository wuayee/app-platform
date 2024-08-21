/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.knowledge.service;

import modelengine.fitframework.annotation.Genericable;
import com.huawei.jade.app.engine.knowledge.dto.KTableDto;
import com.huawei.jade.app.engine.knowledge.service.param.PageQueryParam;

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
     * @return 新创建的知识表id
     */
    @Genericable(id = "com.huawei.jade.app.engine.knowledge.service.KTableService.create")
    Long create(KTableDto kTableDto);

    /**
     * 获取知识表。
     *
     * @param id 表示知识表ID的 {@link Long}。
     * @return 返回知识表。
     */
    @Genericable(id = "com.huawei.jade.app.engine.knowledge.service.KTableService.getById")
    KTableDto getById(Long id);

    /**
     * 分页查询某个知识库下的知识表。
     *
     * @param repoId 表示知识库ID的 {@link Long}。
     * @param param 分页查询参数
     * @return 返回知识表列表。
     */
    @Genericable(id = "com.huawei.jade.app.engine.knowledge.service.KTableService.getByRepoId")
    List<KTableDto> getByRepoId(Long repoId, PageQueryParam param);

    /**
     * 更新知识表。
     *
     * @param kTableDto 表示知识表的 {@link KTableDto}。
     */
    @Genericable(id = "com.huawei.jade.app.engine.knowledge.service.KTableService.update")
    void update(KTableDto kTableDto);

    /**
     * 删除知识表。
     *
     * @param id 表示知识表ID的 {@link Long}。
     */
    @Genericable(id = "com.huawei.jade.app.engine.knowledge.service.KTableService.delete")
    void delete(Long id);

    /**
     * 获取某知识库下知识表总数
     *
     * @param repositoryId 知识库ID
     * @return 知识表总数
     */
    @Genericable(id = "com.huawei.jade.app.engine.knowledge.service.KTableService.getTableCountByRepoId")
    Integer getTableCountByRepoId(Long repositoryId);
}
