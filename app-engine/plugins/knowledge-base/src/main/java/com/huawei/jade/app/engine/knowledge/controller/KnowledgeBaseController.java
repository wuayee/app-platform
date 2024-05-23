/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.knowledge.controller;

import com.huawei.fit.http.annotation.DeleteMapping;
import com.huawei.fit.http.annotation.GetMapping;
import com.huawei.fit.http.annotation.PathVariable;
import com.huawei.fit.http.annotation.PostMapping;
import com.huawei.fit.http.annotation.PutMapping;
import com.huawei.fit.http.annotation.RequestBody;
import com.huawei.fit.http.annotation.RequestMapping;
import com.huawei.fit.http.protocol.HttpResponseStatus;
import com.huawei.fit.http.server.HttpServerException;
import com.huawei.fit.http.server.HttpServerResponseException;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.annotation.Fit;
import com.huawei.fitframework.log.Logger;
import com.huawei.fitframework.util.CollectionUtils;
import com.huawei.jade.app.engine.knowledge.dto.KRepoDto;
import com.huawei.jade.app.engine.knowledge.dto.KStorageDto;
import com.huawei.jade.app.engine.knowledge.dto.KTableDto;
import com.huawei.jade.app.engine.knowledge.dto.KbChunkQueryDto;
import com.huawei.jade.app.engine.knowledge.dto.KbGenerateConfigDto;
import com.huawei.jade.app.engine.knowledge.dto.TableKnowledgeColDto;
import com.huawei.jade.app.engine.knowledge.params.RepoQueryParam;
import com.huawei.jade.app.engine.knowledge.params.TableKnowledgeParam;
import com.huawei.jade.app.engine.knowledge.service.KRepoService;
import com.huawei.jade.app.engine.knowledge.service.KStorageService;
import com.huawei.jade.app.engine.knowledge.service.KTableService;
import com.huawei.jade.app.engine.knowledge.service.KbGenerateService;
import com.huawei.jade.app.engine.knowledge.service.param.PageQueryParam;
import com.huawei.jade.app.engine.knowledge.vo.PageResultVo;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 处理知识库相关的HTTP请求。
 *
 * @since 2024-05-18
 */
@Component
@RequestMapping("/knowledge")
public class KnowledgeBaseController {
    private static final Logger LOGGER = Logger.get(KnowledgeBaseController.class);

    @Fit
    private KTableService kTableService;

    @Fit
    private KRepoService kRepoService;

    @Fit
    private KStorageService kStorageService;

    @Fit
    private KbGenerateService kbGenerateService;

    /**
     * 通过名称查找知识库列表
     *
     * @param param 查询参数
     * @return 知识库列表
     */
    @PostMapping("/repos/list")
    public PageResultVo<KRepoDto> getReposByName(@RequestBody RepoQueryParam param) {
        return new PageResultVo<>(getReposCount(param), kRepoService.queryReposByName(param));
    }

    private int getReposCount(RepoQueryParam param) {
        return kRepoService.queryReposCount(param);
    }

    /**
     * 获取所有的知识库。
     *
     * @return 返回知识库列表
     */
    @GetMapping("/repos")
    public List<KRepoDto> getAllRepos() {
        return kRepoService.getAllRepos();
    }

    /**
     * 根据ID获取知识库。
     *
     * @param id 表示id的 {@link Long}。
     * @return 返回对应的知识库记录
     */
    @GetMapping("/repos/{id}")
    public KRepoDto getRepoById(@PathVariable("id") Long id) {
        return kRepoService.getById(id);
    }

    /**
     * 创建知识库。
     *
     * @param kRepoDto 表示知识库记录的 {@link KRepoDto}。
     */
    @PostMapping("/repos")
    public void createRepo(@RequestBody KRepoDto kRepoDto) {
        // 用户后端设置固定值
        kRepoDto.setOwnerId(1L);
        kRepoService.create(kRepoDto);
    }

    /**
     * 更新知识库
     *
     * @param kRepoDto 表示知识库记录的 {@link KRepoDto}。
     */
    @PostMapping("/repos/update")
    public void updateRepo(@RequestBody KRepoDto kRepoDto) {
        kRepoDto.setUpdatedAt(new Date(System.currentTimeMillis()));
        kRepoService.update(kRepoDto);
    }

    /**
     * 删除知识库。
     *
     * @param id 表示知识库ID的 {@link Long}。
     */
    @DeleteMapping("/repos/{id}")
    public void deleteRepo(@PathVariable("id") Long id) {
        List<KTableDto> tableDtos = kTableService.getByRepoId(id, new PageQueryParam());
        // 判断知识表是否有在任务中，如果有，则直接返回数据处理中，请稍后再试
        List<KTableDto> updatingTables = tableDtos.stream()
            .filter(kTable -> kTable.getStatus() != 0)
            .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(updatingTables)) {
            LOGGER.warn("record updating, cannot delete.");
            throw new HttpServerException("Data updating, please delete later.");
        }
        kRepoService.delete(id);
    }

    /**
     * 分页查询某个知识库下的知识表。
     *
     * @param repoId 表示知识库ID的 {@link Long}。
     * @param pageQueryParam 分页查询参数
     * @return 返回知识表列表。
     */
    @PostMapping("/repos/{repo_id}/tables/query")
    public PageResultVo getKnowledgeTables(@PathVariable("repo_id") Long repoId,
        @RequestBody PageQueryParam pageQueryParam) {
        Integer tableCount = kTableService.getTableCountByRepoId(repoId);
        List<KTableDto> kTableDtos = kTableService.getByRepoId(repoId, pageQueryParam);
        return new PageResultVo(tableCount, kTableDtos);
    }

    /**
     * 获取单个知识表
     *
     * @param id 表示知识表ID的 {@link Long}。
     * @return 返回知识表。
     */
    @PostMapping("/tables/{id}")
    public KTableDto getTable(@PathVariable("id") Long id) {
        return kTableService.getById(id);
    }

    /**
     * 创建知识表。
     *
     * @param kTableDto 表示知识表的 {@link KTableDto}
     * @return 新创建的知识表id
     */
    @PostMapping("/repos/{repo_id}/tables")
    public Long createTable(@RequestBody KTableDto kTableDto) {
        return kTableService.create(kTableDto);
    }

    /**
     * 更新知识表。
     *
     * @param kTableDto 表示知识表的 {@link KTableDto}。
     */
    @PostMapping("/tables/update/")
    public void updateTable(@RequestBody KTableDto kTableDto) {
        kTableService.update(kTableDto);
    }

    /**
     * 删除知识表。
     *
     * @param id 表示知识表ID的 {@link Long}。
     */
    @DeleteMapping("/tables/{id}")
    public void deleteTable(@PathVariable("id") Long id) {
        if (kTableService.getById(id).getStatus() != 0) {
            throw new HttpServerResponseException(HttpResponseStatus.OK, "There are unfinished uploading tasks.");
        }
        kTableService.delete(id);
    }

    // storages

    /**
     * 获取所有存储服务。
     *
     * @return 返回存储服务列表。
     */
    @GetMapping("/storages")
    public List<KStorageDto> getAllStorages() {
        return kStorageService.getAllStorages();
    }

    /**
     * 获取存储服务。
     *
     * @param id 表示存储服务的 {@link Long}。
     * @return 返回存储服务。
     */
    @GetMapping("/storages/{id}")
    public KStorageDto getStorage(@PathVariable("id") Long id) {
        return kStorageService.getById(id);
    }

    /**
     * 创建存储服务。
     *
     * @param kStorageDto 表示存储服务的 {@link KStorageDto}。
     */
    @PostMapping("/storages")
    public void createStorage(@RequestBody KStorageDto kStorageDto) {
        kStorageService.create(kStorageDto);
    }

    /**
     * 更新存储服务。
     *
     * @param id 表示存储服务的 {@link Long}。
     * @param kStorageDto 表示存储服务的 {@link KStorageDto}。
     */
    @PutMapping("/storages/{id}")
    public void updateKStorage(@PathVariable("id") Long id, @RequestBody KStorageDto kStorageDto) {
        kStorageService.update(id, kStorageDto);
    }

    /**
     * 删除存储服务。
     *
     * @param id 表示存储服务的 {@link Long}。
     */
    @DeleteMapping("/storages/{id}")
    public void deleteKStorage(@PathVariable("id") Long id) {
        kStorageService.delete(id);
    }

    /**
     * 导入文本类型知识接口
     *
     * @param fileConfigDto 文件导入配置信息
     */
    @PostMapping(path = "/import-knowledge/text")
    public void importKnowledge(@RequestBody KbGenerateConfigDto fileConfigDto) {
        kbGenerateService.importKnowledge(fileConfigDto);
    }

    /**
     * 获取向量知识信息
     *
     * @param chunkQueryDto 查询参数
     * @return 查询结果
     */
    @PostMapping(path = "/chunks")
    public PageResultVo<String> getChunk(@RequestBody KbChunkQueryDto chunkQueryDto) {
        return kbGenerateService.getChunks(chunkQueryDto);
    }

    /**
     * 导入表格类型知识接口
     *
     * @param param 表格型知识表创建参数
     * @return 表格列信息
     */
    @PostMapping(path = "/table-knowledge/columns")
    public List<TableKnowledgeColDto> getTableKnowledgeColumns(@RequestBody TableKnowledgeParam param) {
        return kbGenerateService.getTableKnowledgeColumns(param);
    }

    /**
     * 表格场景 知识表表格创建
     *
     * @param param 表格型知识表创建参数
     */
    @PostMapping(path = "/table-knowledge/construct")
    public void createTableKnowledge(@RequestBody TableKnowledgeParam param) {
        kbGenerateService.createTableKnowledge(param);
    }
}
